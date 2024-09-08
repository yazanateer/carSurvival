package com.example.carsurvival

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import android.widget.RelativeLayout
import android.os.Handler
import android.os.Looper
import kotlin.random.Random
class MainActivity : AppCompatActivity() {

    private lateinit var car: Car
//the obstacles:

    private val obstacleList = mutableListOf<Obstacle>() // List to hold obstacles
    private lateinit var mainLayout: RelativeLayout
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main)
        // Find views using findViewById
        val carView = findViewById<ImageView>(R.id.imageView4)
        val leftButton = findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton)
        val rightButton = findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton2)

        // Get screen width
        val screenWidth = resources.displayMetrics.widthPixels














        // Initialize the Car object with 3 lanes and pass the car's ImageView
        car = Car(carView, 3)



        // Set the lane width (dividing screen width into 3 lanes) - and we use the .post to set the car in the center when we run the app
        carView.post {
            car.setLaneWidth(screenWidth / 3f)
        }
        // Move the car left when the left button is clicked
        leftButton.setOnClickListener {
            car.moveLeft()
        }

        // Move the car right when the right button is clicked
        rightButton.setOnClickListener {
            car.moveRight()
        }


        // Add obstacles
        addObstacles(screenWidth)

        // Start moving the obstacles
        startObstacleMovement()
    }

    private fun addObstacles(screenWidth: Int) {
        // Add obstacles to each lane
        addObstacleInLane(0, screenWidth) // Left lane
        addObstacleInLane(1, screenWidth) // Center lane
        addObstacleInLane(2, screenWidth) // Right lane
    }

    private fun addObstacleInLane(lane: Int, screenWidth: Int) {
        // Create and position an obstacle in the given lane
        val obstacle = Obstacle( lane, screenWidth / 3f, this)
        mainLayout.addView(obstacle.getView()) // Add the obstacle to the main layout
        obstacleList.add(obstacle) // Add to list for managing movement
    }

    private fun startObstacleMovement() {
        // Handler to move the obstacles down the screen at regular intervals
        val speed = 10 // Speed of obstacle movement
        val updateInterval = 50L // Time interval (in milliseconds)

        handler.post(object : Runnable {
            override fun run() {
                for (obstacle in obstacleList) {
                    // Move each obstacle down
                    obstacle.move(speed)

                    // Check if the obstacle is out of the screen
                    if (obstacle.check_isValid_position(mainLayout.height)) {
                        obstacle.reset_view() // Reset position if out of view
                    }

                    // Optional: Collision detection (can be implemented later)
                }

                // Repeat the obstacle movement
                handler.postDelayed(this, updateInterval)
            }
        })
    }



    }

