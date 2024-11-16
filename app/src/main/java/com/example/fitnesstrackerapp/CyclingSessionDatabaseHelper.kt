package com.example.fitnesstrackerapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CyclingSessionDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "cycling_sessions.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "cycling_sessions"

        const val COL_ID = "id"
        const val COL_USER_ID = "user_id"
        const val COL_DURATION = "duration"
        const val COL_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_ID INTEGER NOT NULL,
                $COL_DURATION TEXT NOT NULL,
                $COL_TIMESTAMP TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert session with user ID
    fun insertSession(userId: Int, duration: String, timestamp: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_ID, userId) // Store user ID
            put(COL_DURATION, duration)
            put(COL_TIMESTAMP, timestamp)
        }
        val result = db.insert(TABLE_NAME, null, values)
        return result != -1L
    }

    fun getAllSessions(userId: Int): List<String> {
        val sessionList = mutableListOf<String>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COL_USER_ID = ?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val duration = cursor.getString(cursor.getColumnIndexOrThrow(COL_DURATION))
                val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP))
                sessionList.add("Duration: $duration, Time: $timestamp")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return sessionList
    }
}
