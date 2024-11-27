package com.example.rewatchparty

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class WatchMovieActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var seekButton: Button
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScrollView: ScrollView

    private val database = FirebaseDatabase.getInstance()
    private val roomRef = database.getReference("rooms")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_movie)

        // Initialize views
        webView = findViewById(R.id.youtube_webview)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        seekButton = findViewById(R.id.seek_button)
        chatInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.send_button)
        chatContainer = findViewById(R.id.chat_container)
        chatScrollView = findViewById(R.id.chat_scroll_view)

        // WebView settings for JavaScript and loading YouTube video
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Load the YouTube video using the room data
        val videoId = intent.getStringExtra("video_id") ?: "YOUR_DEFAULT_VIDEO_ID"
        val embedUrl = "https://www.youtube.com/embed/$videoId?enablejsapi=1"
        webView.loadUrl(embedUrl)

        // Play button: Play the video
        playButton.setOnClickListener {
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
            updateVideoControlsInFirebase("play", true)
        }

        // Pause button: Pause the video
        pauseButton.setOnClickListener {
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
            updateVideoControlsInFirebase("pause", true)
        }

        // Seek button: Seek to 30 seconds
        seekButton.setOnClickListener {
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].currentTime = 30;")
            updateVideoControlsInFirebase("currentTime", 30.0)
        }

        // Send a chat message
        sendButton.setOnClickListener {
            val message = chatInput.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                chatInput.text.clear()
            }
        }

        // Sync video controls and chat with Firebase
        syncVideoWithFirebase()
        syncChatWithFirebase()
    }

    private fun syncVideoWithFirebase() {
        val roomId = intent.getStringExtra("room_id") ?: "YOUR_ROOM_ID"  // Get room ID from Intent

        // Listen for changes in video controls (play, pause, seek)
        roomRef.child(roomId).child("video").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playStatus = snapshot.child("play").getValue(Boolean::class.java) ?: false
                val pauseStatus = snapshot.child("pause").getValue(Boolean::class.java) ?: false
                val currentTime = snapshot.child("currentTime").getValue(Double::class.java) ?: 0.0

                // Update video controls
                if (playStatus) {
                    webView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
                } else if (pauseStatus) {
                    webView.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
                }

                // Seek to specific time
                webView.loadUrl("javascript:document.getElementsByTagName('video')[0].currentTime = $currentTime;")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun updateVideoControlsInFirebase(controlType: String, value: Any) {
        val roomId = intent.getStringExtra("room_id") ?: "YOUR_ROOM_ID"  // Get room ID dynamically
        val videoRef = roomRef.child(roomId).child("video")

        when (controlType) {
            "play" -> videoRef.child("play").setValue(value)
            "pause" -> videoRef.child("pause").setValue(value)
            "currentTime" -> videoRef.child("currentTime").setValue(value)
        }
    }

    private fun sendMessage(message: String) {
        val roomId = intent.getStringExtra("room_id") ?: "YOUR_ROOM_ID"  // Get room ID dynamically

        // Send message to Firebase
        val messageRef = roomRef.child(roomId).child("chat").push()
        messageRef.setValue(message)

        // Add the message to the chat container
        val chatMessageView = TextView(this)
        chatMessageView.text = message
        chatContainer.addView(chatMessageView)

        // Scroll chat to the bottom
        chatScrollView.post { chatScrollView.fullScroll(View.FOCUS_DOWN) }
    }

    private fun syncChatWithFirebase() {
        val roomId = intent.getStringExtra("room_id") ?: "YOUR_ROOM_ID"  // Get room ID dynamically

        // Listen for chat messages from Firebase
        roomRef.child(roomId).child("chat").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(String::class.java)
                if (message != null) {
                    // Add the new message to the chat container
                    val chatMessageView = TextView(this@WatchMovieActivity)
                    chatMessageView.text = message
                    chatContainer.addView(chatMessageView)

                    // Scroll chat to the bottom
                    chatScrollView.post { chatScrollView.fullScroll(View.FOCUS_DOWN) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }
}
