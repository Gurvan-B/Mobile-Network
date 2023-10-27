package fr.istic.mob.networkBC.Models

import android.graphics.Color

class Device(var x:Float,var y:Float) {

    private var posX: Float = x
    private var posY: Float = y
    var nom: String = ""
    private var color: Int = Color.RED
    var link: Link? = null

    fun setColor (color:Int) {
        this.color = color
    }

    fun getColor ():Int {
        return color
    }
}