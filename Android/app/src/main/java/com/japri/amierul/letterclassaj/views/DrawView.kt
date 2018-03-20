package com.japri.amierul.letterclassaj.views

import android.content.Context
import android.graphics.*
import android.view.View
import android.util.AttributeSet

/**
 * Created by Hexa-Amierul.Japri on 19/3/2018.
 */

class DrawView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet){

    private val mPaint:Paint = Paint()
    private var mModel:DrawModel? = null

    private var mOffscreenBitmap:Bitmap? = null
    private var mOffscreenCanvas: Canvas? = null

    private val mMatrix: Matrix = Matrix()
    private val mInvMatrix: Matrix = Matrix()

    private var mDrawnLineSize = 0
    private var mSetup = false
    private val mTmpPoints = FloatArray(2)

    fun setModel(model:DrawModel){
        mModel = model
    }

    private fun setup(){

        //Model bitmap size
        val modelWidth:Float = mModel!!.width.toFloat()
        val modelHeight = mModel!!.height.toFloat()

        val scaleW = width/modelWidth
        val scaleH = height/modelHeight

        var scale = scaleW
        if(scale>scaleH){
            scale = scaleH
        }

        val newCx = modelWidth * scale / 2
        val newCy = modelHeight * scale / 2
        val dx = width/2 -newCx
        val dy = height/2 - newCy

        mMatrix.setScale(scale,scale)
        mMatrix.postTranslate(dx,dy)
        mMatrix.invert(mInvMatrix)
        mSetup = true

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(mModel == null){
            return
        }

        if (!mSetup){
            setup()
        }

        var startIndex = mDrawnLineSize - 1
        if(startIndex<0){
            startIndex = 0
        }


        DrawRenderer.Companion.renderModel(mOffscreenCanvas!!, mModel!!, mPaint, startIndex)
        canvas!!.drawBitmap(mOffscreenBitmap,mMatrix,mPaint)

        mDrawnLineSize = mModel!!.getLineSize()

    }

    fun calcPos(x:Float,y:Float,out:PointF){

        mTmpPoints[0] = x
        mTmpPoints[1] = y
        mInvMatrix.mapPoints(mTmpPoints)
        out.x = mTmpPoints[0]
        out.y = mTmpPoints[1]
    }

    fun onResume(){
        createBitmap()
    }

    fun onPause(){
        releaseBitmap()
    }

    private fun createBitmap(){
        if(mOffscreenBitmap != null){
            mOffscreenBitmap!!.recycle()
        }

        mOffscreenBitmap = Bitmap.createBitmap(mModel!!.width,mModel!!.height,Bitmap.Config.ARGB_8888)
        mOffscreenCanvas = Canvas(mOffscreenBitmap)
        reset()
    }

    fun reset(){
        mDrawnLineSize = 0
        if(mOffscreenBitmap != null){
            mPaint.color = Color.WHITE
            val width = mOffscreenBitmap!!.width
            val height = mOffscreenBitmap!!.height
            mOffscreenCanvas!!.drawRect(Rect(0,0,width,height),mPaint)
        }
    }

    private fun releaseBitmap(){
        if(mOffscreenBitmap != null){
            mOffscreenBitmap!!.recycle()
            mOffscreenBitmap = null
            mOffscreenCanvas = null

        }

        reset()
    }

    fun getPixelData(): FloatArray? {
        if(mOffscreenBitmap == null){
            return null
        }

        val width = mOffscreenBitmap!!.width
        val height = mOffscreenBitmap!!.height

        val pixels = IntArray(width * height)
        mOffscreenBitmap!!.getPixels(pixels,0,width,0,0,width,height)

        val retPixels = FloatArray(pixels.size)
        for(i in pixels.indices){

            val pix = pixels[i]
            val b = pix and 0xff
            retPixels[i] = ((0xff - b) / 255.0).toFloat()
        }

        return retPixels
    }





}