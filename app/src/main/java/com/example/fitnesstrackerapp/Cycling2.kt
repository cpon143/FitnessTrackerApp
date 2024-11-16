package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Cycling2 : AppCompatActivity() {

    private lateinit var exerciseList: LinearLayout
    private lateinit var btnRestart: Button
    private lateinit var chronometer: Chronometer
    private lateinit var btnStop: Button
    private lateinit var tvCaloriesBurned: TextView
    private lateinit var etWeight: EditText
    private var isChronometerRunning = false
    private var totalCaloriesBurned = 0.0
    private val handler = Handler()
    private var caloriesPerSecond: Double = 0.05  // Estimated calories burned per second for cycling
    private var pauseOffset: Long = 0  // Variable to store the pause offset

    private lateinit var activityRepository: ActivityRepository

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycling2)

        // Initialize the repository with the context
        activityRepository = ActivityRepository(this)

        // Initialize views
        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val btnFavorite = findViewById<ImageButton>(R.id.btn_favorite)
        btnRestart = findViewById(R.id.btn_restart)
        exerciseList = findViewById(R.id.exercise_list)
        btnStop = findViewById(R.id.btn_stop)
        chronometer = findViewById(R.id.chronometer)
        tvCaloriesBurned = findViewById(R.id.tv_calories_burned) // Initialize the TextView for calories
        etWeight = findViewById(R.id.et_weight) // Get reference to the weight input

        val exerciseItems = listOf(
            findViewById(R.id.exercise_1),
            findViewById(R.id.exercise_2),
            findViewById(R.id.exercise_3),
            findViewById<LinearLayout>(R.id.exercise_4),
            findViewById(R.id.exercise_5)
        )

        btnStart.setOnClickListener {
            val weightInput = etWeight.text.toString()

            if (weightInput.isNotEmpty()) {
                val weight = weightInput.toDouble()
                caloriesPerSecond = 0.05 * (weight / 70)  // Adjust calorie rate based on the user's weight

                if (!isChronometerRunning) {
                    // Start the chronometer from the current pauseOffset
                    chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                    chronometer.start()
                    isChronometerRunning = true
                    startCaloriesBurning()  // Start the calorie burning calculation
                    Toast.makeText(this, "Cycling Session Started!", Toast.LENGTH_SHORT).show()
                } else {
                    // If chronometer is running, resume from pauseOffset
                    chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
                    chronometer.start()
                    isChronometerRunning = true
                    startCaloriesBurning()  // Continue the calorie calculation
                    Toast.makeText(this, "Cycling Session Resumed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your weight.", Toast.LENGTH_SHORT).show()
            }
        }

        btnStop.setOnClickListener {
            // Stop the chronometer and store the elapsed time (pauseOffset)
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            isChronometerRunning = false
            Toast.makeText(this, "Cycling Session Stopped. Take a break!", Toast.LENGTH_SHORT).show()
        }

//        btnRestart.setOnClickListener {
//            val weightInput = etWeight.text.toString()
//            if (weightInput.isNotEmpty()) {
//                val weight = weightInput.toDouble()
//
//                // Reset session
//                chronometer.stop()
//                chronometer.base = SystemClock.elapsedRealtime()
//                totalCaloriesBurned = 0.0
//                tvCaloriesBurned.text = "Calories Burned: 0 kcal"
//                pauseOffset = 0
//                resetExercises(exerciseItems)
//                isChronometerRunning = false
//            } else {
//                Toast.makeText(this, "Please enter your weight before resetting the session.", Toast.LENGTH_SHORT).show()
//            }
//        }

        btnRestart.setOnClickListener {
            val weightInput = etWeight.text.toString()
            if (weightInput.isNotEmpty()) {
                val weight = weightInput.toDouble()
                val duration = SystemClock.elapsedRealtime() - chronometer.base
                val date = "2024-11-14" // This could be dynamically set to current date

                // Insert data into SQLite using the repository
                activityRepository.insertCyclingActivityData(
                    activityType = "Cycling",
                    date = date,
                    duration = duration,
                    caloriesBurned = totalCaloriesBurned,
                    distance = null, // Set distance if applicable
                    notes = "Cycling session notes",
                    weight = weight
                )

                // Reset session
                chronometer.stop()
                chronometer.base = SystemClock.elapsedRealtime()
                totalCaloriesBurned = 0.0
                tvCaloriesBurned.text = "Calories Burned: 0 kcal"
                pauseOffset = 0
                resetExercises(exerciseItems)
                isChronometerRunning = false
                Toast.makeText(this, "Cycling data saved and session reset.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter your weight before resetting the session.", Toast.LENGTH_SHORT).show()
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
                    totalCaloriesBurned += caloriesPerSecond  // Add calories every second based on weight
                    tvCaloriesBurned.text = String.format("Calories Burned: %.2f kcal", totalCaloriesBurned)
                    handler.postDelayed(this, 1000) // Update every second
                }
            }
        }, 1000)
    }
}
