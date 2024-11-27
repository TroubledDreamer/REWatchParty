package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class URLMovieSelect : AppCompatActivity() {

    private lateinit var urlEditText: EditText
    private lateinit var submitURLButton: Button
    private lateinit var roomText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.youtube_link_activity)

        // Initialize views
        urlEditText = findViewById(R.id.urlEditText)
        submitURLButton = findViewById(R.id.submitURLButton)
        roomText = findViewById(R.id.room_text)

        // Set button click listener
        submitURLButton.setOnClickListener {
            // Get URL from EditText
            val url = urlEditText.text.toString()

            // Handle button click (for example, update the TextView with the entered URL)
            if (url.isNotEmpty()) {
                roomText.text = "URL Submitted: $url"

                val intent = Intent(this, R.layout.room_invite_activity::class.java)

                startActivity(intent)  // Start the new activity

            } else {
                roomText.text = "Please enter a valid URL."
            }
        }
    }
}
