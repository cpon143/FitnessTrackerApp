package com.example.fitnesstrackerapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class GamificationActivity : AppCompatActivity() {

    private lateinit var db: GamificationDatabaseHelper
    private lateinit var tvPoints: TextView
    private lateinit var badgeListLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamification)

        db = GamificationDatabaseHelper(this)
        tvPoints = findViewById(R.id.tv_points)
        badgeListLayout = findViewById(R.id.layout_badge_list)
        val btnViewBadges = findViewById<Button>(R.id.btn_view_badges)

        updateTotalPoints()

        btnViewBadges.setOnClickListener { displayBadges() }
    }

    private fun updateTotalPoints() {
        val totalPoints = db.getTotalPoints()
        tvPoints.text = "Total Points: $totalPoints"
    }

    private fun displayBadges() {
        badgeListLayout.removeAllViews()
        val badges = db.getAllBadges()

        for (badge in badges) {
            val badgeView = TextView(this).apply {
                text = badge
                textSize = 18f
                setPadding(8, 8, 8, 8)
                setTextColor(resources.getColor(android.R.color.white, theme))
            }
            badgeListLayout.addView(badgeView)
        }
    }
}
