package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class URLMovieSelect : AppCompatActivity() {

    private lateinit var urlEditText: EditText
    private lateinit var submitURLButton: Button
    private lateinit var roomText: TextView
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.youtube_link_activity)

        // Initialize views
        urlEditText = findViewById(R.id.urlEditText)
        submitURLButton = findViewById(R.id.submitURLButton)
        roomText = findViewById(R.id.room_text)

        // Get the room ID passed from CreateRoomActivity
        roomId = intent.getStringExtra("roomId") ?: ""

        // Set button click listener
        submitURLButton.setOnClickListener {
            val url = urlEditText.text.toString()

            if (url.isNotEmpty()) {
                // Update TextView with the entered URL
                roomText.text = "URL Submitted: $url"

                // Store the YouTube URL in Firebase under the room's ID
                val database = FirebaseDatabase.getInstance()
                val roomRef = database.getReference("rooms").child(roomId)

                // Prepare data as MutableMap<String, Any>
                val updateData: MutableMap<String, Any> = hashMapOf(
                    "youtubeURL" to url
                )

                // Update the Firebase Realtime Database with the YouTube URL
                roomRef.updateChildren(updateData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Navigate to WatchMovieActivity after saving the URL
                        val intent = Intent(this, YouTubeActivity::class.java)
                        intent.putExtra("roomId", roomId)  // Pass roomId to WatchMovieActivity
                        startActivity(intent)
                    } else {
                        roomText.text = "Failed to submit URL. Please try again."
                    }
                }
            } else {
                roomText.text = "Please enter a valid URL."
            }
        }
    }
}
