package fr.istic.mob.networkBC.Models

import android.graphics.Color

class Link(device1:Device) {

    var nom: String = ""

    var device1: Device = device1
    var device2: Device = device1

    var middleX: Float = device1.x
    var middleY: Float = device1.y

    var endPosX: Float = device1.x
    var endPosY: Float = device1.y

    var strokeWidth: Float = 8F

    private var color: Int = Color.RED

    fun setColor (color:Int) {
        this.color = color
    }

    fun getColor ():Int {
        return color
    }
}