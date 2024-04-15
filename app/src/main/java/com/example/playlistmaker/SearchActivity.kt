package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class SearchActivity : AppCompatActivity() {
    var searchText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val buttonBack = findViewById<ImageView>(R.id.button_back)
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
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            clearButton.visibility = View.INVISIBLE
        }

        buttonBack.setOnClickListener {
            onBackPressed()
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
        super.onBackPressed()
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val EMPTY_TEXT = ""
    }
}