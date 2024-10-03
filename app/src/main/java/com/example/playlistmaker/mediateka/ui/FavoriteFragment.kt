package com.example.playlistmaker.mediateka.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoriteBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.Track.Companion.TRACK_DATA
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    private val viewModel: FavoriteViewModel by viewModel()
    private lateinit var adapter: TrackAdapter
    private var isClickAllowed = true
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavTracks()
        viewModel.favScreenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                FavScreenState.NothingInFav -> showEmpty()
                is FavScreenState.FavTracks -> showFavTracks(state.favTracks)
                is FavScreenState.NavigateToPlayer -> navigateToPlayer(
                    state.trackJsonString,
                    state.navigationHandled
                )

            }
        }
        adapter = TrackAdapter(mutableListOf()) {
            if (clickDebounce()) selectTrack(it)
        }
        binding.favoriteList.adapter = adapter
        binding.favoriteList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun selectTrack(track: Track) {
        viewModel.onTrackSelected(track)

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

    private fun showFavTracks(favoriteTracks: List<Track>) {
        binding.favNothingImage.isVisible = false
        binding.favNothingLabel.isVisible = false
        binding.favoriteList.isVisible = true
        adapter.updateTracks(favoriteTracks)
    }

    private fun showEmpty() {
        binding.favNothingImage.isVisible = true
        binding.favNothingLabel.isVisible = true
        binding.favoriteList.isVisible = false

    }

    private fun navigateToPlayer(trackJsonString: String, navigationHandled: Boolean) {
        if (!navigationHandled) {
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra(TRACK_DATA, trackJsonString)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavTracks()
    }

    companion object {
        fun newInstance(): Fragment = FavoriteFragment()
    }
}