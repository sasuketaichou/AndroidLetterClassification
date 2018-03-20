package com.japri.amierul.letterclassaj.views

/**
 * Created by Hexa-Amierul.Japri on 19/3/2018.
 */

class DrawModel(val width:Int,val height:Int){

    class LineElem(var x: Float, var y: Float)
    class Line{
        val elems:ArrayList<LineElem> = ArrayList()

        fun addElem(elem:LineElem){
            elems.add(elem)
        }

        fun getElemSize():Int{
            return elems.size
        }

        fun getElem(index:Int):LineElem{
            return elems[index]
        }
    }

    private var mCurrentLine:Line? = null

    private val mLines:MutableList<Line> = arrayListOf()

    fun startLine(x:Float,y:Float){
        mCurrentLine = Line()
        mCurrentLine!!.addElem(LineElem(x,y))
        mLines.add(mCurrentLine!!)
    }

    fun endline(){
        mCurrentLine = null
    }

    fun addLineElem(x:Float,y:Float){
        if(mCurrentLine!=null){
            mCurrentLine!!.addElem(LineElem(x,y))
        }
    }

    fun getLineSize():Int{
        return mLines.size
    }

    fun getLine(index:Int):Line{
        return mLines[index]
    }

    fun clear(){
        mLines.clear()
    }

    fun endLine() {
        mCurrentLine = null
    }



}