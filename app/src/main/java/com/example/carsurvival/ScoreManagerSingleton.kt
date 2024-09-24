package com.example.carsurvival
import android.util.Log
import com.google.android.gms.maps.model.LatLng
data class ScoreAndLocation(val value: Int, val location: LatLng)

object ScoreManagerSingleton {

    var scores: MutableList<ScoreAndLocation> = mutableListOf()
    fun insert_score(score: Int, location: LatLng) {
        scores.add(ScoreAndLocation(score, location))
        if (scores.size > 10) {
            scores.sortByDescending { it.value } // Sort by score in descending order
            scores = scores.take(10).toMutableList() // Take the top 10 scores
        }
        }
    }
