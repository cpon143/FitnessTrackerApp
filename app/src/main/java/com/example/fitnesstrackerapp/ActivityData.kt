package com.example.fitnesstrackerapp

data class ActivityData(
    val id: Int,
    val activityType: String,
    val date: String,
    val duration: Long,
    val caloriesBurned: Double,
    val distance: Double?,
    val notes: String?,
    val weight: Double
)