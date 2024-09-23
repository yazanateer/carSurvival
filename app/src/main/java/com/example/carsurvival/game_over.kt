package com.example.carsurvival

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class game_over : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val restart_button = findViewById<Button>(R.id.restart_button) //the button which restart the game
        restart_button.setOnClickListener{
            val intent_gameOver = Intent(this, MenuActivity::class.java)
            startActivity(intent_gameOver) //to run the game again
            finish()

        }


    }
}