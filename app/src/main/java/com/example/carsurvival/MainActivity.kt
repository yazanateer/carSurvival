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

    //define the objects here
    private lateinit var car: Car
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var first_obstacle: Obstacle
    private lateinit var second_obstacle: Obstacle
    private lateinit var third_obstacle: Obstacle
    private val hearts = mutableListOf<ImageView>()

    private lateinit var mainLayout: RelativeLayout

    //to use when the obstacle hit the car
    private var firstHit = false
    private var secondHit = false
    private var thirdHit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SignalManager.init(this) //init the signalManager file to use it for tht Toast msg and the vibration

        mainLayout = findViewById(R.id.main)

        // initial the buttons and the car according to the xml layout
        val carView = findViewById<ImageView>(R.id.CarIcon)
        val leftButton = findViewById<ExtendedFloatingActionButton>(R.id.LeftButton)
        val rightButton = findViewById<ExtendedFloatingActionButton>(R.id.RightButton)

        // get screen width
        val screenWidth = resources.displayMetrics.widthPixels

        // initialize the Car object with 3 lanes and pass the car ImageView
        car = Car(carView, 3)

        // define each obstacle according to its id in the XML layout
        val obstacleView5 = findViewById<ShapeableImageView>(R.id.FirstObstacle)
        val obstacleView6 = findViewById<ShapeableImageView>(R.id.SecondObstacle)
        val obstacleView7 = findViewById<ShapeableImageView>(R.id.ThirdObstacle)

        // declare each obstacle
        first_obstacle = Obstacle(obstacleView5, 0, screenWidth / 3f)
        second_obstacle = Obstacle(obstacleView6, 1, screenWidth / 3f)
        third_obstacle = Obstacle(obstacleView7, 2, screenWidth / 3f)

        // list the three hearts
        hearts.add(findViewById(R.id.FirstHeart))
        hearts.add(findViewById(R.id.SecondHeart))
        hearts.add(findViewById(R.id.ThirdHeart))

        // set the lane width and ensure the car is centered
        carView.post {
            car.setLaneWidth(screenWidth / 3f)
        }

        // move the car based on button clicks
        leftButton.setOnClickListener { car.moveLeft() }
        rightButton.setOnClickListener { car.moveRight() }

        // post to ensure that the layout is drawn before using the height
        mainLayout.post {
            val layoutHeight = mainLayout.height
            start_movement(layoutHeight)
        }
    }

    private fun start_movement(height: Int) { //start the movement of the obstacles on the screen
        val obstacle_speed = 35
        val time_update = 50L
        val random_value = Random(System.currentTimeMillis())

        // move each obstacle
        handler.postDelayed({
            move(first_obstacle, obstacle_speed, height, time_update, ::firstObstacleHandler)
        }, random_value.nextLong(0, 2000))

        handler.postDelayed({
            move(second_obstacle, obstacle_speed, height, time_update, ::secondObstacleHandler)
        }, random_value.nextLong(500, 3000))

        handler.postDelayed({
            move(third_obstacle, obstacle_speed, height, time_update, ::thirdObstacleHandler)
        }, random_value.nextLong(1000, 4000))
    }

    private fun move(obstacle: Obstacle, obstacleSpeed: Int, height: Int, time_update: Long, Hit_obstacles: () -> Unit) {
        handler.post(object : Runnable {
            override fun run() {
                obstacle.move(obstacleSpeed)
                Hit_obstacles()

                if (obstacle.checkIsValidPosition(height)) {
                    handler.postDelayed({ obstacle.reset_view() }, Random.nextLong(500, 2000))
                    handler.postDelayed(this, Random.nextLong(500, 2000))
                } else {
                    handler.postDelayed(this, time_update)
                }
            }
        })
    }

    //  handler hit for the three obstacles
    private fun firstObstacleHandler() {
        if (!firstHit && car.isHit(first_obstacle.getView())) {
            notify_hit()
            removeHeart()
            firstHit = true
        }
        if (first_obstacle.checkIsValidPosition(mainLayout.height)) {
            firstHit = false
        }
    }

    private fun secondObstacleHandler() {
        if (!secondHit && car.isHit(second_obstacle.getView())) {
            notify_hit()
            removeHeart()
            secondHit = true
        }
        if (second_obstacle.checkIsValidPosition(mainLayout.height)) {
            secondHit = false
        }
    }

    private fun thirdObstacleHandler() {
        if (!thirdHit && car.isHit(third_obstacle.getView())) {
            notify_hit()
            removeHeart()
            thirdHit = true
        }
        if (third_obstacle.checkIsValidPosition(mainLayout.height)) {
            thirdHit = false
        }
    }
///////////////////////////////////////////////////////////////////////////

    private fun removeHeart() {
        if (hearts.isNotEmpty()) {
            val lastHeart = hearts.removeAt(hearts.size - 1)
            lastHeart.setImageResource(0) // Remove the heart image from the screen
        }
        if(hearts.isEmpty()){ //if the three hearts removed then will start a new game
            restartGame()
        }
    }

    private fun notify_hit(){ //notify for the hit car ( toast msg and vibrator)
        SignalManager.getInstance().toast("Car hit")
        SignalManager.getInstance().vibrate()
    }


    //satrt the game again
    private fun restartGame() {
        handler.removeCallbacksAndMessages(null)

        car.resetCar()
        first_obstacle.reset_view()
        second_obstacle.reset_view()
        third_obstacle.reset_view()

        firstHit = false
        secondHit = false
        thirdHit = false

        restoreHearts()

        mainLayout.post {
            val layoutHeight = mainLayout.height
            start_movement(layoutHeight) // Restart the obstacle movement
        }
    }

    private fun restoreHearts() { //recover the three hearts to the screen
        hearts.clear()
        hearts.add(findViewById(R.id.FirstHeart))
        hearts.add(findViewById(R.id.SecondHeart))
        hearts.add(findViewById(R.id.ThirdHeart))
        for (heart in hearts) {
            heart.setImageResource(R.drawable._99063_heart_icon)
        }
    }


}
