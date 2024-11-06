package com.example.playlistmaker.mediateka.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.mediateka.domain.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.mediateka.domain.Playlist.Companion.ARG_PLAYLIST_NAME

class PlaylistsFragment : Fragment() {
    private val viewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.playlistScreenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlaylistsScreenState.Empty -> showEmpty()
                is PlaylistsScreenState.Playlists -> showPlaylists(state.playlists)
            }
        }
        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(R.id.createPlaylistFragment)
        }
        adapter = PlaylistAdapter(mutableListOf()) {
            val bundle = Bundle().apply {
                putString(ARG_PLAYLIST_NAME, it.playlistName)
            }
            findNavController().navigate(R.id.playlistDetailsFragment, bundle)
        }
        binding.playlistRecycler.adapter = adapter
    }

    private fun showEmpty() {
        binding.playlistRecycler.isVisible = false
        binding.playlistsNothingImage.isVisible = true
        binding.playlistsNothingText.isVisible = true
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.playlistRecycler.isVisible = true
        binding.playlistsNothingImage.isVisible = false
        binding.playlistsNothingText.isVisible = false
        adapter.updatePlaylists(playlists)

    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    companion object {
        fun newInstance(): Fragment = PlaylistsFragment()

    }

}