package com.example.fitnesstrackerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "fitness_app.db"
        const val DATABASE_VERSION = 1

        // User Table Constants
        const val TABLE_USER = "user"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_EMAIL = "email"
        const val COL_PASSWORD = "password"
        const val COL_PROFILE_PICTURE = "profile_picture" // New column for profile picture


        // Activities Table Constants
        const val TABLE_ACTIVITIES = "activities"
        const val COLUMN_ID = "id"
        const val COLUMN_ACTIVITY_TYPE = "activity_type"
        const val COLUMN_DATE = "date"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_CALORIES_BURNED = "calories_burned"
        const val COLUMN_DISTANCE = "distance"
        const val COLUMN_NOTES = "notes"
        const val COLUMN_WEIGHT = "weight"
    }

    // Creating tables
    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT,
                $COL_EMAIL TEXT UNIQUE,
                $COL_PASSWORD TEXT,
                $COL_PROFILE_PICTURE BLOB)  
        """

        val createActivitiesTable = """
            CREATE TABLE $TABLE_ACTIVITIES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACTIVITY_TYPE TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_DURATION INTEGER,
                $COLUMN_CALORIES_BURNED REAL,
                $COLUMN_DISTANCE REAL,
                $COLUMN_NOTES TEXT,
                $COLUMN_WEIGHT REAL)
        """

        db?.execSQL(createUserTable)
        db?.execSQL(createActivitiesTable)
    }

    // Handle upgrades by dropping and recreating both tables
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES")
        onCreate(db)
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN $COL_PROFILE_PICTURE BLOB")
        }
    }

    // User-related methods (Insert, Validate, etc.)
//    fun insertUser(name: String, email: String, password: String): Long {
//        val db = writableDatabase
//        val values = ContentValues().apply {
//            put(COL_NAME, name)
//            put(COL_EMAIL, email)
//            put(COL_PASSWORD, password)
//        }
//        return db.insert(TABLE_USER, null, values)
//    }

    // Insert a new user, optionally with a profile picture
    fun insertUser(name: String, email: String, password: String, profilePicture: ByteArray? = null): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
            put(COL_PROFILE_PICTURE, profilePicture)  // Optional profile picture
        }
        return db.insert(TABLE_USER, null, values)
    }

    // Update the user's profile picture
    fun updateUserProfilePicture(userId: Int, profilePicture: ByteArray?): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PROFILE_PICTURE, profilePicture)
        }
        return db.update(TABLE_USER, values, "$COL_ID = ?", arrayOf(userId.toString()))
    }

    fun validateUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COL_EMAIL = ? AND $COL_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    fun getUserId(email: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_ID FROM $TABLE_USER WHERE $COL_EMAIL = ?", arrayOf(email))
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
        }
        cursor.close()
        return userId
    }
}
