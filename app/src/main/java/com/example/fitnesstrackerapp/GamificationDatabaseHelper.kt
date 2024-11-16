package com.example.fitnesstrackerapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GamificationDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "gamification.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "rewards"

        const val COL_ID = "id"
        const val COL_BADGE_NAME = "badge_name"
        const val COL_POINTS = "points"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BADGE_NAME TEXT NOT NULL,
                $COL_POINTS INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertReward(badgeName: String, points: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_BADGE_NAME, badgeName)
            put(COL_POINTS, points)
        }
        return db.insert(TABLE_NAME, null, values) != -1L
    }

    fun getTotalPoints(): Int {
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT SUM($COL_POINTS) AS total FROM $TABLE_NAME", null)
        var totalPoints = 0
        if (cursor.moveToFirst()) {
            totalPoints = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        return totalPoints
    }

    fun getAllBadges(): List<String> {
        val badgeList = mutableListOf<String>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val badgeName = cursor.getString(cursor.getColumnIndexOrThrow(COL_BADGE_NAME))
                val points = cursor.getInt(cursor.getColumnIndexOrThrow(COL_POINTS))
                badgeList.add("$badgeName - $points Points")
            } while (cursor.moveToNext())
        }
        cursor.close()
        return badgeList
    }
}
