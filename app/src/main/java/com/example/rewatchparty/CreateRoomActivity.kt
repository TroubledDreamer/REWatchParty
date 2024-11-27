package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.app.AppCompatActivity

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var createWPText: TextView
    private lateinit var WPIDEditText: EditText
    private lateinit var WPPasswordEditText: EditText
    private lateinit var userSeekBar: SeekBar
    private lateinit var textView4: TextView
    private lateinit var CreateWPButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_watch_party)  // Make sure this layout is correct

        createWPText = findViewById(R.id.createWPText)
        WPIDEditText = findViewById(R.id.WPIDEditText)
        userSeekBar = findViewById(R.id.userSeekBar)
        textView4 = findViewById(R.id.textView4)
        CreateWPButton = findViewById(R.id.CreateWPButton)
        WPPasswordEditText = findViewById(R.id.WPPasswordEditText)

        CreateWPButton.setOnClickListener {
            val wpID = WPIDEditText.text.toString().trim()
            val wpPassword = WPPasswordEditText.text.toString().trim()
            val userCount = userSeekBar.progress

            // Basic validation
            if (wpID.isEmpty() || wpPassword.isEmpty()) {
                textView4.text = "Room ID and Password cannot be empty"
                return@setOnClickListener
            }

            // Create room data
            val roomData = hashMapOf(
                "password" to wpPassword,
                "maxUsers" to userCount,
                "users" to mutableMapOf<String, String>()  // Empty map for now, will be populated with user IDs later
            )

            // Get a reference to Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val roomsRef = database.getReference("rooms")

            // Save room data to Firebase
            roomsRef.child(wpID).setValue(roomData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Navigate to RoomDetailsActivity after successful creation
                        val intent = Intent(this, RoomDetailsActivity::class.java)
                        intent.putExtra("roomId", wpID)  // Pass the room ID to the next activity
                        startActivity(intent)
                        finish()  // Optionally finish this activity
                    } else {
                        textView4.text = "Failed to create room. Please try again."
                    }
                }
        }
    }
}
