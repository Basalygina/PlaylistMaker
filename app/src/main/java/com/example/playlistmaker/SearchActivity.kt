package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Track.Companion.TRACK_DATA
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {
    private var searchText: String? = null
    private val baseUrl = "https://itunes.apple.com"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesSearchApi::class.java)

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true

    private val gson = Gson()
    private val tracks = mutableListOf<Track>()
    private lateinit var adapter: TrackAdapter
    private lateinit var adapterSearchHistory: TrackAdapter
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var recyclerSearchHistory: RecyclerView
    private lateinit var searchHistory: SearchHistory

    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val searchToolbar = findViewById<Toolbar>(R.id.search_toolbar)
        val refreshButton = findViewById<TextView>(R.id.refresh_button)
        val searchHistoryLabel = findViewById<TextView>(R.id.search_history_label)
        val clearHistoryButton = findViewById<TextView>(R.id.clear_history_button)
        progressBar = findViewById(R.id.progress_bar)
        searchHistory =
            SearchHistory(getSharedPreferences(App.PM_PREFERENCES, MODE_PRIVATE))

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
                }


            }

            override fun afterTextChanged(s: Editable?) {}
        }
        adapterSearchHistory = TrackAdapter(searchHistory.searchHistoryList) {
            searchHistory.addToSearchHistory(it)
            adapterSearchHistory.notifyDataSetChanged()
            if (clickDebounce()) {startPlayerActivity(it)}
        }
        adapter = TrackAdapter(tracks) {
            searchHistory.addToSearchHistory(it)
            adapterSearchHistory.notifyDataSetChanged()
            if (clickDebounce()) {startPlayerActivity(it)}

        }

        searchEditText.setText(searchText)
        searchEditText.addTextChangedListener(textWatcher)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty() && searchHistory.searchHistoryList.isNotEmpty()) {
                searchHistoryLabel.visibility = View.VISIBLE
                recyclerSearchHistory.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                searchHistory.getSearchHistory()
                adapterSearchHistory.notifyDataSetChanged()
            }
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchEditText.clearFocus()
            searchText = EMPTY_TEXT
            hideError()
            tracks.clear()
            adapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            clearButton.visibility = View.GONE
        }

        searchToolbar.setNavigationOnClickListener{
            onBackPressed()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.getSearchHistory()
            searchHistory.searchHistoryList.clear()
            searchHistory.saveSearchHistory()
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


    }



    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
        intent.putExtra(TRACK_DATA, track)
        startActivity(intent)
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
        iTunesService.search(searchText.toString()).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                val results = response.body()?.results
                if (response.isSuccessful) {
                    Log.d(TAG, "Response code: ${response.code()}")
                    if (results?.isNotEmpty() == true) {
                        hideError()
                        hideProgressBar()
                        tracks.clear()
                        tracks.addAll(results)
                        adapter.notifyDataSetChanged()
                    } else {
                        hideProgressBar()
                        showError(
                            ERROR_NOTHING_FOUND,
                            R.drawable.error_nothing_found,
                            R.string.error_nothing_found
                        )
                        tracks.clear()
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    hideProgressBar()
                    Log.d(TAG, "Ошибка соединения: ${response.code()} - ${response.message()}")
                    showError(
                        ERROR_CONNECTION,
                        R.drawable.error_connection,
                        R.string.error_connection
                    )
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                hideProgressBar()
                Log.e(TAG, "Ошибка: ${t.message}", t)
                showError(ERROR_CONNECTION, R.drawable.error_connection, R.string.error_connection)
                tracks.clear()
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
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

