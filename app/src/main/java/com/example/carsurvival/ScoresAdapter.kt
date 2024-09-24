package com.example.carsurvival


import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng

class ScoresAdapter(
    private val scores: List<ScoreAndLocation>,
    private val onItemClick: (LatLng) -> Unit  // Click listener to handle location update
) : RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {

    // ViewHolder for displaying each score
    class ScoreViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val textView = TextView(parent.context)
        textView.textSize = 18f
        textView.setPadding(16, 16, 16, 16)
        return ScoreViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scores[position].value
        val location = scores[position].location

        holder.textView.text = "Score: $score"
        holder.itemView.setOnClickListener {
            onItemClick(location)
        }
    }

    override fun getItemCount(): Int {
        return scores.size
    }
}
