package com.example.fitnesstrackerapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CustomWorkoutsActivity : AppCompatActivity() {

    private lateinit var workoutDb: WorkoutDatabaseHelper
    private lateinit var workoutListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_workouts)

        workoutDb = WorkoutDatabaseHelper(this)

        val etWorkoutName = findViewById<EditText>(R.id.et_workout_name)
        val etWorkoutDescription = findViewById<EditText>(R.id.et_workout_description)
        val btnSaveWorkout = findViewById<Button>(R.id.btn_save_workout)
        val btnViewWorkouts = findViewById<Button>(R.id.btn_view_workouts)
        workoutListLayout = findViewById(R.id.layout_workout_list)

        btnSaveWorkout.setOnClickListener {
            val name = etWorkoutName.text.toString()
            val description = etWorkoutDescription.text.toString()
            if (name.isNotEmpty()) {
                if (workoutDb.insertWorkout(name, description)) {
                    Toast.makeText(this, "Workout Saved!", Toast.LENGTH_SHORT).show()
                    etWorkoutName.text.clear()
                    etWorkoutDescription.text.clear()
                } else {
                    Toast.makeText(this, "Failed to Save Workout", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter a Workout Name", Toast.LENGTH_SHORT).show()
            }
        }

        btnViewWorkouts.setOnClickListener { displayWorkouts() }
    }

    private fun displayWorkouts() {
        workoutListLayout.removeAllViews()
        val workouts = workoutDb.getAllWorkouts()

        for ((name, description) in workouts) {
            val workoutView = TextView(this).apply {
                text = "$name - $description"
                textSize = 18f
                setPadding(8, 8, 8, 8)
                setTextColor(resources.getColor(android.R.color.white, theme))
            }

            workoutView.setOnClickListener {
                workoutDb.deleteWorkout(name)
                Toast.makeText(this, "$name Deleted", Toast.LENGTH_SHORT).show()
                displayWorkouts()
            }

            workoutListLayout.addView(workoutView)
        }
    }
}
