package com.japri.amierul.letterclassaj

import android.support.v7.app.AppCompatActivity

import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.japri.amierul.letterclassaj.views.DrawModel
import com.japri.amierul.letterclassaj.views.DrawView

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {

    // tensorflow input and output
    private val INPUT_SIZE = 28
    private val INPUT_NAME = "input"
    private val OUTPUT_NAME = "output"
    private val MODEL_FILE = "optimized_letter_convnet.pb"
    private val LABEL_FILE = "letterlabels.txt"
    private val PIXEL_WIDTH = 28

    // ui related
    private var clearBtn: Button? = null
    private var classBtn: Button? = null
    private var resText: TextView? = null
    private var classifier: Classifier? = null

    // views related
    private var drawModel: DrawModel? = null
    private var drawView: DrawView? = null
    private val mTmpPiont = PointF()

    private var mLastX: Float = 0.toFloat()
    private var mLastY: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get drawing view
        drawView = findViewById(R.id.draw)
        drawModel = DrawModel(PIXEL_WIDTH, PIXEL_WIDTH)

        drawView!!.setModel(drawModel!!)
        drawView!!.setOnTouchListener(this)

        //clear button
        clearBtn = findViewById<View>(R.id.btn_clear) as Button
        clearBtn!!.setOnClickListener(this)

        //class button
        classBtn = findViewById<View>(R.id.btn_class) as Button
        classBtn!!.setOnClickListener(this)

        // res text
        resText = findViewById<View>(R.id.tfRes) as TextView

        // tensorflow
        loadModel()
    }

    override fun onResume() {
        drawView!!.onResume()
        super.onResume()
    }

    override fun onPause() {
        drawView!!.onPause()
        super.onPause()
    }

    private fun loadModel() {
        Thread(Runnable {
            try {
                classifier = Classifier.create(applicationContext.assets,
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        INPUT_NAME,
                        OUTPUT_NAME)
            } catch (e: Exception) {
                throw RuntimeException("Error initializing TensorFlow!", e)
            }
        }).start()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_clear) {
            drawModel!!.clear()
            drawView!!.reset()
            drawView!!.invalidate()

            resText!!.text = "Result: "
        } else if (view.id == R.id.btn_class) {
            val pixels = drawView!!.getPixelData()

            val res = classifier!!.recognize(pixels!!)
            var result = "Result: "
            if (res.label == null) {
                resText!!.text = result + "?"
            } else {
                result += res.label
                result += "\nwith probability: " + res.conf
                resText!!.text = result
            }
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action and MotionEvent.ACTION_MASK

        if (action == MotionEvent.ACTION_DOWN) {
            processTouchDown(event)
            return true
        } else if (action == MotionEvent.ACTION_MOVE) {
            processTouchMove(event)
            return true
        } else if (action == MotionEvent.ACTION_UP) {
            processTouchUp()
            return true
        }
        return false
    }

    private fun processTouchDown(event: MotionEvent) {
        mLastX = event.x
        mLastY = event.y
        drawView!!.calcPos(mLastX, mLastY, mTmpPiont)
        val lastConvX = mTmpPiont.x
        val lastConvY = mTmpPiont.y
        drawModel!!.startLine(lastConvX, lastConvY)
    }

    private fun processTouchMove(event: MotionEvent) {
        val x = event.x
        val y = event.y

        drawView!!.calcPos(x, y, mTmpPiont)
        val newConvX = mTmpPiont.x
        val newConvY = mTmpPiont.y
        drawModel!!.addLineElem(newConvX, newConvY)

        mLastX = x
        mLastY = y
        drawView!!.invalidate()
    }

    private fun processTouchUp() {
        drawModel!!.endLine()
    }
}