package com.example.carsurvival

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

// Data class that holds both score and location

class ScoresAdapter(
    private val scores: List<ScoreAndLocation>, // Now we use ScoreAndLocation
    private val onItemClick: (LatLng) -> Unit  // Click listener to handle location update
) : RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {

    // ViewHolder for displaying each score
    class ScoreViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    // Inflate the item layout for each score
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val textView = TextView(parent.context)
        textView.textSize = 18f
        textView.setPadding(16, 16, 16, 16)
        return ScoreViewHolder(textView)
    }

    // Bind each score to the view and set the click listener to update the map
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position].value
        val location = scores[position].location

        holder.textView.text = "Score: $score"

        // Handle item click and pass the location to the listener
        holder.itemView.setOnClickListener {
            onItemClick(location)
        }
    }

    // Return the total number of items in the list
    override fun getItemCount(): Int {
        return scores.size
    }
}
