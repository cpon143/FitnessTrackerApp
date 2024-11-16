package com.example.fitnesstrackerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StratchingDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "workoutDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE workout_sessions (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "exercise_name TEXT, reps_or_duration TEXT, calories INTEGER, rating REAL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS workout_sessions")
        onCreate(db)
    }

    fun insertSession(name: String, repsOrDuration: String, calories: Double, rating: Double): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("exercise_name", name)
            put("reps_or_duration", repsOrDuration)
            put("calories", calories)
            put("rating", rating)
        }
        val result = db.insert("workout_sessions", null, values)
        return result != -1L
    }

    fun getAllSessions(): List<String> {
        val sessions = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM workout_sessions", null)
        while (cursor.moveToNext()) {
            val session = "${cursor.getString(1)} - ${cursor.getString(2)} - ${cursor.getInt(3)}kcal - ${cursor.getFloat(4)}★"
            sessions.add(session)
        }
        cursor.close()
        return sessions
    }
}
