package com.example.rewatchparty

import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference


class RoomDetailsActivity : AppCompatActivity() {

    private lateinit var roomId: String
    private lateinit var roomRef: DatabaseReference
    private lateinit var roomInfoText: TextView
    private lateinit var joinRoomButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)  // Make sure this layout is correct

        roomId = intent.getStringExtra("roomId") ?: ""
        roomInfoText = findViewById(R.id.roomInfoText)
        joinRoomButton = findViewById(R.id.joinRoomButton)

        if (roomId.isEmpty()) {
            roomInfoText.text = "Room ID is missing!"
            return
        }

        roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomId)

        // Fetch room data from Firebase and display it
        roomRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val password = snapshot.child("password").getValue(String::class.java)
                val maxUsers = snapshot.child("maxUsers").getValue(Int::class.java)

                roomInfoText.text = "Room ID: $roomId\nPassword: $password\nMax Users: $maxUsers"

                joinRoomButton.setOnClickListener {
                    // Check if user is already authenticated
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                    if (currentUserId != null) {
                        val usersRef = roomRef.child("users")

                        // Check the number of users in the room
                        usersRef.get().addOnSuccessListener { userSnapshot ->
                            val userCount = userSnapshot.childrenCount
                            val maxUsersLimit = maxUsers ?: 8

                            if (userCount < maxUsersLimit) {
                                // Add the user to the room's users list
                                usersRef.child(currentUserId).setValue(FirebaseAuth.getInstance().currentUser?.email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            roomInfoText.text = "You have successfully joined the room!"
                                        } else {
                                            roomInfoText.text = "Failed to join the room."
                                        }
                                    }
                            } else {
                                roomInfoText.text = "Room is full. Cannot join."
                            }
                        }
                    } else {
                        roomInfoText.text = "Please log in to join the room."
                    }
                }
            } else {
                roomInfoText.text = "Room does not exist!"
            }
        }
    }
}
