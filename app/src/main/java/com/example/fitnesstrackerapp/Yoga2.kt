package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Yoga2 : AppCompatActivity() {


    private lateinit var yogaPoseList: LinearLayout
    private lateinit var btnRestart: Button
    private lateinit var chronometer: Chronometer
    private lateinit var btnStop: Button
    private lateinit var tvCaloriesBurned: TextView
    private lateinit var etDuration: EditText
    private var isChronometerRunning = false
    private var totalCaloriesBurned = 0.0
    private val handler = Handler()
    private var caloriesPerSecond: Double = 0.008  // Fixed calories per second for yoga practice
    private var pauseOffset: Long = 0  // Variable to store the time when paused

    private lateinit var activityRepository: ActivityRepository

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga2)

        // Initialize the repository with the context
        activityRepository = ActivityRepository(this)


        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnFavorite = findViewById<ImageButton>(R.id.btn_favorite)
        btnRestart = findViewById(R.id.btn_restart)
        yogaPoseList = findViewById(R.id.pose_list)
        btnStop = findViewById(R.id.btn_stop)
        chronometer = findViewById(R.id.chronometer)
        tvCaloriesBurned = findViewById(R.id.tv_calories_burned) // Initialize the TextView for calories
        etDuration = findViewById(R.id.et_duration) // Get reference to the duration input

        // Track completion status for all yoga poses
        val poseItems = listOf(
            findViewById<LinearLayout>(R.id.pose_1),
            findViewById<LinearLayout>(R.id.pose_2),
            findViewById<LinearLayout>(R.id.pose_3),
            findViewById<LinearLayout>(R.id.pose_4),
            findViewById<LinearLayout>(R.id.pose_5)
        )

        btnStart.setOnClickListener {
            val durationInput = etDuration.text.toString()

            if (durationInput.isNotEmpty()) {
                val duration = durationInput.toDouble()  // Duration in minutes
                // We don't need to adjust caloriesPerSecond here for yoga, it is fixed for now
                if (!isChronometerRunning) {
                    // Start or resume chronometer from the paused time
                    chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                    chronometer.start()
                    isChronometerRunning = true
                    startCaloriesBurning()  // Start calculating calories
                    Toast.makeText(this, "Yoga Practice Started!", Toast.LENGTH_SHORT).show()
                } else {
                    // If already running, resume from the pauseOffset
                    chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                    chronometer.start()
                    isChronometerRunning = true
                    startCaloriesBurning()  // Continue the calorie calculation
                    Toast.makeText(this, "Yoga Practice Resumed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your practice duration.", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            // Stop the workout and store the elapsed time as pauseOffset
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base // Store the time at the point of stopping
            isChronometerRunning = false
            Toast.makeText(this, "Yoga Practice Stopped. Take a break!", Toast.LENGTH_SHORT).show()
        }

//        btnRestart.setOnClickListener {
//            val durationInput = etDuration.text.toString()
//            if (durationInput.isNotEmpty()) {
//                val duration = durationInput.toDouble()
//
//
//                // Reset everything to initial state
//                chronometer.stop()
//                chronometer.base = SystemClock.elapsedRealtime()
//                isChronometerRunning = false
//                pauseOffset = 0 // Reset pauseOffset
//                resetPoses(poseItems)
//                totalCaloriesBurned = 0.0
//                tvCaloriesBurned.text = "Calories Burned: 0 kcal"
//                handler.removeCallbacksAndMessages(null)  // Stop the calorie calculation
//
////                Toast.makeText(this, "Session Reset and Saved!", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Please enter your practice duration before saving the session.", Toast.LENGTH_SHORT).show()
//            }
//        }

        btnRestart.setOnClickListener {
            val durationInput = etDuration.text.toString()
            if (durationInput.isNotEmpty()) {
                val durationn = durationInput.toDouble()
                val duration = SystemClock.elapsedRealtime() - chronometer.base
                val date = "2024-11-14" // This could be dynamically set to current date

                // Insert data into SQLite using the repository
                activityRepository.insertYogaActivityData(
                    activityType = "Yoga",
                    date = date,
                    duration = duration,
                    caloriesBurned = totalCaloriesBurned,
                    distance = null, // Set distance if applicable
                    notes = "Cycling session notes",
                    weight = durationn
                )

                // Reset everything to initial state
                chronometer.stop()
                chronometer.base = SystemClock.elapsedRealtime()
                isChronometerRunning = false
                pauseOffset = 0 // Reset pauseOffset
                resetPoses(poseItems)
                totalCaloriesBurned = 0.0
                tvCaloriesBurned.text = "Calories Burned: 0 kcal"
                handler.removeCallbacksAndMessages(null)  // Stop the calorie calculation

                Toast.makeText(this, "Yoga data saved and session reset.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter your practice duration before saving the session.", Toast.LENGTH_SHORT).show()
            }
        }


        btnBack.setOnClickListener { finish() }

        btnFavorite.setOnClickListener {
            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
        }

        // Set click listeners for yoga pose items
        poseItems.forEach { item ->
            item.setOnClickListener { markAsCompleted(item) }
        }
    }

    // Function to mark a yoga pose as completed
    private fun markAsCompleted(item: LinearLayout) {
        item.setBackgroundColor(Color.LTGRAY)
        Toast.makeText(this, "Yoga Pose Completed!", Toast.LENGTH_SHORT).show()
    }

    // Function to reset all poses to incomplete state
    private fun resetPoses(items: List<LinearLayout>) {
        items.forEach { item ->
            item.setBackgroundColor(Color.TRANSPARENT) // Reset background color
        }
        Toast.makeText(this, "Yoga Poses Reset!", Toast.LENGTH_SHORT).show()
    }

    // Function to update and calculate calories burned
    private fun startCaloriesBurning() {
        // Update calories burned every second
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isChronometerRunning) {
                    totalCaloriesBurned += caloriesPerSecond  // Add 0.008 calories every second (for yoga)
                    tvCaloriesBurned.text = String.format("Calories Burned: %.2f kcal", totalCaloriesBurned)
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }, 1000)
    }
}
