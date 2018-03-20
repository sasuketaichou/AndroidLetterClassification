package com.japri.amierul.letterclassaj

import android.content.res.AssetManager
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by Hexa-Amierul.Japri on 19/3/2018.
 */

class Classifier{

    private var tfHelper:TensorFlowInferenceInterface? = null

    private var inputName:String? = null
    private var outputName:String? = null
    private var inputSize:Int? = null

    private var labels:List<String>? = null
    private var output:FloatArray? = null
    private var outputNames:Array<String>? = null

    companion object {

        val THRESHOLD = 0.1f

        @Throws(IOException::class)
        fun readLabels(c:Classifier, am:AssetManager,fileName:String ):List<String>{

            var br:BufferedReader? = BufferedReader(InputStreamReader(am.open(fileName)))


            var line = br!!.readLine()
            val labels = ArrayList<String>()
            while (line != null){

                labels.add(line)
                line = br.readLine()
            }

            br.close()
            return labels

        }

        @Throws(IOException::class)
        fun create(assetManager: AssetManager, modelPath:String,labelFile:String, inputSize:Int,inputName:String,outputName:String):Classifier{

            val c = Classifier()
            c.inputName = inputName
            c.outputName = outputName

            c.labels = readLabels(c,assetManager,labelFile)

            c.tfHelper = TensorFlowInferenceInterface(assetManager,modelPath)
            val numClasses = 10

            c.inputSize = inputSize

            c.outputNames = arrayOf(outputName)

            c.outputName = outputName
            c.output = FloatArray(numClasses)

            return c

        }
    }

    fun recognize(pixels:FloatArray):Classification{

        tfHelper!!.feed(inputName,pixels,1,inputSize!!.toLong(),inputSize!!.toLong(),1)
        tfHelper!!.feed("keep_prob", floatArrayOf(1.0f))
        tfHelper!!.run(outputNames)

        tfHelper!!.fetch(outputName,output)

        val ans = Classification()
        for(i in 0 until output!!.size){
            if(output!![i]> THRESHOLD && output!![i]>ans.conf){
                ans.update(output!![i], labels!![i])
            }
        }
        return ans
    }
}