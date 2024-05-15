package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App.Companion.SEARCH_HISTORY_KEY
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    var searchText: String? = null
    private val gson = Gson()
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesSearchApi::class.java)
    private val tracks = mutableListOf<Track>()
    private val searchHistory = mutableListOf<Track>()
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var recyclerSearchHistory: RecyclerView
    private var adapter = TrackAdapter(tracks) {
            addToSearchHistory(it)
    }
    private var adapterSearchHistory = TrackAdapter(searchHistory) {
        Toast.makeText(
            applicationContext,
            "Просмотр трека из истории",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun addToSearchHistory(selectedTrack: Track) {
        val searchHistoryMaxSize = 10
        getSearchHistory()
        if (searchHistory.contains(selectedTrack)) {
            searchHistory.remove(selectedTrack)
        } else {
            if (searchHistory.size == searchHistoryMaxSize) {
                searchHistory.removeAt(searchHistoryMaxSize - 1)
            }
        }
        searchHistory.add(0, selectedTrack)
        adapterSearchHistory.notifyDataSetChanged()
        saveSearchHistory()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val buttonBack = findViewById<ImageView>(R.id.button_back)
        val refreshButton = findViewById<TextView>(R.id.refresh_button)
        val searchHistoryLabel = findViewById<TextView>(R.id.search_history_label)
        val searchHistoryList = findViewById<RecyclerView>(R.id.search_history_list)
        val clearHistoryButton = findViewById<TextView>(R.id.clear_history_button)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotBlank() == true) {
                    clearButton.visibility = View.VISIBLE
                    searchHistoryLabel.visibility = View.GONE
                    searchHistoryList.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
                searchText = searchEditText.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchEditText.setText(searchText)
        searchEditText.addTextChangedListener(textWatcher)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                searchHistoryLabel.visibility = View.VISIBLE
                searchHistoryList.visibility = View.VISIBLE
                clearHistoryButton.visibility = View.VISIBLE
                getSearchHistory()
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
            clearButton.visibility = View.INVISIBLE
        }

        buttonBack.setOnClickListener {
            onBackPressed()
        }

        clearHistoryButton.setOnClickListener {
            getSearchHistory()
            searchHistory.clear()
            saveSearchHistory()
            adapterSearchHistory.notifyDataSetChanged()
        }

        recyclerSearch = findViewById(R.id.search_list)
        recyclerSearch.adapter = adapter
        recyclerSearch.layoutManager = LinearLayoutManager(this)

        recyclerSearchHistory = findViewById(R.id.search_history_list)
        recyclerSearchHistory.adapter = adapterSearchHistory
        recyclerSearchHistory.layoutManager = LinearLayoutManager(this)


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(searchEditText.text.toString())
                true
            }
            false
        }
        refreshButton.setOnClickListener {
            search(searchEditText.text.toString())
        }

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

    private fun search(query: String) {
        iTunesService.search(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                val results = response.body()?.results
                if (response.isSuccessful) {
                    Log.d(Companion.TAG, "Response code: ${response.code()}")
                    if (results != null) {
                        hideError()
                        tracks.clear()
                        tracks.addAll(results)
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
                } else {
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
                showError(ERROR_CONNECTION, R.drawable.error_connection, R.string.error_connection)
                tracks.clear()
                adapter.notifyDataSetChanged()
            }

        })
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

    fun parseJson(jsonString: String): MutableList<Track> {
        try {
            // Если JSON это массив
            val tracks = gson.fromJson(jsonString, Array<Track>::class.java).toMutableList()
            return tracks
        } catch (e: JsonSyntaxException) {
            // Если JSON это объект
            val track = gson.fromJson(jsonString, Track::class.java)
            return mutableListOf(track)
        }

    }

    private fun getSearchHistory(): MutableList<Track> {
        val sharedPref = getSharedPreferences(App.PM_PREFERENCES, MODE_PRIVATE)
        var json = sharedPref.getString(SEARCH_HISTORY_KEY, null)
        if (json != null) {
            searchHistory.clear()
            searchHistory.addAll(parseJson(json))
        }
        return searchHistory
    }

    private fun saveSearchHistory(){
        val sharedPref = getSharedPreferences(App.PM_PREFERENCES, MODE_PRIVATE)
        val json = gson.toJson(searchHistory)
        sharedPref.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        const val TAG = "PMtest"
        const val ERROR_NOTHING_FOUND = "ERROR_NOTHING_FOUND"
        const val ERROR_CONNECTION = "ERROR_CONNECTION"
    }
}