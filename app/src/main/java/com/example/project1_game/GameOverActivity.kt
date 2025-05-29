package com.example.project1_game

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import manager.HighScore

class GameOverActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    private var score: Int = 0
    private var alreadySaved = false
    private var pendingGoToScores = false

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        score = intent.getIntExtra("SCORE", 0)
        val useSensor = intent.getBooleanExtra("USE_SENSOR_MODE", false)

        findViewById<TextView>(R.id.txtScore).text = "Your Score: $score"
        Glide.with(this).asGif().load(R.drawable.player).into(findViewById(R.id.player))

        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("USE_SENSOR_MODE", useSensor)
            })
            finish()
        }

        findViewById<Button>(R.id.btnMainMenu).setOnClickListener {
            startActivity(Intent(this, StartActivity::class.java).apply {
                putExtra("USE_SENSOR_MODE", useSensor)
            })
            finish()
        }

        findViewById<Button>(R.id.btnHighScores).setOnClickListener {
            pendingGoToScores = true
            requestLocationPermissionAndFetch()
        }


        requestLocationPermissionAndFetch()
    }

    private fun requestLocationPermissionAndFetch() {
        val fine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            fetchLastLocationAndSave()
        }
    }

    private fun fetchLastLocationAndSave() {
        Log.d("GameOverActivity", "Trying to get location...")

        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("GameOverActivity", "Location permissions are missing!")
            saveHighScore()
            return
        }

        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 1000
            numUpdates = 1
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        Log.d("GameOverActivity", "Got location: ${lastLocation!!.latitude}, ${lastLocation!!.longitude}")
                    } else {
                        Log.w("GameOverActivity", "Location is null (maybe emulator?)")
                    }
                    saveHighScore()
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun saveHighScore() {
        if (alreadySaved) {
            Log.d("GameOverActivity", "High score already saved, skipping.")
            return
        }
        alreadySaved = true

        val highScore = HighScore(
            score = score,
            timestamp = System.currentTimeMillis(),
            latitude = lastLocation?.latitude,
            longitude = lastLocation?.longitude
        )

        val sharedPref = getSharedPreferences("high_scores", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<HighScore>>() {}.type
        val scoreList: MutableList<HighScore> = sharedPref.getString("score_list", null)?.let {
            gson.fromJson(it, type)
        } ?: mutableListOf()

        scoreList.add(highScore)
        val updated = gson.toJson(scoreList.sortedByDescending { it.score }.take(10))
        sharedPref.edit().putString("score_list", updated).apply()

        Log.d("GameOverActivity", "High score saved: $highScore")

        if (pendingGoToScores) {
            startActivity(Intent(this, HighScoresActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            fetchLastLocationAndSave()
        }
    }
}