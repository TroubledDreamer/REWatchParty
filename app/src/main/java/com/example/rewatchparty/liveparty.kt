package com.example.rewatchparty
//activity_main.xml should be live_party.xml

import MessageAdapter
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
import androidx.recyclerview.widget.RecyclerView
import com.example.rewatchparty.data.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*


class liveparty : AppCompatActivity() {

    private lateinit var messagedatabase: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.live_party);
    //Initializing Firebase Auth
        messagedatabase = FirebaseDatabase.getInstance().getReference("messages")
        var auth = FirebaseAuth.getInstance();
        var user = auth.currentUser;
        if (user == null) {
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
        var firestoreref = FirebaseFirestore.getInstance();
        fetchVideo(firestoreref);

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMessages);
        val sendButton = findViewById<Button>(R.id.button2);
        val editTextInput = findViewById<EditText>(R.id.editTextText);

        messageAdapter = MessageAdapter(messages);
        recyclerView.adapter = messageAdapter;
        recyclerView.layoutManager = LinearLayoutManager(this);

        messagedatabase.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java);
                if (message != null) {
                    messages.add(message);
                    messageAdapter.notifyItemInserted(message.size - 1)
                    recyclerView.scrollToPosition(message.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?){}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        //Send a new message
        sendButton.setOnClickListener {
            val text = editTextInput.text.toString();
            if (text.isNotEmpty()) {
                val message = Message(user!!.email!!, text);
                messagedatabase.push().setValue(message);
                editTextInput.text.clear();
            }
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