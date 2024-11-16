package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GestureDetectorCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class HomeActivity : AppCompatActivity() {


    private lateinit var gestureDetector: GestureDetectorCompat

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize date selector
        highlightCurrentDay()

        // Initialize the FAB
       val fab1 = findViewById<FloatingActionButton>(R.id.fab_dashboard)
        fab1.setOnClickListener {
            // Open the DashboardActivity
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        // Initialize the FAB and set its OnClickListener
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Open the UserDetailsActivity to edit user details
            openUserActivity()
        }

        // Set click listeners for activity cards
        findViewById<View>(R.id.yogaCard).setOnClickListener {
            openActivity(Yoga2::class.java)
        }

        findViewById<View>(R.id.cyclingCard).setOnClickListener {
            openActivity(Cycling2::class.java)
        }

        findViewById<View>(R.id.runningCard).setOnClickListener {
            openActivity(RunningActivity::class.java)
        }

        findViewById<View>(R.id.customWorkCard).setOnClickListener {
            openActivity(CustomWorkoutsActivity::class.java)
        }

        findViewById<View>(R.id.stretchingCard).setOnClickListener {
            openActivity(StretchingActivity::class.java)
        }

        // Initialize the GestureDetector for swipe detection
        gestureDetector = GestureDetectorCompat(this, GestureListener())

        // Get references to the tabs and activity cards
        val outdoorTab = findViewById<TextView>(R.id.outdoorTab)
        val indoorTab = findViewById<TextView>(R.id.indoorTab)
        val outdoorCards = findViewById<LinearLayout>(R.id.outdoorCards)
        val indoorCards = findViewById<LinearLayout>(R.id.indoorCards)

        // Initially, show outdoor activities and hide indoor activities
        outdoorCards.visibility = View.VISIBLE
        indoorCards.visibility = View.GONE

        // Set click listener for Outdoor tab
        outdoorTab.setOnClickListener {
            // Show outdoor activities and hide indoor activities
            outdoorCards.visibility = View.VISIBLE
            indoorCards.visibility = View.GONE

            // Update tab styles (optional)
            outdoorTab.setTextColor(resources.getColor(R.color.selectedTabColor))
            indoorTab.setTextColor(resources.getColor(R.color.unselectedTabColor))
        }

        // Set click listener for Indoor tab
        indoorTab.setOnClickListener {
            // Show indoor activities and hide outdoor activities
            indoorCards.visibility = View.VISIBLE
            outdoorCards.visibility = View.GONE

            // Update tab styles (optional)
            indoorTab.setTextColor(resources.getColor(R.color.selectedTabColor))
            outdoorTab.setTextColor(resources.getColor(R.color.unselectedTabColor))
        }
    }

    // Helper function to open the respective activity
    private fun <T> openActivity(activityClass: Class<T>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    // Helper function to open the UserDetailsActivity
    private fun openUserActivity() {
        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)
    }

    // Implement swipe gesture listener for left and right swipe
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val threshold = 100 // minimum distance for a swipe to be considered
            if (e1 != null && e2 != null) {
                val deltaX = e2.x - e1.x
                if (Math.abs(deltaX) > threshold) {
                    if (deltaX > 0) {
                        // Swipe right: show outdoor tab
                        showOutdoorTab()
                    } else {
                        // Swipe left: show indoor tab
                        showIndoorTab()
                    }
                    return true
                }
            }
            return false
        }
    }

    // Helper function to show the Outdoor tab
    private fun showOutdoorTab() {
        val outdoorTab = findViewById<TextView>(R.id.outdoorTab)
        val indoorTab = findViewById<TextView>(R.id.indoorTab)
        val outdoorCards = findViewById<LinearLayout>(R.id.outdoorCards)
        val indoorCards = findViewById<LinearLayout>(R.id.indoorCards)

        // Show outdoor activities and hide indoor activities
        outdoorCards.visibility = View.VISIBLE
        indoorCards.visibility = View.GONE

        // Update tab styles
        outdoorTab.setTextColor(resources.getColor(R.color.selectedTabColor))
        indoorTab.setTextColor(resources.getColor(R.color.unselectedTabColor))
    }

    // Helper function to show the Indoor tab
    private fun showIndoorTab() {
        val outdoorTab = findViewById<TextView>(R.id.outdoorTab)
        val indoorTab = findViewById<TextView>(R.id.indoorTab)
        val outdoorCards = findViewById<LinearLayout>(R.id.outdoorCards)
        val indoorCards = findViewById<LinearLayout>(R.id.indoorCards)

        // Show indoor activities and hide outdoor activities
        indoorCards.visibility = View.VISIBLE
        outdoorCards.visibility = View.GONE

        // Update tab styles
        indoorTab.setTextColor(resources.getColor(R.color.selectedTabColor))
        outdoorTab.setTextColor(resources.getColor(R.color.unselectedTabColor))
    }

    // Override the onTouchEvent to pass touch events to GestureDetector
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    // Helper function to highlight the current day of the week and set the correct date
    private fun highlightCurrentDay() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Set the date for each day in the week
        setDayDate(R.id.monCard, Calendar.MONDAY, dayOfMonth)
        setDayDate(R.id.tueCard, Calendar.TUESDAY, dayOfMonth + 1)
        setDayDate(R.id.wedCard, Calendar.WEDNESDAY, dayOfMonth + 2)
        setDayDate(R.id.thuCard, Calendar.THURSDAY, dayOfMonth + 3)
        setDayDate(R.id.friCard, Calendar.FRIDAY, dayOfMonth + 4)
        setDayDate(R.id.satCard, Calendar.SATURDAY, dayOfMonth + 5)
        setDayDate(R.id.sunCard, Calendar.SUNDAY, dayOfMonth + 6)

        // Highlight the current day
        when (dayOfWeek) {
            Calendar.MONDAY -> highlightDay(R.id.monCard)
            Calendar.TUESDAY -> highlightDay(R.id.tueCard)
            Calendar.WEDNESDAY -> highlightDay(R.id.wedCard)
            Calendar.THURSDAY -> highlightDay(R.id.thuCard)
            Calendar.FRIDAY -> highlightDay(R.id.friCard)
            Calendar.SATURDAY -> highlightDay(R.id.satCard)
            Calendar.SUNDAY -> highlightDay(R.id.sunCard)
        }
    }

    // Helper function to set the date for each day of the week
    private fun setDayDate(dayId: Int, dayOfWeek: Int, date: Int) {
        val dayCard = findViewById<TextView>(dayId)
        // Format the day and date, e.g., "MON 5"
        val formattedDate = getDayOfWeekName(dayOfWeek) + "\n" + date
        dayCard.text = formattedDate
    }

    // Helper function to get the full day name from the calendar
    private fun getDayOfWeekName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> ""
        }
    }

    // Helper function to highlight a specific day
    private fun highlightDay(dayId: Int) {
        val dayCard = findViewById<TextView>(dayId)

        // Change the background color to orange
        dayCard.setBackgroundResource(R.drawable.rounded_button_orange)

        // Set the text color to white and make the text bold
        dayCard.setTextColor(resources.getColor(android.R.color.white))
        dayCard.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    // Helper function to reset all days to default background and style
    private fun resetAllDays() {
        val days = listOf(
            R.id.monCard, R.id.tueCard, R.id.wedCard, R.id.thuCard,
            R.id.friCard, R.id.satCard, R.id.sunCard
        )
        for (dayId in days) {
            val dayCard = findViewById<TextView>(dayId)

            // Reset the background to gray
            dayCard.setBackgroundResource(R.drawable.rounded_button_gray)

            // Reset text style to normal (non-bold) and text color to default
            dayCard.setTextColor(resources.getColor(android.R.color.black))
            dayCard.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }
}
