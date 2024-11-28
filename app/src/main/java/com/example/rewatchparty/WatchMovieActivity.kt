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
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatContainer: LinearLayout
    private lateinit var chatScrollView: ScrollView

    private lateinit var roomId: String
    private val database = FirebaseDatabase.getInstance()
    private val roomRef = database.getReference("rooms")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.watch_movie)

        // Initialize views
        webView = findViewById(R.id.youtube_webview)
        webView.addJavascriptInterface(videoStateInterface, "Android")

        chatInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.send_button)
        chatContainer = findViewById(R.id.chat_container)
        chatScrollView = findViewById(R.id.chat_scroll_view)

        // Get the roomId passed from URLMovieSelect
        roomId = intent.getStringExtra("roomId") ?: ""

        // WebView settings for JavaScript and loading YouTube video
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Load the YouTube video using the roomId
        loadVideoFromFirebase()

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

    private fun loadVideoFromFirebase() {
        // Get the YouTube URL from Firebase for the given roomId
        roomRef.child(roomId).child("youtubeURL").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val youtubeURL = snapshot.getValue(String::class.java)
                if (!youtubeURL.isNullOrEmpty()) {
                    // Embed the YouTube video in WebView
                    val embedUrl = "https://www.youtube.com/embed/${youtubeURL.substringAfterLast("/")}?enablejsapi=1&autoplay=0"
                    webView.loadUrl(embedUrl)

                    // Inject JavaScript to listen to video state changes
                    webView.loadUrl("""
                        (function() {
                            var player;
                            function onYouTubeIframeAPIReady() {
                                player = new YT.Player('youtube_webview', {
                                    events: {
                                        'onStateChange': onPlayerStateChange
                                    }
                                });
                            }
                            function onPlayerStateChange(event) {
                                if (event.data == YT.PlayerState.PLAYING) {
                                    Android.onPlay();  // Notify Android when video is playing
                                } else if (event.data == YT.PlayerState.PAUSED) {
                                    Android.onPause();  // Notify Android when video is paused
                                }
                            }
                            var script = document.createElement('script');
                            script.src = "https://www.youtube.com/iframe_api";
                            var firstScriptTag = document.getElementsByTagName('script')[0];
                            firstScriptTag.parentNode.insertBefore(script, firstScriptTag);
                        })();
                    """)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (e.g., URL not found)
                Toast.makeText(this@WatchMovieActivity, "Failed to load video.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // JavaScript Interface to communicate from WebView to Android
    private val videoStateInterface = object {
        @android.webkit.JavascriptInterface
        fun onPlay() {
            updateVideoControlsInFirebase("play", true)
            updateVideoControlsInFirebase("pause", false) // Ensure pause is false when playing
        }

        @android.webkit.JavascriptInterface
        fun onPause() {
            updateVideoControlsInFirebase("play", false)
            updateVideoControlsInFirebase("pause", true) // Set pause to true when paused
        }
    }

    // Add the JavaScript interface to WebView
    init {
        webView.addJavascriptInterface(videoStateInterface, "Android")
    }

    private fun syncVideoWithFirebase() {
        // Listen for changes in video controls (play, pause, seek) in Firebase
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
        val videoRef = roomRef.child(roomId).child("video")

        when (controlType) {
            "play" -> videoRef.child("play").setValue(value)
            "pause" -> videoRef.child("pause").setValue(value)
            "currentTime" -> videoRef.child("currentTime").setValue(value)
        }
    }

    private fun sendMessage(message: String) {
        // Send message to Firebase under the chat node for this roomId
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
        // Listen for chat messages in Firebase for this roomId
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
