package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StretchingActivity : AppCompatActivity() {

    private lateinit var dbHelper: StratchingDatabaseHelper  // Initialize the database helper
    private lateinit var exerciseList: LinearLayout
    private lateinit var btnRestart: Button
    private lateinit var chronometer: Chronometer
    private lateinit var btnStop: Button
    private lateinit var tvCaloriesBurned: TextView
    private lateinit var etWeight: EditText
    private var isChronometerRunning = false
    private var totalCaloriesBurned = 0.0
    private val handler = Handler()
    private var caloriesPerSecond: Double = 0.0174  // Fixed calories per second for stretching
    private var pauseOffset: Long = 0  // Variable to store the pause time offset

    private lateinit var activityRepository: ActivityRepository

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stretching)

        // Initialize the repository with the context
        activityRepository = ActivityRepository(this)

        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnFavorite = findViewById<ImageButton>(R.id.btn_favorite)
        btnRestart = findViewById(R.id.btn_restart)
        exerciseList = findViewById(R.id.exercise_list)
        btnStop = findViewById(R.id.btn_stop)
        chronometer = findViewById(R.id.chronometer)
        tvCaloriesBurned = findViewById(R.id.tv_calories_burned) // Initialize the TextView for calories
        etWeight = findViewById(R.id.et_weight) // Get reference to the weight input

        // Track completion status for all exercises
        val exerciseItems = listOf(
            findViewById<LinearLayout>(R.id.exercise_1),
            findViewById<LinearLayout>(R.id.exercise_2),
            findViewById<LinearLayout>(R.id.exercise_3),
            findViewById<LinearLayout>(R.id.exercise_4),
            findViewById<LinearLayout>(R.id.exercise_5)
        )

        btnStart.setOnClickListener {
            val weightInput = etWeight.text.toString()

            if (weightInput.isNotEmpty()) {
                val weight = weightInput.toDouble()
                // We don't need to adjust caloriesPerSecond here for stretching
                // caloriesPerSecond will remain fixed at 0.0174 per second during stretching
                if (!isChronometerRunning) {
                    // Start the chronometer or resume from the pause offset
                    chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                    chronometer.start()
                    isChronometerRunning = true
                    startCaloriesBurning()  // Start calculating calories
                    Toast.makeText(this, "Workout Started!", Toast.LENGTH_SHORT).show()
                } else {
                    // If already running, resume from the pauseOffset
                    chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                    chronometer.start()
                    isChronometerRunning = true
                    startCaloriesBurning()  // Continue the calorie calculation
                    Toast.makeText(this, "Workout Resumed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your weight.", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            // Stop the workout and save the pause time
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base // Store the time at the point of stopping
            isChronometerRunning = false
            Toast.makeText(this, "Workout Stopped. Take a break!", Toast.LENGTH_SHORT).show()
        }

//        btnRestart.setOnClickListener {
//            // Insert session data into the database
//            val weightInput = etWeight.text.toString()
//            if (weightInput.isNotEmpty()) {
//                val weight = weightInput.toDouble()
//
//                // Insert session into the database
//                dbHelper.insertSession("Stretching",
//                    (totalCaloriesBurned / caloriesPerSecond).toInt().toString(), totalCaloriesBurned, weight)
//
//                // Reset everything to the initial state
//                chronometer.stop()
//                chronometer.base = SystemClock.elapsedRealtime()
//                isChronometerRunning = false
//                pauseOffset = 0 // Reset pauseOffset
//                resetExercises(exerciseItems)
//                totalCaloriesBurned = 0.0
//                tvCaloriesBurned.text = "Calories Burned: 0 kcal"
//                handler.removeCallbacksAndMessages(null)  // Stop the calorie calculation
//
//                Toast.makeText(this, "Session Reset and Saved!", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Please enter your weight before saving the session.", Toast.LENGTH_SHORT).show()
//            }
//        }

        btnRestart.setOnClickListener {
            // Insert session data into the database
            val weightInput = etWeight.text.toString()
            if (weightInput.isNotEmpty()) {
                val weight = weightInput.toDouble()
                val duration = SystemClock.elapsedRealtime() - chronometer.base
                val date = "2024-11-14" // This could be dynamically set to current date

                // Insert data into SQLite using the repository
                activityRepository.insertStretchingActivityData(
                    activityType = "Stretching",
                    date = date,
                    duration = duration,
                    caloriesBurned = totalCaloriesBurned,
                    distance = null, // Set distance if applicable
                    notes = "Cycling session notes",
                    weight = weight
                )

                // Reset everything to the initial state
                chronometer.stop()
                chronometer.base = SystemClock.elapsedRealtime()
                isChronometerRunning = false
                pauseOffset = 0 // Reset pauseOffset
                resetExercises(exerciseItems)
                totalCaloriesBurned = 0.0
                tvCaloriesBurned.text = "Calories Burned: 0 kcal"
                handler.removeCallbacksAndMessages(null)  // Stop the calorie calculation

                Toast.makeText(this, "Stretching data saved and session reset.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter your weight before saving the session.", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener { finish() }

        btnFavorite.setOnClickListener {
            Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show()
        }

        // Set click listeners for exercise items
        exerciseItems.forEach { item ->
            item.setOnClickListener { markAsCompleted(item) }
        }
    }

    // Function to mark an exercise as completed
    private fun markAsCompleted(item: LinearLayout) {
        item.setBackgroundColor(Color.LTGRAY)
        Toast.makeText(this, "Exercise Completed!", Toast.LENGTH_SHORT).show()
    }

    // Function to reset all exercises to incomplete state
    private fun resetExercises(items: List<LinearLayout>) {
        items.forEach { item ->
            item.setBackgroundColor(Color.TRANSPARENT) // Reset background color
        }
        Toast.makeText(this, "Exercises Reset!", Toast.LENGTH_SHORT).show()
    }

    // Function to update and calculate calories burned
    private fun startCaloriesBurning() {
        // Update calories burned every second
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isChronometerRunning) {
                    totalCaloriesBurned += caloriesPerSecond  // Add 0.0174 calories every second (for stretching)
                    tvCaloriesBurned.text = String.format("Calories Burned: %.2f kcal", totalCaloriesBurned)
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }, 1000)
    }
}
