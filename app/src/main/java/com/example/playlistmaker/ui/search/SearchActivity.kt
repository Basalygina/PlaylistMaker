package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.interactors.TracksInteractor
import com.example.playlistmaker.domain.api.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.api.repository.SelectedTrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.Track.Companion.TRACK_DATA
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {
    private var searchText: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true

    private val tracks = mutableListOf<Track>()
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var selectedTrackRepository: SelectedTrackRepository
    private lateinit var adapter: TrackAdapter
    private lateinit var adapterSearchHistory: TrackAdapter
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var recyclerSearchHistory: RecyclerView
    private lateinit var searchHistoryRepository: SearchHistoryRepository
    private lateinit var progressBar: ProgressBar

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var searchToolbar: Toolbar
    private lateinit var refreshButton: TextView
    private lateinit var searchHistoryLabel: TextView
    private lateinit var clearHistoryButton: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        tracksInteractor = Creator.provideTracksInteractor()
        selectedTrackRepository = Creator.getSelectedTrackRepository()

        searchEditText = findViewById<EditText>(R.id.search_edit_text)
        clearButton = findViewById<ImageView>(R.id.clear_button)
        searchToolbar = findViewById<Toolbar>(R.id.search_toolbar)
        refreshButton = findViewById<TextView>(R.id.refresh_button)
        searchHistoryLabel = findViewById<TextView>(R.id.search_history_label)
        clearHistoryButton = findViewById<TextView>(R.id.clear_history_button)
        progressBar = findViewById(R.id.progress_bar)

        searchHistoryRepository = SearchHistoryRepositoryImpl((application as App).sharedPref)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    clearButton.visibility = View.VISIBLE
                    searchHistoryLabel.visibility = View.GONE
                    recyclerSearchHistory.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    searchText = s.toString()
                    searchDebounce()
                } else {
                    clearSearchQuery()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        adapterSearchHistory = TrackAdapter(searchHistoryRepository.getSearchHistory())
        {
            searchHistoryRepository.addToSearchHistory(it)
            adapterSearchHistory.notifyDataSetChanged()
            if (clickDebounce()) {
                startPlayerActivity(it)
            }
        }
        adapter = TrackAdapter(tracks)
        {
            searchHistoryRepository.addToSearchHistory(it)
            adapterSearchHistory.notifyDataSetChanged()
            if (clickDebounce()) {
                startPlayerActivity(it)
            }

        }

        searchEditText.setText(searchText)
        searchEditText.addTextChangedListener(textWatcher)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty() &&
                searchHistoryRepository.getSearchHistory().isNotEmpty()
            ) {
                showSearchHistory()
            }
        }

        clearButton.setOnClickListener {
            clearSearchQuery()
            searchEditText.clearFocus()
            hideKeyboard()
        }

        searchToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryRepository.getSearchHistory()
            searchHistoryRepository.clearSearchHistory()
            searchHistoryRepository.saveSearchHistory()
            adapterSearchHistory.notifyDataSetChanged()
            searchHistoryLabel.visibility = View.GONE
            recyclerSearchHistory.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE

        }

        recyclerSearch = findViewById(R.id.search_list)
        recyclerSearch.adapter = adapter
        recyclerSearch.layoutManager = LinearLayoutManager(this)

        recyclerSearchHistory = findViewById(R.id.search_history_list)
        recyclerSearchHistory.adapter = adapterSearchHistory
        recyclerSearchHistory.layoutManager = LinearLayoutManager(this)

        refreshButton.setOnClickListener {
            search()
        }

        recyclerSearch.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {   // Скрытие клавиатуры при прокрутке вниз
                        hideKeyboard()
                    }
                }
            })

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchEditText.clearFocus()
                hideKeyboard()
                true
            } else {
                false
            }
        }

    }

    private fun startPlayerActivity(track: Track) {
        val trackJsonString = selectedTrackRepository.encodeTrackDetails(track)
        val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
        intent.putExtra(TRACK_DATA, trackJsonString)
        startActivity(intent)
    }

    private fun clearSearchQuery() {
        searchEditText.text.clear()
        searchText = EMPTY_TEXT
        tracks.clear()
        adapter.notifyDataSetChanged()
        handler.removeCallbacks(searchRunnable)
        clearButton.visibility = View.GONE
        hideError()
        if (searchHistoryRepository.getSearchHistory().isNotEmpty()) {
            showSearchHistory()
        }
    }

    private fun showSearchHistory() {
        searchHistoryLabel.visibility = View.VISIBLE
        recyclerSearchHistory.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
        searchHistoryRepository.getSearchHistory()
        adapterSearchHistory.notifyDataSetChanged()
    }

    private fun showError(errorType: String, imageResource: Int, errorMessageResource: Int) {
        val errorImage = findViewById<ImageView>(R.id.error_image)
        val errorText = findViewById<TextView>(R.id.error_text)
        errorImage.isVisible = true
        errorText.isVisible = true
        if (errorType == ERROR_CONNECTION) {
            val refreshButton = findViewById<TextView>(R.id.refresh_button)
            refreshButton.isVisible = true
        }
        errorImage.setImageResource(imageResource)
        errorText.setText(errorMessageResource)
    }

    private fun hideError() {
        val errorImage = findViewById<ImageView>(R.id.error_image)
        val errorText = findViewById<TextView>(R.id.error_text)
        val refreshButton = findViewById<TextView>(R.id.refresh_button)
        errorImage.isVisible = false
        errorText.isVisible = false
        refreshButton.isVisible = false
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun search() {
        showProgressBar()

        tracksInteractor.searchTracks(
            searchText.toString(),
            object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>) {
                    runOnUiThread {
                        hideProgressBar()
                        if (foundTracks.isNotEmpty()) {
                            hideError()
                            tracks.clear()
                            tracks.addAll(foundTracks)
                            adapter.notifyDataSetChanged()
                        } else {
                            showError(
                                ERROR_NOTHING_FOUND,
                                R.drawable.error_nothing_found,
                                R.string.error_nothing_found
                            )
                            tracks.clear()
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onError(t: Throwable) {
                    runOnUiThread {
                        hideProgressBar()
                        Log.e(TAG, "Ошибка: ${t.message}")
                        showError(
                            ERROR_CONNECTION,
                            R.drawable.error_connection,
                            R.string.error_connection
                        )
                        tracks.clear()
                        adapter.notifyDataSetChanged()
                    }
                }

            })
    }


    private fun showProgressBar() {
        progressBar.isVisible = true
        recyclerSearch.isVisible = false
        recyclerSearchHistory.isVisible = false
        hideError()

    }

    private fun hideProgressBar() {
        progressBar.isVisible = false
        recyclerSearch.isVisible = true
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
        hideError()
        super.onBackPressed()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        const val TAG = "PMtest"
        const val ERROR_NOTHING_FOUND = "ERROR_NOTHING_FOUND"
        const val ERROR_CONNECTION = "ERROR_CONNECTION"
        private const val CLICK_DEBOUNCE_DELAY = 1_000L
        private const val SEARCH_DEBOUNCE_DELAY = 2_000L
    }
}

