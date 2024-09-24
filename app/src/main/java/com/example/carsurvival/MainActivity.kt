package com.example.carsurvival


//import libraries for the sensor Tilt


//to use for the sound effect
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlin.random.Random
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import android.util.Log


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


    //initialize for the odometer distance
    private var distance_meters = 0
    private lateinit var odometer: TextView

    //initialize for the coins counter
    private var coins_counter = 0
    private lateinit var coins: TextView

    //initialize the coin icon
    private lateinit var coin_icon: ImageView

    private var screenWidthh = 0

    private var speed_mode: Int = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var Location_map: LatLng? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val displayMetrics = resources.displayMetrics
        screenWidthh = displayMetrics.widthPixels

        val gameMode = intent.getStringExtra("Mode") //this the extra input from the intent from the menuActivity
        //for the selection the mode of the game ( fast, slow, sensor )




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


        //the odometer distance
        odometer = findViewById(R.id.distance)


    //to adjust the screen according to the chosen game mode
        if (gameMode == "fast"){
            speed_mode = 80
        } else if(gameMode == "slow"){
            speed_mode = 20
        } else {
            speed_mode = 35
            rightButton.visibility = View.GONE //to hide the buttons if chosen the sensor mode
            leftButton.visibility = View.GONE
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request location permissions if needed
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

        //use the handler to update the distance
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                increase_distance()
                handler.postDelayed(this, 1000) // Update distance every 1 second
            }
        }
        handler.postDelayed(runnable, 1000)



        //the coins couter
        coins = findViewById(R.id.coins_counter)


        //the coin icon
        coin_icon = findViewById(R.id.coin_icon)


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
                handleTilt(it.values)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun handleTilt(values: FloatArray) {
        val xTilt = values[0] // Tilt on the X-axis (left/right movement)
        if (xTilt > 1) { //left
            car.moveLeft()
        } else if (xTilt < -1) {
            car.moveRight() //right
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
        val obstacle_speed = speed_mode
        val time_update = 50L
        val random_value = Random(System.currentTimeMillis())
        val time_spawn_coin = 3000L


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

        handler.postDelayed(object: Runnable {
            override fun run() {
                spawn_coin(height)
                handler.postDelayed(this ,time_spawn_coin)
            }
        }, time_spawn_coin)

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
            restartGame(distance_meters)
        }
    }

    private fun notify_hit(){ //notify for the hit car ( toast msg and vibrator)
        crash_sound()
        SignalManager.getInstance().toast("Car hit")
        SignalManager.getInstance().vibrate()
    }


    //satrt the game again
    private fun restartGame(score_m: Int) {
        getLocation { location ->
            if (location != null) {
                ScoreManagerSingleton.insert_score(score_m, location) // Use the location
            } else {
                Log.d("MainActivity", "Location not available, inserting score without location.")
                ScoreManagerSingleton.insert_score(score_m, LatLng(32.0968509,34.6317778))  // Insert default location or handle it
            }

            // Proceed with restarting the game
            val intent = Intent(this, HistoryBoardActivity::class.java)
            startActivity(intent)
            finish()

            restart_game()
            handler.removeCallbacksAndMessages(null)

            // Reset game state
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


    //to play the sound effect when the car hit
    private fun crash_sound(){
        val crash_sound = MediaPlayer.create(this, R.raw.crash) //the file from raw folder
        crash_sound.start() //start the sound

        crash_sound.setOnCompletionListener { c ->
              c.release()
        }
    }

    private fun increase_distance() {
        distance_meters += 1 // Increment distance by 1 meter every second
        odometer.text = "Distance: ${distance_meters}" //update the text on the layout screen
    }

    private fun increase_coins(){
        coins_counter += 1
        coins.text = "Coins: ${coins_counter}"
    }


    private fun spawn_coin(height: Int) {
        val lane = screenWidthh / 5f
        val generated_random_lane = Random.nextInt(5) //generate lane from the first to the fifth

        coin_icon.x =  (lane / 2 + generated_random_lane * lane) - (coin_icon.width / 2)
        coin_icon.y = -coin_icon.height.toFloat()

        coin_icon.visibility = View.VISIBLE //change to visible to display the coin on the layout
        coin_icon.animate()
            .translationY(height.toFloat() + coin_icon.height)
            .setDuration(2500)
            .setUpdateListener { animation -> //to check if the coin hit the car and update the increase_coins
                if (car.isHit(coin_icon)) {
                    increase_coins()
                    coin_icon.visibility = View.GONE // hide the coin after hit
                    animation.cancel()
                }
            }
            .withEndAction {
                coin_icon.visibility = View.GONE //hide the coin after reach to the bottom
            }
            .start()
    }

    private fun getLocation(onLocationReceived: (LatLng?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    onLocationReceived(latLng) // This invokes the callback function
                } else {
                    onLocationReceived(null)  // Invokes the callback with null if no location is found
                }
            }
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }




}
