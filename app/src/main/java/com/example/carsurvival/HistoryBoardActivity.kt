package com.example.carsurvival

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.TreeSet



import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class HistoryBoardActivity : AppCompatActivity(), OnMapReadyCallback  {

    private lateinit var scoresRecyclerView: RecyclerView
    private lateinit var scoresAdapter: ScoresAdapter
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history_board)

        scoresRecyclerView = findViewById(R.id.scores_recyclerView)
        scoresRecyclerView.layoutManager = LinearLayoutManager(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_container) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize adapter with click listener
        scoresAdapter = ScoresAdapter(ScoreManagerSingleton.scores) { location ->
            updateMapLocation(location)
        }
        scoresRecyclerView.adapter = scoresAdapter
    }

    override fun onResume() {
        super.onResume()
        scoresAdapter.notifyDataSetChanged()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun updateMapLocation(location: LatLng) {
        val zoomLevel = 15f
        map.clear()
        map.addMarker(MarkerOptions().position(location).title("Game Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }
}
