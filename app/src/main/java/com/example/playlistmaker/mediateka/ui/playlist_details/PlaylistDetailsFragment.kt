package com.example.playlistmaker.mediateka.ui.playlist_details

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.databinding.LinearPlaylistBinding
import com.example.playlistmaker.main.ui.RootActivity
import com.example.playlistmaker.mediateka.domain.Playlist
import com.example.playlistmaker.mediateka.domain.Playlist.Companion.ARG_PLAYLIST_NAME
import com.example.playlistmaker.mediateka.ui.MediaTextFormatter
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.Track.Companion.TRACK_DATA
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragment : Fragment() {
    private val viewModel: PlaylistDetailsViewModel by viewModel()
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var playlistCardBinding: LinearPlaylistBinding
    private var currentPlaylistName: String? = null
    private var isClickAllowed = true
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        arguments?.let {
            currentPlaylistName = it.getString(ARG_PLAYLIST_NAME)
        }
        playlistCardBinding = LinearPlaylistBinding.bind(binding.playlistCard.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentActivity = activity
        if (currentActivity is RootActivity) {
            currentActivity.binding.bottomElementsGroup.isVisible = false
        }
        viewModel.setCurrentPlaylist(currentPlaylistName)
        viewModel.playlistState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SelectedPlaylistState.Content -> setupPlaylistDetails(
                    state.playlistModel,
                    state.trackList
                )

                SelectedPlaylistState.Error -> showCustomToast(getString(R.string.error_loading_playlist))
                SelectedPlaylistState.Loading -> Unit
                is SelectedPlaylistState.NavigateToPlayer -> navigateToPlayer(
                    state.trackJsonString,
                    state.navigationHandled
                )

                SelectedPlaylistState.Deleted -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                is SelectedPlaylistState.SharePlaylist -> sharePlaylist(
                    state.playlistString,
                    state.isSharingPossible
                )
            }
        }

        binding.playlistToolbar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        adapter = TrackAdapter(mutableListOf(),
            clickListener = {
                if (clickDebounce()) selectTrack(it)
            },
            longClickListener = {
                deleteTrackFromPlaylistDialog(currentPlaylistName!!, it)

            }
        )

        binding.tracksRecycler.adapter = adapter

        val bottomSheetTracks =
            BottomSheetBehavior.from(view.findViewById(R.id.bottom_sheet_container))
        val bottomSheetMenu = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetMenu.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (_binding == null) return
                    bottomSheetTracks.isDraggable =
                        newState != BottomSheetBehavior.STATE_EXPANDED
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                        }

                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                        }
                    }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding == null) return
                binding.overlay.alpha = slideOffset
            }
        })

        binding.shareButton.setOnClickListener {
            viewModel.sharePlaylist(currentPlaylistName!!)
        }

        binding.menuButton.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.menuDeletePlaylist.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_COLLAPSED
            deletePlaylistDialog(currentPlaylistName!!)
        }

        binding.menuShare.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.sharePlaylist(currentPlaylistName!!)
        }


        binding.menuEditInfo.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_PLAYLIST_NAME, currentPlaylistName)
            }
            findNavController().navigate(R.id.editPlaylistFragment, bundle, null, null)
        }

    }

    private fun sharePlaylist(playlistString: String, isSharingPossible: Boolean) {
        if (isSharingPossible) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, playlistString)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        } else {
            showCustomToast(getString(R.string.nothing_to_share))
        }
    }

    private fun deletePlaylistDialog(playlistName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_playlist_dialog_message, playlistName))
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { dialog, _ ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deletePlaylist(playlistName)
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteTrackFromPlaylistDialog(playlistName: String, track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.delete_track_dialog_message)
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { dialog, _ ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.deleteTrackFromPlaylist(playlistName, track)

                }
                dialog.dismiss()
            }
            .show()

    }

    private fun navigateToPlayer(trackJsonString: String, navigationHandled: Boolean) {
        if (!navigationHandled) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra(TRACK_DATA, trackJsonString)
            startActivity(intent)
        }
    }

    private fun selectTrack(track: Track) {
        viewModel.onTrackSelected(track)
    }

    private fun setupPlaylistDetails(playlist: Playlist, tracks: List<Track>) {
        val totalMinutes = (playlist.durationSum / 1000) / 60
        with(binding){
            if (tracks.size > 0) {
                tracksNothingImage.isVisible = false
                tracksNothingText.isVisible = false
            } else {
                tracksNothingImage.isVisible = true
                tracksNothingText.isVisible = true
            }
            playlistName.text = playlist.playlistName
            playlistDescription.text = playlist.description
            playlistDuration.text =
                "$totalMinutes " + MediaTextFormatter.getMinutesDeclension(totalMinutes.toInt())
            tracksCount.text =
                playlist.tracksCount.toString() + " " + MediaTextFormatter.getTrackDeclension(playlist.tracksCount)
        }

        if (playlist.cover != null) {
            Glide.with(this)
                .load(playlist.cover)
                .placeholder(R.drawable.placeholder_large)
                .centerCrop()
                .into(binding.playlistCover)
        } else {
            binding.playlistCover.setImageResource(R.drawable.placeholder_large)
        }

        //настройка отоброжения вспомогательного меню
        playlistCardBinding.playlistName.text = playlist.playlistName
        playlistCardBinding.tracksCount.text =
            playlist.tracksCount.toString() + " " + MediaTextFormatter.getTrackDeclension(playlist.tracksCount)
        Glide.with(this)
            .load(playlist.cover)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(playlistCardBinding.coverImage)

        adapter.updateTracks(tracks)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun showCustomToast(message: String) {
        val layout = layoutInflater.inflate(R.layout.playlist_toast, null)
        val toastTextView = layout.findViewById<TextView>(R.id.toast_text)
        toastTextView.text = message
        Toast(requireContext())
            .apply {
                duration = Toast.LENGTH_SHORT
                view = layout
                setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 100)
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        val currentActivity = activity
        if (currentActivity is RootActivity) {
            currentActivity.binding.bottomElementsGroup.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val currentActivity = activity
        if (currentActivity is RootActivity) {
            currentActivity.binding.bottomElementsGroup.isVisible = true
        }
        _binding = null
    }

}


