package com.example.rewatchparty

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
        setContentView(R.layout.room_invite_activity)  // Replace with your actual layout file name

        // Initialize views
        urlEditText = findViewById(R.id.urlEditText)
        submitURLButton = findViewById(R.id.startPartyButton)
        roomText = findViewById(R.id.room_text)

        // Set button click listener
        submitURLButton.setOnClickListener {
            // Get URL from EditText
            val url = urlEditText.text.toString()

            // Handle button click (for example, update the TextView with the entered URL)
            if (url.isNotEmpty()) {
                roomText.text = "URL Submitted: $url"
            } else {
                roomText.text = "Please enter a valid URL."
            }
        }
    }
}
