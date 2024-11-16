package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class YogaActivity : AppCompatActivity() {

    private lateinit var chronometer: Chronometer
    private var isRunning = false
    private var pauseOffset: Long = 0
    private var userId: Int = -1  // Store user ID

    private lateinit var yogaDb: YogaSessionDatabaseHelper
    private lateinit var tvHistory: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga)

        // Retrieve user ID passed through intent
        userId = intent.getIntExtra("USER_ID", -1)
//        if (userId == -1) {
//            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
//            finish()  // Close the activity if user ID is invalid
//        }

        // Initialize Views and Database
        yogaDb = YogaSessionDatabaseHelper(this)
        chronometer = findViewById(R.id.chronometer)
        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnPause = findViewById<Button>(R.id.btn_pause)
        val btnStop = findViewById<Button>(R.id.btn_stop)
        val btnShowHistory = findViewById<Button>(R.id.btn_show_history)
        val feedbackText = findViewById<TextView>(R.id.tv_feedback)
        tvHistory = findViewById(R.id.tv_history)

        // Set click listeners
        btnStart.setOnClickListener { startChronometer(feedbackText) }
        btnPause.setOnClickListener { pauseChronometer(feedbackText) }
        btnStop.setOnClickListener { stopChronometer(feedbackText) }
        btnShowHistory.setOnClickListener { showHistory() }
    }

    // Start the chronometer
    private fun startChronometer(feedbackText: TextView) {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            isRunning = true
            feedbackText.text = "Session started! Keep going 🧘‍♂️"
        }
    }

    // Pause the chronometer
    private fun pauseChronometer(feedbackText: TextView) {
        if (isRunning) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            isRunning = false
            feedbackText.text = "Session paused. Take a breath 🌬️"
        }
    }

    // Stop the chronometer and save session
    private fun stopChronometer(feedbackText: TextView) {
        if (isRunning || pauseOffset != 0L) {
            chronometer.stop()
            val duration = chronometer.text.toString()
            val timestamp = getCurrentTimestamp()

            if (yogaDb.insertSession(userId, duration, timestamp)) {
                // Insert a reward for completing the cycling session
                val db = GamificationDatabaseHelper(this)
                db.insertReward("Yoga Champ", 50)
                Toast.makeText(this, "Yoga Session Completed! 🧘‍♂️ | +50 Points 🎉", Toast.LENGTH_SHORT).show()
            }

            resetChronometer()
            feedbackText.text = "Session stopped! Good job 🎉"
        } else {
            Toast.makeText(this, "No session to stop!", Toast.LENGTH_SHORT).show()
        }
    }

    // Reset the chronometer
    private fun resetChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        pauseOffset = 0
        isRunning = false
    }

    // Get the current timestamp
    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // Display session history in the TextView
    private fun showHistory() {
        val sessions = yogaDb.getAllSessions(userId)
        if (sessions.isNotEmpty()) {
            tvHistory.text = sessions.joinToString("\n")
        } else {
            tvHistory.text = "No session history available."
        }
        tvHistory.visibility = TextView.VISIBLE  // Make sure it is visible
    }


}
