package com.example.project1_game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val score = intent.getIntExtra("SCORE", 0)

        val scoreTextView = findViewById<TextView>(R.id.txtScore)
        scoreTextView.text = "Your Score: $score"

        val gifImage = findViewById<ImageView>(R.id.jewish)
        Glide.with(this)
            .asGif()
            .load(R.drawable.jewish)
            .into(gifImage)

        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}