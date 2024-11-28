package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RoomInvite : AppCompatActivity() {

    private lateinit var urlEditText: EditText
    private lateinit var submitURLButton: Button
    private lateinit var roomText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_invite_activity)  // Ensure this is the correct layout file

        // Initialize views
//        urlEditText = findViewById(R.id.urlEditText)  // Initialize the URL EditText
        submitURLButton = findViewById(R.id.startPartyButton)  // Initialize the button
        roomText = findViewById(R.id.room_text)  // Initialize the roomText TextView

        // Set button click listener
        submitURLButton.setOnClickListener {
            // Get URL from EditText
            val url = urlEditText.text.toString().trim()  // Trim the URL to avoid leading/trailing spaces

            // Handle button click
            if (url.isNotEmpty()) {
                // Update roomText with the submitted URL
                roomText.text = "URL Submitted: $url"

                // Pass the URL to WatchMovieActivity along with room ID and video ID
                val intent = Intent(this, WatchMovieActivity::class.java)
                val roomId = "your_room_id_here"  // Replace with actual room ID logic
                val videoId = "your_video_id_here"  // Replace with actual video ID logic

                intent.putExtra("room_id", roomId)  // Passing room ID
                intent.putExtra("video_id", videoId)  // Passing video ID
                intent.putExtra("url", url)  // Passing the URL as an extra
                startActivity(intent)

            } else {
                // Show message if URL is empty
                roomText.text = "Please enter a valid URL."
            }
        }
    }
}
