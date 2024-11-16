package com.example.fitnesstrackerapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class WorkoutDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "workouts.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "custom_workouts"

        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT NOT NULL,
                $COL_DESCRIPTION TEXT
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertWorkout(name: String, description: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_DESCRIPTION, description)
        }
        return db.insert(TABLE_NAME, null, values) != -1L
    }

    fun getAllWorkouts(): List<Pair<String, String>> {
        val workoutList = mutableListOf<Pair<String, String>>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
                workoutList.add(Pair(name, description))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return workoutList
    }

    fun deleteWorkout(name: String): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COL_NAME=?", arrayOf(name)) > 0
    }
}
