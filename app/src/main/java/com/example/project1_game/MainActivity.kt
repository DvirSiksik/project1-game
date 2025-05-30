package com.example.project1_game

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import manager.GameManager

class MainActivity : AppCompatActivity() {

    private lateinit var gameManager: GameManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val useSensorMode = intent.getBooleanExtra("USE_SENSOR_MODE", false)
        val isFastMode = intent.getBooleanExtra("FAST_MODE", false)


        gameManager = GameManager(this, useSensorMode, isFastMode)
        gameManager.initGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameManager.cleanup()
    }
}