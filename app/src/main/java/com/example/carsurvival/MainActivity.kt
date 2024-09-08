package com.example.carsurvival

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import android.widget.RelativeLayout
import android.os.Handler
import android.os.Looper
import com.google.android.material.imageview.ShapeableImageView
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    private lateinit var car: Car
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var first_obstacle: Obstacle
    private lateinit var second_obstacle: Obstacle
    private lateinit var third_obstacle: Obstacle
    private val hearts = mutableListOf<ImageView>()

    // Declare the main layout as a class property
    private lateinit var mainLayout: RelativeLayout

    // To keep track of which obstacles have already collided
    private var firstObstacleHit = false
    private var secondObstacleHit = false
    private var thirdObstacleHit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(this)
        // Initialize mainLayout
        mainLayout = findViewById(R.id.main)

        // Find views using findViewById
        val carView = findViewById<ImageView>(R.id.imageView4)
        val leftButton = findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton)
        val rightButton = findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton2)

        // Get screen width
        val screenWidth = resources.displayMetrics.widthPixels

        // Initialize the Car object with 3 lanes and pass the car's ImageView
        car = Car(carView, 3)

        // Define each obstacle according to its ID in the XML layout
        val obstacleView5 = findViewById<ShapeableImageView>(R.id.imageView5)
        val obstacleView6 = findViewById<ShapeableImageView>(R.id.imageView6)
        val obstacleView7 = findViewById<ShapeableImageView>(R.id.imageView7)

        // Declare each obstacle
        first_obstacle = Obstacle(obstacleView5, 0, screenWidth / 3f)
        second_obstacle = Obstacle(obstacleView6, 1, screenWidth / 3f)
        third_obstacle = Obstacle(obstacleView7, 2, screenWidth / 3f)

        // List of the three hearts
        hearts.add(findViewById(R.id.imageView))
        hearts.add(findViewById(R.id.imageView2))
        hearts.add(findViewById(R.id.imageView3))

        // Set the lane width and ensure the car is centered
        carView.post {
            car.setLaneWidth(screenWidth / 3f)
        }

        // Move the car left and right based on button clicks
        leftButton.setOnClickListener { car.moveLeft() }
        rightButton.setOnClickListener { car.moveRight() }

        // Post to ensure that the layout is drawn before using the height
        mainLayout.post {
            val layoutHeight = mainLayout.height
            start_movement(layoutHeight) // Now we ensure mainLayout is fully initialized
        }
    }

    private fun start_movement(height: Int) {
        val obstacle_speed = 35
        val time_update = 50L
        val random_value = Random(System.currentTimeMillis())

        // Move each obstacle
        handler.postDelayed({
            move(first_obstacle, obstacle_speed, height, time_update, ::firstObstacleCollisionHandler)
        }, random_value.nextLong(0, 2000))

        handler.postDelayed({
            move(second_obstacle, obstacle_speed, height, time_update, ::secondObstacleCollisionHandler)
        }, random_value.nextLong(500, 3000))

        handler.postDelayed({
            move(third_obstacle, obstacle_speed, height, time_update, ::thirdObstacleCollisionHandler)
        }, random_value.nextLong(1000, 4000))
    }

    private fun move(
        obstacle: Obstacle,
        obstacleSpeed: Int,
        height: Int,
        time_update: Long,
        collisionHandler: () -> Unit
    ) {
        handler.post(object : Runnable {
            override fun run() {
                obstacle.move(obstacleSpeed)

                // Handle collision
                collisionHandler()

                if (obstacle.checkIsValidPosition(height)) {
                    handler.postDelayed({ obstacle.reset_view() }, Random.nextLong(500, 2000))
                    handler.postDelayed(this, Random.nextLong(500, 2000))
                } else {
                    handler.postDelayed(this, time_update)
                }
            }
        })
    }

    // Collision handler for the first obstacle
    private fun firstObstacleCollisionHandler() {
        if (!firstObstacleHit && car.isHit(first_obstacle.getView())) {
            notify_hit()
            removeHeart()
            firstObstacleHit = true
        }

        // Reset flag when obstacle goes out of view
        if (first_obstacle.checkIsValidPosition(mainLayout.height)) {
            firstObstacleHit = false
        }
    }

    // Collision handler for the second obstacle
    private fun secondObstacleCollisionHandler() {
        if (!secondObstacleHit && car.isHit(second_obstacle.getView())) {
            notify_hit()
            removeHeart()
            secondObstacleHit = true
        }

        // Reset flag when obstacle goes out of view
        if (second_obstacle.checkIsValidPosition(mainLayout.height)) {
            secondObstacleHit = false
        }
    }

    // Collision handler for the third obstacle
    private fun thirdObstacleCollisionHandler() {
        if (!thirdObstacleHit && car.isHit(third_obstacle.getView())) {
            notify_hit()
            removeHeart()
            thirdObstacleHit = true
        }

        // Reset flag when obstacle goes out of view
        if (third_obstacle.checkIsValidPosition(mainLayout.height)) {
            thirdObstacleHit = false
        }
    }

    // Function to remove a heart
    private fun removeHeart() {
        if (hearts.isNotEmpty()) {
            val lastHeart = hearts.removeAt(hearts.size - 1)
            lastHeart.setImageResource(0) // Remove the heart visually
        }
        if(hearts.isEmpty()){
            restartGame()
        }
    }

    private fun notify_hit(){
        SignalManager.getInstance().toast("Car hit")
        SignalManager.getInstance().vibrate()
    }

    private fun restartGame() {
        handler.removeCallbacksAndMessages(null)

        // Reset the car and obstacles positions
        car.resetCar()
        first_obstacle.reset_view()
        second_obstacle.reset_view()
        third_obstacle.reset_view()

        // Reset collision flags
        firstObstacleHit = false
        secondObstacleHit = false
        thirdObstacleHit = false

        // Restore all hearts
        restoreHearts()

        // Start the movement again
        mainLayout.post {
            val layoutHeight = mainLayout.height
            start_movement(layoutHeight) // Restart the obstacle movement
        }
    }

    private fun restoreHearts() {
        hearts.clear()
        hearts.add(findViewById(R.id.imageView))
        hearts.add(findViewById(R.id.imageView2))
        hearts.add(findViewById(R.id.imageView3))

        for (heart in hearts) {
            heart.setImageResource(R.drawable._99063_heart_icon)
        }
    }


}
