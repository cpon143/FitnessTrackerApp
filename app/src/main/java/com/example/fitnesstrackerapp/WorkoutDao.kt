package com.example.fitnesstrackerapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fitnesstrackerapp.database.Workout

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(workout: Workout)

    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    suspend fun getAll(): List<Workout>
}
