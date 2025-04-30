package com.example.project1_game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val gifImage = findViewById<ImageView>(R.id.jewish)
        Glide.with(this)
            .asGif()
            .load(R.drawable.jewish)
            .into(gifImage)

        val playButton = findViewById<Button>(R.id.start_btn_play)
        playButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}