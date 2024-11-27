package com.example.rewatchparty
//activity_main.xml should be live_party.xml

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.TextView
import android.widget.VideoView
import com.google.firebase.Firebase
import com.google.firebase.database.database
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class liveparty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_party);
    //Initializing Firebase Auth
        var auth = FirebaseAuth.getInstance();
        var user = auth.currentUser;
        if (user == null) {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
        var firestoreref = FirebaseFirestore.getInstance();
        fetchVideo(firestoreref);

        val editTextInput = findViewById<EditText>(R.id.editTextText);
        val sendDisplayButton = findViewById<Button>(R.id.button2);
        val textoutput = findViewById<TextView>(R.id.textView7);

        sendDisplayButton.setOnClickListener {
            val inputText = editTextInput.text.toString();
            textoutput.text = inputText;
        }
    }

    private fun fetchVideo(firestoreref: FirebaseFirestore) {
        val videoRef = firestoreref.collection("videos").document("vid1");
        videoRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val videoUrl = document.getString("url");
                    if (videoUrl != null) {
                        playVideo(videoUrl);
                    }
                 }                    }
    }

    private fun playVideo(videoUrl: String) {
        val videoView = findViewById<VideoView>(R.id.videoView);
        videoView.setVideoPath(videoUrl);
        videoView.start();
    }
}