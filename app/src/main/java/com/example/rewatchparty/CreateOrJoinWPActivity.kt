package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.database.database
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image


class CreateOrJoinWPActivity : AppCompatActivity() {

    private lateinit var createRoomButton: ImageButton;
    private lateinit var joinRoomButton: ImageButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_join_wpactivity);

        createRoomButton = findViewById(R.id.createButton);
        joinRoomButton = findViewById(R.id.joinParty);

        createRoomButton.setOnClickListener {
            val intent = Intent(this, CreateRoomActivity::class.java);
            startActivity(intent);
        }
        joinRoomButton.setOnClickListener {
            val intent = Intent(this, JoinRoomActivity::class.java);
            startActivity(intent);
        }

    }
}