package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    var searchText: String? = null
    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesSearchApi::class.java)
    private val tracks = ArrayList<Track>()
    private lateinit var recycler: RecyclerView
    private var adapter = TrackAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val buttonBack = findViewById<ImageView>(R.id.button_back)
        val refreshButton = findViewById<TextView>(R.id.refresh_button)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotBlank() == true) {
                    clearButton.visibility = View.VISIBLE
                }
                searchText = searchEditText.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchEditText.setText(searchText)
        searchEditText.addTextChangedListener(textWatcher)
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

        recycler = findViewById<RecyclerView>(R.id.searchList)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)


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
                when (response.code()) {
                    200 -> {
                        Log.d(Companion.TAG, "Response code: ${response.code()}")
                        if (response.body()?.results?.isNotEmpty() == true) {
                            hideError()
                            tracks.clear()
                            tracks.addAll(response.body()?.results!!)
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

                    else -> onFailure(call, Throwable("Response code ${response.code()}"))
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

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
        const val TAG = "PMtest"
        const val ERROR_NOTHING_FOUND = "ERROR_NOTHING_FOUND"
        const val ERROR_CONNECTION = "ERROR_CONNECTION"
    }
}