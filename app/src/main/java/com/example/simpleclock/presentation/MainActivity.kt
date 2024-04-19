/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.simpleclock.presentation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.simpleclock.R
import com.example.simpleclock.presentation.theme.SimpleClockTheme

class MainActivity : ComponentActivity() {

    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isStopwatchRunning = false
    private lateinit var secondsImage: ImageView
    private lateinit var minutesImage: ImageView
    private lateinit var hoursImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContentView(R.layout.layout)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val resetBtn = findViewById<ImageButton>(R.id.resetButton);

        fun vibration(vbTime: Long){
            if (vibrator.hasVibrator()) { // Vibrator availability checking
                vibrator.vibrate(VibrationEffect.createOneShot(vbTime, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }

        secondsImage = findViewById(R.id.secStopw)
        minutesImage = findViewById(R.id.minStopw)
        hoursImage = findViewById(R.id.hoursStopw)
        val stopwatchBtn = findViewById<ImageButton>(R.id.startStopwatchButton)
        val stopwTimeText = findViewById<TextView>(R.id.stopwatchTime)

        stopwatchBtn.setOnClickListener {
            if (!isStopwatchRunning) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            toggleStopwatch(stopwatchBtn, secondsImage, minutesImage, hoursImage)
            vibration(10)
        }

        resetBtn.setOnClickListener {
            if (!isStopwatchRunning) {
                secondsImage.rotation = 0f
                minutesImage.rotation = 0f
                hoursImage.rotation = 0f
                elapsedTime = 0
                updateElapsedTimeTextView(stopwTimeText)
                vibration(10)
            }
        }
    }

    val handler = Handler()

    private fun rotateImage(secondsImageView: ImageView, minutesImageView: ImageView, hoursImageView: ImageView) {
        val stopwTimeText = findViewById<TextView>(R.id.stopwatchTime)
        handler.post(object : Runnable {
            override fun run() {
                val currentTime = System.currentTimeMillis()
                elapsedTime = currentTime - this@MainActivity.startTime
                val elapsedSeconds = elapsedTime.toDouble() / 1000.0
                val secondsAngle = elapsedSeconds * 360.0 / 60.0 % 360.0
                val elapsedMinutes = elapsedSeconds / 60.0
                val minutesAngle = elapsedMinutes * 360.0 / 60.0 % 360.0
                val elapsedHours = elapsedMinutes / 60.0
                val hoursAngle = elapsedHours * 360.0 / 12.0 % 360.0
                secondsImageView.rotation = secondsAngle.toFloat()
                minutesImageView.rotation = minutesAngle.toFloat()
                hoursImageView.rotation = hoursAngle.toFloat()
                updateElapsedTimeTextView(stopwTimeText)
                handler.postDelayed(this, 10)
            }
        })
    }

    private fun toggleStopwatch(stopwatchBtn: ImageButton, secondsImage: ImageView, minutesImage: ImageView, hoursImage: ImageView) {
        if (!isStopwatchRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            rotateImage(secondsImage, minutesImage, hoursImage)
            isStopwatchRunning = true
            stopwatchBtn.setImageResource(R.mipmap.pause_stopwatch_foreground)
        } else {
            elapsedTime = System.currentTimeMillis() - startTime
            isStopwatchRunning = false
            stopwatchBtn.setImageResource(R.mipmap.start_stopwatch_foreground)
            handler.removeCallbacksAndMessages(null)
        }
    }

    private fun updateElapsedTimeTextView(stopwTimeText: TextView) {
        val totalMilliseconds = elapsedTime
        val totalSeconds = totalMilliseconds / 1000
        val milliseconds = (totalMilliseconds % 1000) / 10
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val formattedTime = String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds)
        stopwTimeText.text = formattedTime
    }
}