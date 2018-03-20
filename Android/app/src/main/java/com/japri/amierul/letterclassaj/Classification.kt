package com.japri.amierul.letterclassaj

/**
 * Created by Hexa-Amierul.Japri on 19/3/2018.
 */
class Classification {

    var conf: Float = 0.toFloat()
        private set
    var label: String? = null
        private set

    constructor(conf: Float, label: String) {
        update(conf, label)
    }

    constructor() {
        this.conf = (-1.0).toFloat()
        this.label = null
    }

    fun update(conf: Float, label: String) {
        this.conf = conf
        this.label = label
    }

}