package com.example.carsurvival

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val fast_button = findViewById<Button>(R.id.button_fast)
        val slow_button = findViewById<Button>(R.id.button_slow)
        val sensor_mode = findViewById<Button>(R.id.sensor_mode)

        fast_button.setOnClickListener {
            val fast_button_intent = Intent(this, MainActivity::class.java)
            fast_button_intent.putExtra("Mode", "fast")
            startActivity(fast_button_intent)
            finish()
        }



        slow_button.setOnClickListener {
            val slow_button_intent = Intent(this, MainActivity::class.java)
            slow_button_intent.putExtra("Mode", "slow")
            startActivity(slow_button_intent)
            finish()
        }



        sensor_mode.setOnClickListener {
            val sensor_mode_intent = Intent(this, MainActivity::class.java)
            sensor_mode_intent.putExtra("Mode", "sensor")
            startActivity(sensor_mode_intent)
            finish()
        }




    }
}