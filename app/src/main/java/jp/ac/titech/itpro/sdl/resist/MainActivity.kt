package jp.ac.titech.itpro.sdl.resist

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var rotationView: RotationView? = null
    private var manager: SensorManager? = null
    private var gyroscope: Sensor? = null

    private var arg: Double = 0.0
    private var prevOmegaZ: Double = 0.0
    private var prevTime: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")
        rotationView = findViewById(R.id.rotation_view)
        manager = getSystemService(SENSOR_SERVICE) as SensorManager
        if (manager == null) {
            Toast.makeText(this, R.string.toast_no_sensor_manager, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        gyroscope = manager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        if (gyroscope == null) {
            Toast.makeText(this, R.string.toast_no_gyroscope, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        manager!!.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        manager!!.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val omegaZ = event.values[2].toDouble() // z-axis angular velocity (rad/sec)
        val time = Date()
        if (prevTime != null) {
            arg +=  ((time.time - prevTime!!.time) * (omegaZ * (1.0 - ALPHA) + prevOmegaZ * ALPHA) / 1000) % (2.0 * Math.PI)
        }
        prevTime = time
        prevOmegaZ = omegaZ
        rotationView!!.setDirection(arg)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged: accuracy=$accuracy")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val ALPHA = 0.75
    }
}