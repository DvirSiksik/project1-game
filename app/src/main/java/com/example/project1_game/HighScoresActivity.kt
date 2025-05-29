package com.example.project1_game

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import manager.HighScore

class HighScoresActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mapView: MapView
    private lateinit var btnBack: ImageButton
    private lateinit var highScores: List<HighScore>
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        recyclerView = findViewById(R.id.recyclerView)
        mapView = findViewById(R.id.mapView)
        btnBack = findViewById(R.id.btnBack)

        highScores = getHighScores()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HighScoreAdapter(highScores) { score ->
            val lat = score.latitude
            val lng = score.longitude
            if (lat != null && lng != null) {
                val location = LatLng(lat, lng)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getHighScores(): List<HighScore> {
        val sharedPref = getSharedPreferences("high_scores", MODE_PRIVATE)
        val json = sharedPref.getString("score_list", null) ?: return emptyList()
        val type = object : TypeToken<List<HighScore>>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        var firstLocation: LatLng? = null

        for (score in highScores) {
            val lat = score.latitude
            val lng = score.longitude
            if (lat != null && lng != null) {
                val location = LatLng(lat, lng)
                map?.addMarker(MarkerOptions().position(location).title("Score: ${score.score}"))

                if (firstLocation == null) {
                    firstLocation = location
                }
            }
        }

        firstLocation?.let {
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 5f))
        } ?: Toast.makeText(this, "No locations available to display", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}