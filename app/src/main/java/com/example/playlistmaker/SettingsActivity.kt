package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val buttonBack = findViewById<ImageView>(R.id.button_back)
        buttonBack.setOnClickListener {
            val returnToMainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(returnToMainActivityIntent)
        }

    }


}