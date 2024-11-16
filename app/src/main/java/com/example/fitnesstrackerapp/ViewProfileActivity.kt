package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewProfileActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var profilePictureImageView: ImageView
    private var userId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        databaseHelper = DatabaseHelper(this)
        userNameTextView = findViewById(R.id.viewUserNameTextView)
        userEmailTextView = findViewById(R.id.viewUserEmailTextView)
        profilePictureImageView = findViewById(R.id.viewProfilePictureImageView)

        // Retrieve user ID
        userId = databaseHelper.getUserId("a@a.a") // Replace with actual user email

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val cursor = databaseHelper.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COL_ID} = ?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL))
            val profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_PICTURE))

            userNameTextView.text = name
            userEmailTextView.text = email

            if (profilePicture != null) {
                val bitmap = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.size)
                profilePictureImageView.setImageBitmap(bitmap)
            }else{
                Log.d("ViewProfileActivity", "No user data found for userId: $userId")
            }
        }
        cursor.close()
    }
}
