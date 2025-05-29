package com.example.project1_game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import manager.HighScore

class HighScoreAdapter(
    private val scores: List<HighScore>,
    private val onItemClick: (HighScore) -> Unit
) : RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtScore: TextView = view.findViewById(R.id.txtScore)
        val txtLocation: TextView = view.findViewById(R.id.txtLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_high_score, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = scores[position]
        holder.txtScore.text = "Score: ${score.score}"
        holder.txtLocation.text = "Lat: ${score.latitude ?: "N/A"}, Lng: ${score.longitude ?: "N/A"}"


        holder.itemView.setOnClickListener {
            onItemClick(score)
        }
    }

    override fun getItemCount(): Int = scores.size
}