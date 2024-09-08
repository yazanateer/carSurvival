package com.example.carsurvival

import android.widget.ImageView

class Obstacle(private val obstacleView: ImageView, private val lane: Int , private val laneWidth: Float) {

    init{
        obstacleView.post {
            setPosition()
        }
    }

    private fun setPosition() {
        obstacleView.x = laneWidth * lane + (laneWidth - obstacleView.width) / 2
    }

    fun checkIsValidPosition(heightObstacle : Int): Boolean{
        return obstacleView.y > heightObstacle
    }

    fun move(obstacleSpeed: Int) {
        obstacleView.y +=  obstacleSpeed
    }

    fun reset_view() { //reset the obstacle to the top of the page
        obstacleView.y = -obstacleView.height.toFloat()
        setPosition()
    }

    fun getView() : ImageView {
        return obstacleView
    }
}