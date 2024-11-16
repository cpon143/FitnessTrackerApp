package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class CyclingActivity : AppCompatActivity(), LocationListener {



    private lateinit var chronometer: Chronometer
    private var isRunning = false
    private var pauseOffset: Long = 0

    private lateinit var cyclingDb: CyclingSessionDatabaseHelper
    private lateinit var tvHistory: TextView
    private lateinit var tvDistance: TextView

    private var locationManager: LocationManager? = null
    private var lastLocation: Location? = null
    private var totalDistance: Float = 0.0f
    private var userId: Int = -1  // Store user ID

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 100
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cycling)



        // Retrieve user ID passed through intent
        userId = intent.getIntExtra("USER_ID", -1)
//        if (userId == -1) {
//            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show()
//            finish()  // Close the activity if user ID is invalid
//        }

        // Initialize Views and Database
        cyclingDb = CyclingSessionDatabaseHelper(this)
        chronometer = findViewById(R.id.chronometer)
        tvDistance = findViewById(R.id.tv_distance)
        val btnStart = findViewById<Button>(R.id.btn_start)
        val btnPause = findViewById<Button>(R.id.btn_pause)
        val btnStop = findViewById<Button>(R.id.btn_stop)
        val btnShowHistory = findViewById<Button>(R.id.btn_show_history)
        val btnViewDistance = findViewById<Button>(R.id.btn_view_distance)
        tvHistory = findViewById(R.id.tv_history)

        // Initialize Location Manager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Set click listeners
        btnStart.setOnClickListener { startChronometer() }
        btnPause.setOnClickListener { pauseChronometer() }
        btnStop.setOnClickListener { stopChronometer() }
        btnShowHistory.setOnClickListener { showHistory() }
        btnViewDistance.setOnClickListener { displayDistance() }

        // Request location permission if not already granted
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000L, // Update every 2 seconds
            5f, // Update every 5 meters
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        if (lastLocation != null) {
            val distance = lastLocation!!.distanceTo(location)
            totalDistance += distance
        }
        lastLocation = location
    }

    private fun displayDistance() {
        val distanceInKm = totalDistance / 1000  // Convert to kilometers
        tvDistance.text = String.format("Distance: %.2f km", distanceInKm)
        Toast.makeText(this, "Distance Traveled: $distanceInKm km", Toast.LENGTH_SHORT).show()
    }

    private fun startChronometer() {
        if (!isRunning) {
            chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            chronometer.start()
            isRunning = true
            Toast.makeText(this, "Ride Started! 🚴‍♂️", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pauseChronometer() {
        if (isRunning) {
            chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.base
            isRunning = false
            Toast.makeText(this, "Ride Paused. Take a Break! 🌬️", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun stopChronometer() {
//        if (isRunning || pauseOffset != 0L) {
//            chronometer.stop()
//            val duration = chronometer.text.toString()
//            val timestamp = getCurrentTimestamp()
//
//            if (cyclingDb.insertSession(userId, duration, getCurrentTimestamp())) {
//                Toast.makeText(this, "Session Saved! 🚴‍♂️", Toast.LENGTH_SHORT).show()
//            }
//            resetChronometer()
//        } else {
//            Toast.makeText(this, "No session to stop!", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun stopChronometer() {

        if (isRunning || pauseOffset != 0L) {
            chronometer.stop()
            val duration = chronometer.text.toString()
            val timestamp = getCurrentTimestamp()



            if (cyclingDb.insertSession(userId, duration, timestamp)) {
                // Insert a reward for completing the cycling session
                val db = GamificationDatabaseHelper(this)
                db.insertReward("Cycling Champ", 50)
                Toast.makeText(this, "Cycling Session Completed! 🚴‍♂️ | +50 Points 🎉", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Session not saved", Toast.LENGTH_SHORT).show()
            }
            resetChronometer()
        } else {
            Toast.makeText(this, "No session to stop!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        pauseOffset = 0
        isRunning = false
        totalDistance = 0.0f
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }


    private fun showHistory() {
        val sessions = cyclingDb.getAllSessions(userId)
        tvHistory.text = sessions.joinToString("\n")
    }
//    private fun showHistory() {
//        val sessions = cyclingDb.getAllSessions(userId)
//        if (sessions.isNotEmpty()) {
//            tvHistory.text = sessions.joinToString("\n")
//        } else {
//            tvHistory.text = "No session history available."
//        }
//        tvHistory.visibility = TextView.VISIBLE  // Make sure it is visible
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
//            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show()
        }


    }


    fun openGamification(view: View) {
        val intent = Intent(this, GamificationActivity::class.java)
        startActivity(intent)
    }
}
