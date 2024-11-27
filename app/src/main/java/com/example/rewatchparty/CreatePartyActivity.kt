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
import com.google.firebase.auth.FirebaseAuth


class CreatePartyActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_watch_party);
        var auth = FirebaseAuth.getInstance();
        var user = auth.currentUser;
        if (user == null) {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
        else {
//            val joinButton = findViewById<ImageButton>(R.id.joinButton);
        }
    }}

