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
import com.example.rewatchparty.data.Party
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class CreateOrJoinWPActivity : AppCompatActivity() {

    private lateinit var createParty: ImageButton;
    private lateinit var joinRoomButton: ImageButton;
//    val db = Firebase.firestore
//    val usersCollection = db.collection("users")
//    val partiesCollection = db.collection("parties")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_join_wpactivity);
        var auth = FirebaseAuth.getInstance();
        var user = auth.currentUser;
        if (user == null) {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
        else {
            createParty = findViewById(R.id.createParty);
            joinRoomButton = findViewById(R.id.joinParty);

            joinRoomButton.setOnClickListener {
                val intent3 = Intent(this, JoinRoomActivity::class.java);
                startActivity(intent3);
            }
            createParty.setOnClickListener {
                val intent3 = Intent(this, CreatePartyActivity::class.java);
                startActivity(intent3);
            }
        }



    }
}