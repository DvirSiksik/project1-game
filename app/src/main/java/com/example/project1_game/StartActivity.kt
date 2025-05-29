package com.example.project1_game

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val gifImage = findViewById<ImageView>(R.id.player)
        Glide.with(this)
            .asGif()
            .load(R.drawable.player)
            .into(gifImage)

        val checkboxSensor = findViewById<CheckBox>(R.id.checkbox_sensor)
        val checkboxNormal = findViewById<CheckBox>(R.id.checkbox_speed_normal)
        val checkboxFast = findViewById<CheckBox>(R.id.checkbox_speed_fast)

        val startButton = findViewById<Button>(R.id.start_btn)
        val highScoresButton = findViewById<Button>(R.id.high_scores_btn)


        val useSensorFromIntent = intent.getBooleanExtra("USE_SENSOR_MODE", false)
        checkboxSensor.isChecked = useSensorFromIntent


        checkboxNormal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkboxFast.isChecked = false
        }

        checkboxFast.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) checkboxNormal.isChecked = false
        }

        startButton.setOnClickListener {
            val useSensor = checkboxSensor.isChecked
            val isFastMode = checkboxFast.isChecked
            startGame(useSensor, isFastMode)
        }

        highScoresButton.setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGame(useSensor: Boolean, isFast: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USE_SENSOR_MODE", useSensor)
        intent.putExtra("FAST_MODE", isFast)
        startActivity(intent)
        finish()
    }
}