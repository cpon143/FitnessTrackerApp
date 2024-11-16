package com.example.fitnesstrackerapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, "fitnessDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE activities(id INTEGER PRIMARY KEY, name TEXT)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS activities")
        onCreate(db)
    }

    fun insertActivity(name: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply { put("name", name) }
        return db.insert("activities", null, values)
    }
}
