package com.example.countdowntimer

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private var START_TIME_IN_MILLIS: Long = 0
    private var countDownTimer: CountDownTimer? = null
    private var timerRunning: Boolean = false
    private var timeLeftInMillis: Long = START_TIME_IN_MILLIS
    private var endTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        set_time_btn.setOnClickListener {
            setTimer()
        }

        // TODO Start_Pause Timer
        start_pause_btn.setOnClickListener {
            if (timerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        // TODO Reset Timer
        reset_btn.setOnClickListener {
            resetTimer()
        }
//        updateCountDownText()
    }

    // TODO Set Timer
    private fun setTimer() {
        var input: String = edit_text_input.text.toString()
        if (input.isNullOrEmpty()) {
            Toast.makeText(this@MainActivity,
                    "Field can't be empty", Toast.LENGTH_SHORT).show()
            return
        }
        var millisInput = input.toLong() * 60000
        if (millisInput <= 0) {
            Toast.makeText(this@MainActivity,
                    "Enter a positive number", Toast.LENGTH_SHORT).show()
            return
        }
        START_TIME_IN_MILLIS = millisInput
        edit_text_input.setText("")
        resetTimer()
        closeKeyboard()
    }

    // TODO Hide/Close Keyboard
    private fun closeKeyboard() {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    // TODO Start Timer
    private fun startTimer() {
        endTime = System.currentTimeMillis() + timeLeftInMillis
        // Countdown Timer Update The Text Every 1000 Milli Seconds (1sec)
        // Mean Our onTick() Method is Only Called Every One Second
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            // Member of "object" written above
            override fun onFinish() {
                timerRunning = false
                start_pause_btn.text = "Start"
//                start_pause.visibility = View.INVISIBLE
//                reset.visibility = View.VISIBLE
                updateWatchInterface()
            }
        }.start()
        timerRunning = true
//        start_pause.text = "Pause"
//        reset.visibility = View.INVISIBLE
        updateWatchInterface()
    }

    // TODO Pause Timer
    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerRunning = false
//        start_pause.text = "Start"
//        reset.visibility = View.VISIBLE
        updateWatchInterface()
    }

    // TODO Reset Timer
    private fun resetTimer() {
        timeLeftInMillis = START_TIME_IN_MILLIS
        updateCountDownText();
//        start_pause.visibility = View.VISIBLE
//        reset.visibility = View.INVISIBLE
        updateWatchInterface()
    }

    // TODO updateCountDownText()
    private fun updateCountDownText() {
        val hours = (timeLeftInMillis / 1000).toInt() / 3600
        val minutes = (timeLeftInMillis / 1000).toInt() / 60
        val seconds = (timeLeftInMillis / 1000).toInt() % 60
//        val timeLeftFormatted: String = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
//        val hours = TimeUnit.MILLISECONDS.toHours(timeLeftInMillis)
//        val minutes = (timeLeftInMillis / 1000 % 3600).toInt() / 60
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftInMillis)
//        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftInMillis)

        var timeLeftFormatted: String = if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
        text_view_countdown.text = timeLeftFormatted
    }

    // TODO updateWatchInterface()
    private fun updateWatchInterface() {
        if(timerRunning) {
            start_pause_btn.text = "Pause"
            reset_btn.visibility = View.INVISIBLE
            edit_text_input.visibility = View.INVISIBLE
            set_time_btn.visibility = View.INVISIBLE
        } else {
            start_pause_btn.text = "Start"
            edit_text_input.visibility = View.VISIBLE
            set_time_btn.visibility = View.VISIBLE

            if(timeLeftInMillis < 1000) {
                start_pause_btn.visibility = View.INVISIBLE
            } else {
                start_pause_btn.visibility = View.VISIBLE
            }

            if(timeLeftInMillis < START_TIME_IN_MILLIS) {
                reset_btn.visibility = View.VISIBLE
            } else {
                reset_btn.visibility = View.INVISIBLE
            }
        }
    }

    // TODO Same as onSaveInstanceState() Method
    override fun onStop() {
        super.onStop()

        var prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        var editor: SharedPreferences.Editor = prefs.edit()

        editor.putLong("startTimeInMillis",START_TIME_IN_MILLIS)
        editor.putLong("millisLeft",timeLeftInMillis)
        editor.putLong("endTime",endTime)
        editor.putBoolean("timerRunning",timerRunning)

        editor.apply()

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }
    }

    // TODO Same as onRestoreInstanceState() Method
    override fun onStart() {
        super.onStart()

        var prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)

        // Second Parameters values are Default Values
        START_TIME_IN_MILLIS = prefs.getLong("startTimeInMillis", 600000)
        timeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS)
        timerRunning = prefs.getBoolean("timerRunning", false)

        updateCountDownText()
        updateWatchInterface()

        if (timerRunning) {
            // TODO we take endTime bcz: We keep our Time while onTick() is called every one second
            // "0" is default value of endTime
            endTime = prefs.getLong("endTime", 0)
            timeLeftInMillis = endTime - System.currentTimeMillis()

            // IF-ELSE Check is to avoid negative counting
            if (timeLeftInMillis < 0) {
                timeLeftInMillis = 0
                timerRunning = false
                updateCountDownText()
                updateWatchInterface()
            } else {
                startTimer()
            }
        }
    }



//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putLong("millisLeft", timeLeftInMillis)
//        outState.putBoolean("timerRunning",timerRunning)
//        outState.putLong("endTime",endTime)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        timeLeftInMillis = savedInstanceState.getLong("millisLeft")
//        timerRunning = savedInstanceState.getBoolean("timerRunning")
//        updateCountDownText()
//        updateButtons()
//        if(timerRunning){
//            // we take endTime bcz: We keep our Time while onTick() is called every one second
//            endTime = savedInstanceState.getLong("endTime")
//            timeLeftInMillis = endTime - System.currentTimeMillis()
//            startTimer()
//        }
//    }
}
