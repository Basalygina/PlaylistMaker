package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.domain.SelectedTrackRepository
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.Track.Companion.TRACK_DATA
import com.example.playlistmaker.search.domain.TracksInteractor

class SearchActivity : AppCompatActivity() {
    private var searchText: String? = null
    private var isClickAllowed = true
    private val tracks = mutableListOf<Track>()
    private lateinit var selectedTrack: Track
    private lateinit var viewModel: SearchViewModel
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var selectedTrackRepository: SelectedTrackRepository
    private lateinit var adapter: TrackAdapter
    private lateinit var adapterSearchHistory: TrackAdapter
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        ).get(SearchViewModel::class.java)
        viewModel.searchScreenState.observe(this) { state ->
            when (state) {
                SearchScreenState.Loading -> showLoading()
                SearchScreenState.Prepared -> showPrepared()
                SearchScreenState.NothingFound -> showNothingFound()
                SearchScreenState.Error -> showError()
                is SearchScreenState.Results -> showResults(state.resultsList)
                is SearchScreenState.SearchHistory -> showSearchHistory(state.searchHistoryList)
                is SearchScreenState.NavigateToPlayer -> navigateToPlayer(state.trackJsonString)
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    binding.clearTextButton.isVisible = true
                    viewModel.searchDebounce(s.toString())
                } else {
                    clearSearchQuery()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        adapterSearchHistory = TrackAdapter(mutableListOf()) {
            selectTrack(it)
        }
        adapter = TrackAdapter(tracks) {
            selectTrack(it)
        }

        binding.searchEditText.setText(searchText)
        binding.searchEditText.addTextChangedListener(textWatcher)
        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty() ) {
                viewModel.getSearchHistory()
            }
        }

        binding.clearTextButton.setOnClickListener {
            clearSearchQuery()
            binding.searchEditText.clearFocus()
            hideKeyboard()
        }

        binding.searchToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            adapterSearchHistory.notifyDataSetChanged()
            binding.searchHistoryLabel.visibility = View.GONE
            binding.searchHistoryList.visibility = View.GONE
            binding.clearHistoryButton.visibility = View.GONE
        }

        binding.searchList.adapter = adapter
        binding.searchList.layoutManager = LinearLayoutManager(this)

        binding.searchHistoryList.adapter = adapterSearchHistory
        binding.searchHistoryList.layoutManager = LinearLayoutManager(this)

        binding.refreshButton.setOnClickListener {
            viewModel.searchDebounce(binding.searchEditText.text.toString())
        }

        binding.searchList.addOnScrollListener(
            object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {   // Скрытие клавиатуры при прокрутке вниз
                        hideKeyboard()
                    }
                }
            })

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchEditText.clearFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

    }


    private fun showNothingFound() {
        hideUnusedViews()
        binding.errorImage.isVisible = true
        binding.errorText.isVisible = true
        binding.errorImage.setImageResource(R.drawable.nothing_found)
        binding.errorText.setText(R.string.nothing_found)
    }

    private fun showPrepared() {
        hideUnusedViews()
        binding.progressBar.isVisible = false
        binding.searchList.isVisible = false
        binding.searchHistoryList.isVisible = false

    }

    private fun showLoading() {
        hideUnusedViews()
        binding.progressBar.isVisible = true
        binding.searchList.isVisible = false
        binding.searchHistoryList.isVisible = false

    }

    private fun showResults(results: List<Track>) {
        hideUnusedViews()
        binding.progressBar.isVisible = false
        binding.searchList.isVisible = true
        adapter.updateTracks(results)

    }

    private fun showSearchHistory(historyList: List<Track>) {
        hideUnusedViews()
        binding.searchHistoryLabel.visibility = View.VISIBLE
        binding.searchHistoryList.visibility = View.VISIBLE
        binding.clearHistoryButton.visibility = View.VISIBLE
        adapterSearchHistory.updateTracks(historyList)

    }


    private fun selectTrack(track: Track) {
        viewModel.onTrackSelected(track)

    }


    private fun navigateToPlayer(trackJsonString: String) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(TRACK_DATA, trackJsonString)
        startActivity(intent)
    }

    private fun clearSearchQuery() {
        hideUnusedViews()
        binding.searchEditText.text.clear()
        searchText = EMPTY_TEXT
        adapter.clearTracks()
        binding.clearTextButton.visibility = View.GONE
        viewModel.stopDelayedSearchRequest()
        viewModel.getSearchHistory()
    }


    private fun showError() {
        hideUnusedViews()
        binding.errorImage.isVisible = true
        binding.errorText.isVisible = true
        binding.refreshButton.isVisible = true
        binding.errorImage.setImageResource(R.drawable.error_connection)
        binding.errorText.setText(R.string.error_connection)
    }

    private fun hideUnusedViews() {
        binding.progressBar.isVisible = false
        binding.errorImage.isVisible = false
        binding.errorText.isVisible = false
        binding.refreshButton.isVisible = false
        binding.searchHistoryLabel.isVisible = false
        binding.clearHistoryButton.isVisible = false
        binding.searchHistoryList.isVisible = false
    }

    private fun hideKeyboard() {
        val currentFocusView = currentFocus
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_TEXT, searchText)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        searchText = savedInstanceState.getString(SEARCH_TEXT)
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() {
        searchText = EMPTY_TEXT
        tracks.clear()
        hideUnusedViews()
        super.onBackPressed()
    }


    companion object{
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        const val ERROR_NOTHING_FOUND = "ERROR_NOTHING_FOUND"
        const val ERROR_CONNECTION = "ERROR_CONNECTION"
    }
}