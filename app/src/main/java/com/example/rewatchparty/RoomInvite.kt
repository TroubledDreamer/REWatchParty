package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RoomInvite : AppCompatActivity() {

    private lateinit var startPartyButton: Button
    private lateinit var roomText: TextView
    private lateinit var roomId: String
    private lateinit var videoId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room_invite_activity)  // Ensure this is the correct layout file

        // Initialize views
        startPartyButton = findViewById(R.id.startPartyButton)  // Initialize the button
        roomText = findViewById(R.id.room_text)  // Initialize the roomText TextView

        // Retrieve the roomId and videoId passed from URLMovieSelect activity
        roomId = intent.getStringExtra("roomId") ?: ""
        videoId = intent.getStringExtra("videoId") ?: ""

        // Fetch the stored YouTube URL from Firebase
        val database = FirebaseDatabase.getInstance()
        val roomRef = database.getReference("rooms").child(roomId)

        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val youtubeURL = snapshot.child("youtubeURL").getValue(String::class.java)
                if (youtubeURL != null) {
                    roomText.text = "Video URL: $youtubeURL"
                } else {
                    roomText.text = "No video URL found for this room."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                roomText.text = "Error fetching data: ${error.message}"
            }
        })

        // Set the button click listener to start the party
        startPartyButton.setOnClickListener {
            // Proceed to WatchMovieActivity
            val intent = Intent(this, WatchMovieActivity::class.java)
            intent.putExtra("room_id", roomId)  // Passing room ID
            intent.putExtra("video_id", videoId)  // Passing video ID
            startActivity(intent)
        }
    }
}
