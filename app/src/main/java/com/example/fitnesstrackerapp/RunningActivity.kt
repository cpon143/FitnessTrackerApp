package com.example.fitnesstrackerapp

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstrackerapp.database.Workout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*

class RunningActivity : AppCompatActivity(), LocationListener {

    private lateinit var startButton: AppCompatImageButton
    private lateinit var stopButton: AppCompatImageButton
    private lateinit var resetButton: AppCompatImageButton
    private lateinit var showHistoryButton: Button
    private lateinit var timerText: TextView
    private lateinit var distanceText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyRecyclerView: RecyclerView

    private lateinit var locationManager: LocationManager
    private var isRunning = false
    private var isTracking = false
    private var startTime: Long = 0
    private var totalDistance: Float = 0.0f
    private var totalCalories: Float = 0.0f
    private var previousLocation: Location? = null
    private val updateInterval = 1000L  // Location update interval (in ms)

    private var handler: Handler? = null
    private var timerRunnable: Runnable? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_running)

        // Initialize views
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        resetButton = findViewById(R.id.resetButton)
        showHistoryButton = findViewById(R.id.showHistoryButton)
        timerText = findViewById(R.id.timerText)
        distanceText = findViewById(R.id.distanceText)
        caloriesText = findViewById(R.id.caloriesText)
        progressBar = findViewById(R.id.progressBar)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)

        // Initialize LocationManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Check for permissions and request them if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Set up button listeners
        startButton.setOnClickListener {
            if (!isRunning) {
                startRun()
            }
        }

        stopButton.setOnClickListener {
            stopRun()
        }

        resetButton.setOnClickListener {
            resetRun()
        }

        showHistoryButton.setOnClickListener {
            showWorkoutHistory()
        }

        // RecyclerView setup in onCreate()
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = WorkoutHistoryAdapter()

        // Set up initial UI state
        updateUI()
    }

    private fun startRun() {
        // Start the run
        isRunning = true
        isTracking = true
        startTime = System.currentTimeMillis()  // Reset start time
        startTimer()  // Start the timer
        startButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE
        resetButton.visibility = View.VISIBLE

        // Start location updates
        startLocationUpdates()

        // Reset the timer display to "00:00" initially
        timerText.text = "00:00"
    }

    private fun stopRun() {
        // Stop the run
        isRunning = false
        isTracking = false
        stopButton.visibility = View.GONE
        startButton.visibility = View.VISIBLE
        resetButton.visibility = View.GONE

        // Stop location updates
        locationManager.removeUpdates(this)

        // Save workout details to the database
        val duration = (System.currentTimeMillis() - startTime) / 1000  // Duration in seconds
        saveWorkoutToDatabase(duration, totalDistance, totalCalories)

        // Stop the timer updates
        handler?.removeCallbacks(timerRunnable!!)
    }

    private fun resetRun() {
        totalDistance = 0.0f
        totalCalories = 0.0f
        previousLocation = null
        updateUI()  // Reset UI to show "00:00" and zero distance/calories
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request here if necessary
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateInterval, 0f, this)
    }

    private fun updateUI() {
        val elapsedTime = System.currentTimeMillis() - startTime  // Calculate elapsed time
        val elapsedTimeFormatted = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(elapsedTime))
        timerText.text = elapsedTimeFormatted  // Update the timer text

        distanceText.text = "Distance: %.2f km".format(totalDistance)
        caloriesText.text = "Calories: %.2f kcal".format(totalCalories)

        // Simulate progress bar
        progressBar.progress = (totalDistance * 10).toInt()
    }

    private fun startTimer() {
        // Initialize the handler and the runnable to update the timer every second
        handler = Handler()
        timerRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    updateUI()  // Update the UI every second
                    handler?.postDelayed(this, 1000)  // Run the update every second
                }
            }
        }
        handler?.post(timerRunnable!!)  // Start the timer
    }

    private fun showWorkoutHistory() {
        // Show workout history (toggle visibility of RecyclerView)
        historyRecyclerView.visibility = if (historyRecyclerView.isVisible) View.GONE else View.VISIBLE
    }

    private fun saveWorkoutToDatabase(duration: Long, distance: Float, calories: Float) {
        val workout = Workout(
            startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            duration = duration,
            distance = distance,
            calories = calories
        )

        lifecycleScope.launch(Dispatchers.IO) {
            WorkoutDatabase.getDatabase(applicationContext).workoutDao().insert(workout)
            loadWorkoutHistory()
        }
    }

    // Loading workout history
    private fun loadWorkoutHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            val workouts = WorkoutDatabase.getDatabase(applicationContext).workoutDao().getAll()
            runOnUiThread {
                (historyRecyclerView.adapter as WorkoutHistoryAdapter).submitList(workouts)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        if (previousLocation == null) {
            previousLocation = location
            return
        }

        val distance = previousLocation!!.distanceTo(location) / 1000f  // in kilometers
        totalDistance += distance
        previousLocation = location
        totalCalories += totalDistance * 0.1f  // Simulating calories burned (e.g., 0.1 kcal per meter)

        updateUI()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        // Make sure to stop location updates when the activity is paused to save battery
        locationManager.removeUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        // Restart location updates if permissions are granted
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        }
    }
}
