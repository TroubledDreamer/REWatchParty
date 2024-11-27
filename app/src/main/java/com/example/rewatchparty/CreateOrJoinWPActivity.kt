package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CreateOrJoinWPActivity : AppCompatActivity() {

    private lateinit var createRoomButton: ImageButton
    private lateinit var joinRoomButton: ImageButton
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_join_wpactivity)

        // Bind views
        createRoomButton = findViewById(R.id.createButton)
        joinRoomButton = findViewById(R.id.joinParty)

        // Handle "Create Room" button click
        createRoomButton.setOnClickListener {
            val intent2 = Intent(this, CreateRoomActivity::class.java)
            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent2)
        }

        // Handle "Join Room" button click
        joinRoomButton.setOnClickListener {
            val intent3 = Intent(this, JoinRoomActivity::class.java)
            startActivity(intent3)
        }
    }
}
