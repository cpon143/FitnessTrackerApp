package com.example.fitnesstrackerapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream

class UserActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: EditText
    private lateinit var userPasswordEditText: EditText
    private lateinit var profilePictureImageView: ImageView
    private var userId: Int = -1
    private val REQUEST_CODE_SELECT_IMAGE = 100

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        databaseHelper = DatabaseHelper(this)
        userNameEditText = findViewById(R.id.userNameEditText)
        userEmailEditText = findViewById(R.id.userEmailEditText)
        userPasswordEditText = findViewById(R.id.userPasswordEditText)
        profilePictureImageView = findViewById(R.id.profilePictureImageView)

        // Retrieve user ID (pass via intent or retrieve from session)
        userId = databaseHelper.getUserId("a@a.a") // Replace with actual user email or ID

        loadUserProfile()

        // Update profile picture on click
        profilePictureImageView.setOnClickListener {
            selectImageFromGallery()
        }

        // Update profile button
        findViewById<Button>(R.id.updateProfileButton).setOnClickListener {
            updateUserProfile()
        }

        // View profile button functionality
        findViewById<Button>(R.id.viewProfileButton).setOnClickListener {
//            val intent = Intent(this, ViewProfileActivity::class.java)
//            startActivity(intent)
            // Passing userId from UserActivity to ViewProfileActivity
            val intent = Intent(this, ViewProfileActivity::class.java)
            intent.putExtra("USER_ID", userId) // Pass the userId to the new activity
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        // Retrieve user data and set to UI
        val cursor = databaseHelper.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COL_ID} = ?",
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PASSWORD))
            val profilePicture = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PROFILE_PICTURE))

            userNameEditText.setText(name)
            userEmailEditText.setText(email)
            userPasswordEditText.setText(password)

            if (profilePicture != null) {
                val bitmap = BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.size)
                profilePictureImageView.setImageBitmap(bitmap)
            }
        }
        cursor.close()
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            profilePictureImageView.setImageBitmap(bitmap)

            // Convert the image to byte array to store in the database
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            val profilePictureBytes = outputStream.toByteArray()
            databaseHelper.updateUserProfilePicture(userId, profilePictureBytes)
        }
    }

    private fun updateUserProfile() {
        val name = userNameEditText.text.toString()
        val email = userEmailEditText.text.toString()
        val password = userPasswordEditText.text.toString()

        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_NAME, name)
            put(DatabaseHelper.COL_EMAIL, email)
            put(DatabaseHelper.COL_PASSWORD, password)
        }
        db.update(DatabaseHelper.TABLE_USER, values, "${DatabaseHelper.COL_ID} = ?", arrayOf(userId.toString()))
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
    }

}
