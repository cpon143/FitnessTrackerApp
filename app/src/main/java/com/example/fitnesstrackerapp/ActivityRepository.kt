package com.example.fitnesstrackerapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.core.database.getDoubleOrNull

class ActivityRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Insert cycling activity data
    fun insertCyclingActivityData(activityType: String, date: String, duration: Long, caloriesBurned: Double, distance: Double?, notes: String?, weight: Double): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ACTIVITY_TYPE, activityType)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_DURATION, duration)
            put(DatabaseHelper.COLUMN_CALORIES_BURNED, caloriesBurned)
            put(DatabaseHelper.COLUMN_DISTANCE, distance)
            put(DatabaseHelper.COLUMN_NOTES, notes)
            put(DatabaseHelper.COLUMN_WEIGHT, weight)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_ACTIVITIES, null, values)
        db.close()
        return newRowId
    }

    // Insert yoga activity data
    fun insertYogaActivityData(activityType: String, date: String, duration: Long, caloriesBurned: Double, distance: Double?, notes: String?, weight: Double): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ACTIVITY_TYPE, activityType)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_DURATION, duration)
            put(DatabaseHelper.COLUMN_CALORIES_BURNED, caloriesBurned)
            put(DatabaseHelper.COLUMN_DISTANCE, distance)
            put(DatabaseHelper.COLUMN_NOTES, notes)
            put(DatabaseHelper.COLUMN_WEIGHT, weight)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_ACTIVITIES, null, values)
        db.close()
        return newRowId
    }

    // Insert Stretching activity data
    fun insertStretchingActivityData(activityType: String, date: String, duration: Long, caloriesBurned: Double, distance: Double?, notes: String?, weight: Double): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ACTIVITY_TYPE, activityType)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_DURATION, duration)
            put(DatabaseHelper.COLUMN_CALORIES_BURNED, caloriesBurned)
            put(DatabaseHelper.COLUMN_DISTANCE, distance)
            put(DatabaseHelper.COLUMN_NOTES, notes)
            put(DatabaseHelper.COLUMN_WEIGHT, weight)
        }
        val newRowId = db.insert(DatabaseHelper.TABLE_ACTIVITIES, null, values)
        db.close()
        return newRowId
    }

    // Fetch all activities
    fun getAllActivities(): List<ActivityData> {
        val db = dbHelper.readableDatabase
        val activityList = mutableListOf<ActivityData>()

        val cursor = db.query(DatabaseHelper.TABLE_ACTIVITIES, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val activity = ActivityData(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    activityType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ACTIVITY_TYPE)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
                    duration = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DURATION)),
                    caloriesBurned = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CALORIES_BURNED)),
                    distance = cursor.getDoubleOrNull(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DISTANCE)),
                    notes = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTES)),
                    weight = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT))
                )
                activityList.add(activity)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return activityList
    }


}