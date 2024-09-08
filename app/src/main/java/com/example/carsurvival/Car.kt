package com.example.carsurvival

import android.graphics.Rect
import android.view.View
import android.widget.ImageView



                ////////////| The position: Left - 0  | Center - 1 | Right - 2 |\\\\\\\\\\\\\\\


class Car(private val carView: ImageView, private val laneCount: Int) {

    private var currentLane: Int = 1 //the default position start in center
    private var laneWidth: Float = 0f // each lane in the screen ( 3 lanes )

    fun resetCar() {
        currentLane = 1 //the car start in the center lane
        updatePosition()
    }
    fun moveLeft() { //move the car one lane left
        if (currentLane > 0) {
            currentLane--
            updatePosition()
        }
    }

    fun moveRight() {//move the car one lane right
        if (currentLane < laneCount - 1) {
            currentLane++
            updatePosition()
        }
    }

    fun updatePosition() { //update the car position
        carView.x = laneWidth * currentLane + (laneWidth - carView.width) / 2
    }

    fun setLaneWidth(laneWidth: Float) { // to use in the update position function
        this.laneWidth = laneWidth
        updatePosition()
    }

    fun isHit(objectView: View): Boolean { // Check if the car crashed with the obstacles
        val carRect = Rect()
        carView.getHitRect(carRect)
        val objectRect = Rect()
        objectView.getHitRect(objectRect)
        return Rect.intersects(carRect, objectRect)
    }

    fun getView(): ImageView { //return the car view
        return carView
    }
}