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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views using findViewById
        val carView = findViewById<ImageView>(R.id.imageView4)
        val leftButton = findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton)
        val rightButton = findViewById<ExtendedFloatingActionButton>(R.id.floatingActionButton2)
        val mainLayout = findViewById<RelativeLayout>(R.id.main) //the relative layout from the xml file

        // Get screen width
        val screenWidth = resources.displayMetrics.widthPixels



        // Initialize the Car object with 3 lanes and pass the car's ImageView
        car = Car(carView, 3)
        //define each obstacle according to its id in the xml layout
        val obstacleView5 = findViewById<ShapeableImageView>(R.id.imageView5)
        val obstacleView6 = findViewById<ShapeableImageView>(R.id.imageView6)
        val obstacleView7 = findViewById<ShapeableImageView>(R.id.imageView7)

        //declare each obstacle
        first_obstacle = Obstacle(obstacleView5, 0,  screenWidth / 3f)
        second_obstacle = Obstacle(obstacleView6, 1, screenWidth / 3f )
        third_obstacle = Obstacle(obstacleView7, 2,  screenWidth / 3f)



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

        //start the movement of the obstacles in random way on the scnreen
        mainLayout.post {
            val layoutHeight = mainLayout.height
            start_movement(layoutHeight)
        }
    }

    private fun start_movement(height: Int) {

        val obstacle_speed = 55
        val time_update = 50L
        val random_value = Random(System.currentTimeMillis())

        //moving each obstacle:
        handler.postDelayed({
            move(first_obstacle, obstacle_speed, height, time_update)
        }, random_value.nextLong(0, 2000)) // Random delay between 0 and 2 seconds

        // Start moving obstacle 2
        handler.postDelayed({
            move(second_obstacle, obstacle_speed, height, time_update)
        }, random_value.nextLong(500, 3000)) // Random delay between 1 and 3 seconds

        // Start moving obstacle 3
        handler.postDelayed({
            move(third_obstacle, obstacle_speed, height, time_update)
        }, random_value.nextLong(1000, 4000)) // Random delay between 2 and 4 seconds

    }

    private fun move(obstacle: Obstacle, obstacleSpeed: Int, height: Int, time_update: Long) {
    handler.post(object: Runnable {
        override fun run(){
            obstacle.move(obstacleSpeed)
            if(obstacle.checkIsValidPosition(height)){
                handler.postDelayed({obstacle.reset_view()}, Random.nextLong(500,2000))
                handler.postDelayed(this, Random.nextLong(500,2000))
            }else {
                handler.postDelayed(this, time_update)
            }
            }
    })
    }


}



