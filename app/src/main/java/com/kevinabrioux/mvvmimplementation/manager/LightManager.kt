package com.kevinabrioux.mvvmimplementation.manager

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import io.reactivex.subjects.BehaviorSubject

/**
 *
 * Created by kevinabrioux on 26/01/2018.
 */
class LightManager(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager? = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
    private val lightSensor: Sensor?
    private var handlerThread: HandlerThread? = null
    val lightValue: BehaviorSubject<Int> = BehaviorSubject.create<Int>()

    init {
        this.lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        this.handlerThread = HandlerThread(LightManager::class.java.simpleName)
        this.handlerThread?.start()
        val handler = Handler(this.handlerThread?.looper)
        this.sensorManager?.registerListener(this, this.lightSensor, SensorManager.SENSOR_DELAY_NORMAL, handler)
    }

    fun onStop() {
        sensorManager?.unregisterListener(this)
        if (this.handlerThread?.isAlive == true)
            this.handlerThread?.quitSafely()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.get(0)?.toInt()?.let { this.lightValue.onNext(it) }
    }

}