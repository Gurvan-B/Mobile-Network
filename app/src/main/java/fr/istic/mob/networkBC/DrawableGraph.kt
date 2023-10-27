package fr.istic.mob.networkBC

import android.graphics.*
import fr.istic.mob.networkBC.Models.Graph
import android.graphics.Path
import android.util.Log


class DrawableGraph() : android.graphics.drawable.Drawable() {

    /*private val redPaint: Paint = Paint().apply {
        color = Color.RED
    }*/
    private val textPaint: Paint = Paint().apply {
        style= Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 20F
        textAlign = Paint.Align.CENTER
    }
    private val linePaint: Paint = Paint().apply {

        style = Paint.Style.STROKE
        strokeWidth = 8F
    }



    override fun draw(canvas: Canvas) {
        val radius = 40F
        canvas.drawColor(0)

        for (link in Graph.mainGraph.links) {
            val path: Path = Path()

            path.moveTo(link.device1.x, link.device1.y)

            if (link.device1 != link.device2) {
                //canvas.drawLine(link.device1.x, link.device1.y, link.device2.x, link.device2.y,linePaint)
                //pathMeasure.length
                path.quadTo(link.middleX, link.middleY, link.device2.x, link.device2.y)
            } else {
                //canvas.drawLine(link.device1.x, link.device1.y, link.endPosX, link.endPosY,linePaint)
                path.quadTo((link.device1.x + link.endPosX ) / 2, (link.device1.y + link.endPosY ) / 2, link.endPosX, link.endPosY)
            }

            canvas.drawPath(path, linePaint.apply { color = link.getColor(); strokeWidth = link.strokeWidth })
            if (link.nom != "") canvas.drawText(link.nom,link.middleX,link.middleY+10F,textPaint)
        }

        //Log.d("device",mainGraph.toString())
        for (device in Graph.mainGraph.devices) {
            canvas.drawCircle(device.x, device.y, radius, Paint().apply { color = device.getColor()})
            if (device.nom != "") canvas.drawText(device.nom,device.x, device.y+10F,textPaint)
        }



    }

    override fun setAlpha(p0: Int) {
        //TODO("Not yet implemented")
        return
    }

    override fun setColorFilter(p0: ColorFilter?) {
        //TODO("Not yet implemented")
        return
    }

    override fun getOpacity(): Int {
        //TODO("Not yet implemented")
        return PixelFormat.OPAQUE
    }
}