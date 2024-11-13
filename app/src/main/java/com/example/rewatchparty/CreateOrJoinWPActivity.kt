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

    private lateinit var CreateRoomButton: ImageButton;
    private lateinit var JoinRoomButton: ImageButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_join_wpactivity)

//        CreateRoomButton = findViewById(R.id.CreateRoomButton)
    }
}