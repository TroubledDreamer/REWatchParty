package com.example.rewatchparty

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.appcompat.app.AppCompatActivity

class JoinRoomActivity : AppCompatActivity() {

    private lateinit var roomIdEditText: EditText
    private lateinit var roomPasswordEditText: EditText
    private lateinit var joinButton: Button
    private lateinit var roomInfoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_watch_party)  // Ensure this layout is correct

        // Initialize the views
        roomIdEditText = findViewById(R.id.roomIdEditText)
        roomPasswordEditText = findViewById(R.id.roomPasswordEditText)
        joinButton = findViewById(R.id.joinRoomButton)
        roomInfoTextView = findViewById(R.id.roomInfoTextView)

        // Set up the join button click listener
        joinButton.setOnClickListener {
            val roomId = roomIdEditText.text.toString().trim()
            val roomPassword = roomPasswordEditText.text.toString().trim()

            // Validate inputs
            if (roomId.isEmpty() || roomPassword.isEmpty()) {
                roomInfoTextView.text = "Please fill in both fields."
                return@setOnClickListener
            }

            // Get a reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val roomRef = database.getReference("rooms").child(roomId)

            // Fetch the room data from Firebase
            roomRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Retrieve stored password and user count from the room data
                    val storedPassword = snapshot.child("password").getValue(String::class.java)
                    val maxUsers = snapshot.child("maxUsers").getValue(Int::class.java) ?: 8
                    val usersRef = roomRef.child("users")

                    // Check if the password is correct
                    if (storedPassword == roomPassword) {
                        // Check if room has space for another user
                        usersRef.get().addOnSuccessListener { userSnapshot ->
                            val currentUserCount = userSnapshot.childrenCount

                            if (currentUserCount < maxUsers) {
                                // Get current user ID (assuming user is logged in)
                                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                                if (currentUserId != null) {
                                    // Add the user to the room under the "users" node
                                    usersRef.child(currentUserId).setValue(FirebaseAuth.getInstance().currentUser?.email)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                roomInfoTextView.text = "You have successfully joined the room!"
                                                // Optionally navigate to another activity (e.g., video controls)
                                                // val intent = Intent(this, YouTubeActivity::class.java)
                                                // startActivity(intent)
                                            } else {
                                                roomInfoTextView.text = "Failed to add you to the room. Please try again."
                                            }
                                        }
                                }
                            } else {
                                roomInfoTextView.text = "The room is full. Cannot join."
                            }
                        }
                    } else {
                        roomInfoTextView.text = "Incorrect password."
                    }
                } else {
                    roomInfoTextView.text = "Room does not exist."
                }
            }.addOnFailureListener { exception ->
                roomInfoTextView.text = "Error: ${exception.message}"
            }
        }
    }
}
