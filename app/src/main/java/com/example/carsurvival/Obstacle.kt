package com.example.carsurvival

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout

class Obstacle(private val lane: Int, private val laneWidth: Float, context: Context) {
    private val obstacleView: ImageView = ImageView(context)
    init{
        obstacleView.setImageResource(R.drawable.rock_svgrepo_com) //the icon of the obstacle
        obstacleView.layoutParams = RelativeLayout.LayoutParams(100, 100) // the size of the icon
        setPosition()
    }

    private fun setPosition() {
        obstacleView.x = laneWidth * lane + (laneWidth - 100) / 2
        obstacleView.y = -100f //set the start position of the obstacle in the top of the screen
    }

    fun check_isValid_position(height_obstacle : Int): Boolean{
        return obstacleView.y > height_obstacle
    }

    fun move(obstacle_speed: Int) {
        obstacleView.y +=  obstacle_speed

    }

    fun reset_view() {
        setPosition()
    }

    fun getView() : ImageView {
        return obstacleView
    }



}