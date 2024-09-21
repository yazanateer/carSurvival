package com.example.carsurvival

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import android.widget.RelativeLayout
import android.os.Handler
import android.os.Looper
import com.google.android.material.imageview.ShapeableImageView
import kotlin.random.Random





//import libraries for the sensor Tilt
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Button

class MainActivity : AppCompatActivity(), SensorEventListener  {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    //define the objects here
    private lateinit var car: Car
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var first_obstacle: Obstacle
    private lateinit var second_obstacle: Obstacle
    private lateinit var third_obstacle: Obstacle

    //adding new two obstacles
    private lateinit var fourth_obstacle: Obstacle
    private lateinit var fifth_obstacle: Obstacle
    private val hearts = mutableListOf<ImageView>()

    private lateinit var mainLayout: RelativeLayout

    //to use when the obstacle hit the car
    private var firstHit = false
    private var secondHit = false
    private var thirdHit = false
    //adding the new two hits ( obstacles )
    private var fourthHit = false
    private var fifthHit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//setups for the sensor accelerator
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)




        SignalManager.init(this) //init the signalManager file to use it for tht Toast msg and the vibration

        mainLayout = findViewById(R.id.main)

        // initial the buttons and the car according to the xml layout
        val carView = findViewById<ImageView>(R.id.CarIcon)
        val leftButton = findViewById<ExtendedFloatingActionButton>(R.id.LeftButton)
        val rightButton = findViewById<ExtendedFloatingActionButton>(R.id.RightButton)

        // get screen width
        val screenWidth = resources.displayMetrics.widthPixels

        // initialize the Car object with 3 lanes and pass the car ImageView
        car = Car(carView, 5)

        // define each obstacle according to its id in the XML layout
        val obstacleView5 = findViewById<ShapeableImageView>(R.id.FirstObstacle)
        val obstacleView6 = findViewById<ShapeableImageView>(R.id.SecondObstacle)
        val obstacleView7 = findViewById<ShapeableImageView>(R.id.ThirdObstacle)
        //adding new two views obstacles
        val obstacleView8 = findViewById<ShapeableImageView>(R.id.FourthObstacle)
        val obstacleView9 = findViewById<ShapeableImageView>(R.id.FifthObstacle)


        // declare each obstacle
        first_obstacle = Obstacle(obstacleView5, 0, screenWidth / 5f)
        second_obstacle = Obstacle(obstacleView6, 1, screenWidth / 5f)
        third_obstacle = Obstacle(obstacleView7, 2, screenWidth / 5f)
        //adding the new two - and update the screen width/ 5f instead of 3f because now 5 lanes
        fourth_obstacle = Obstacle(obstacleView8, 3, screenWidth / 5f)
        fifth_obstacle = Obstacle(obstacleView9, 4, screenWidth / 5f)

        // list the three hearts
        hearts.add(findViewById(R.id.FirstHeart))
        hearts.add(findViewById(R.id.SecondHeart))
        hearts.add(findViewById(R.id.ThirdHeart))

        // set the lane width and ensure the car is centered
        carView.post {
            car.setLaneWidth(screenWidth / 5f)
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


    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                handleTilt(it.values[0])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Can be left empty
    }

    private fun handleTilt(x: Float) {
        if (x < -2) { // Tilt to the left
            car.moveLeft()
        } else if (x > 2) { // Tilt to the right
            car.moveRight()
        }
    }



    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
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

        handler.postDelayed({
            move(fourth_obstacle, obstacle_speed, height, time_update, ::fourthObstacleHandler)
        }, random_value.nextLong(1500, 5000))

        handler.postDelayed({
            move(fifth_obstacle, obstacle_speed, height, time_update, ::fifthObstacleHandler)
        }, random_value.nextLong(2000, 6000))
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

    private fun fourthObstacleHandler() {
        if (!fourthHit && car.isHit(fourth_obstacle.getView())) {
            notify_hit()
            removeHeart()
            fourthHit = true
        }
        if (fourth_obstacle.checkIsValidPosition(mainLayout.height)) {
            fourthHit = false
        }
    }

    private fun fifthObstacleHandler() {
        if (!fifthHit && car.isHit(fifth_obstacle.getView())) {
            notify_hit()
            removeHeart()
            fifthHit = true
        }
        if (fifth_obstacle.checkIsValidPosition(mainLayout.height)) {
            fifthHit = false
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
        restart_game()
        handler.removeCallbacksAndMessages(null)

        car.resetCar()
        first_obstacle.reset_view()
        second_obstacle.reset_view()
        third_obstacle.reset_view()
        fourth_obstacle.reset_view()
        fifth_obstacle.reset_view()

        firstHit = false
        secondHit = false
        thirdHit = false
        fourthHit = false
        fifthHit = false

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

    private fun restart_game() {
        val intent = Intent(this, game_over::class.java)
        startActivity(intent)
        finish()
    }




}
