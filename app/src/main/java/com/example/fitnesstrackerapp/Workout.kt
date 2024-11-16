package com.example.fitnesstrackerapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: String,
    val duration: Long,
    val distance: Float,
    val calories: Float
)
