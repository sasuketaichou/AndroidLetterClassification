package com.japri.amierul.letterclassaj.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Created by Hexa-Amierul.Japri on 19/3/2018.
 */
class DrawRenderer{

    companion object {
        fun renderModel(canvas: Canvas,model: DrawModel,paint: Paint,startIndex:Int){

            paint.isAntiAlias = true

            val lineSize:Int = model.getLineSize()

            for (i in startIndex until lineSize){
                val line:DrawModel.Line = model.getLine(i)
                paint.color = Color.BLACK

                val elemSize:Int = line.getElemSize()
                if(elemSize < 1){
                    continue
                }

                var elem:DrawModel.LineElem = line.getElem(0)
                var lastX = elem.x
                var lastY = elem.y

                for (j in 0 until elemSize){
                    elem = line.getElem(j)
                    val x = elem.x
                    val y = elem.y

                    canvas.drawLine(lastX,lastY,x,y,paint)
                    lastX = x
                    lastY = y

                }

            }
        }
    }
}