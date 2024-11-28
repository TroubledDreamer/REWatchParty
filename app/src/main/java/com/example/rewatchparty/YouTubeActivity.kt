package com.example.rewatchparty

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class YouTubeActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var currentTimeTextView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var seekButton: Button

    private lateinit var roomRef: DatabaseReference
    private var isLeader: Boolean = false
    private var roomId: String = ""
    private lateinit var userId: String
    private var userName: String = ""

    private var isVideoPlaying = false  // Flag to track if video is playing
    private var hasSyncedTime = false  // Flag to track if we have synced the currentTime once

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)

        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()
        val auth = FirebaseAuth.getInstance()

        // Get current user ID
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        userId = currentUser.uid

        // Reference to user data
        val userRef = database.reference.child("users").child(userId)

        // Fetch user data
        fetchUserData(userRef)

        // Initialize views
        webView = findViewById(R.id.youtube_webview)
        currentTimeTextView = findViewById(R.id.current_time_text_view)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        seekButton = findViewById(R.id.seek_button)

        // Get room data from Intent
        roomId = intent.getStringExtra("roomId") ?: "defaultRoomId"
        isLeader = intent.getBooleanExtra("leaderStatus", false)

        // Reference to room data in Firebase
        roomRef = database.reference.child("rooms").child(roomId)

        // Setup WebView dynamically based on Firebase data
        setupWebView()

        // Sync data with Firebase
        syncRoomData()

        // Set up button listeners
        setupButtonListeners()
    }

    private fun fetchUserData(userRef: DatabaseReference) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Retrieve user data
                    userName = snapshot.child("userName").getValue(String::class.java) ?: "Unknown User"
                    Toast.makeText(this@YouTubeActivity, "Welcome, $userName!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@YouTubeActivity, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@YouTubeActivity, "Failed to fetch user data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupWebView() {
        // Listen for the YouTube URL from Firebase
        roomRef.child("youtubeURL").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val youtubeURL = snapshot.getValue(String::class.java)
                    val videoId = extractVideoIdFromUrl(youtubeURL)
                    if (videoId != null) {
                        val embedUrl = "https://www.youtube.com/embed/$videoId?enablejsapi=1&autoplay=1" // Add autoplay=1
                        initializeWebView(embedUrl)
                    } else {
                        Toast.makeText(this@YouTubeActivity, "Invalid YouTube URL.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@YouTubeActivity, "No YouTube URL found for the room.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@YouTubeActivity, "Failed to fetch YouTube URL: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun extractVideoIdFromUrl(url: String?): String? {
        if (url == null) return null
        val regex = Regex("(?<=v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v=|&v=|\\?v=|v=)[^#&?\\n]*")
        val match = regex.find(url)
        return match?.value
    }

    private fun initializeWebView(embedUrl: String) {
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(embedUrl)

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                injectJavaScript() // Inject the script to autoplay
            }
        }

        webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun updateTime(currentTime: Double) {
                runOnUiThread {
                    currentTimeTextView.text = "Current Time: ${currentTime.toInt()} seconds"
                    // Update current time to Firebase only if video is playing
                    if (isLeader && isVideoPlaying && !hasSyncedTime) {
                        updateRoomControl("currentTime", currentTime)
                        hasSyncedTime = true  // Set flag to true after initial sync
                    }
                }
            }
        }, "Android")
    }

    private fun injectJavaScript() {
        // Inject JavaScript to autoplay video when page is loaded
        webView.loadUrl(
            "javascript:(function() {" +
                    "var video = document.getElementsByTagName('video')[0];" +
                    "if (video) {" +
                    "    video.play();" +
                    "    setInterval(function() {" +  // Periodically send current time
                    "        Android.updateTime(video.currentTime);" +
                    "    }, 1000);" +  // Update every second
                    "}" +
                    "})()"
        )
    }

    private fun setupButtonListeners() {
        playButton.setOnClickListener {
            updateRoomControl("isPaused", false)
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
            isVideoPlaying = true
            hasSyncedTime = false  // Reset sync flag when user presses play
        }

        pauseButton.setOnClickListener {
            updateRoomControl("isPaused", true)
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
            isVideoPlaying = false
        }

        seekButton.setOnClickListener {
            webView.evaluateJavascript("document.getElementsByTagName('video')[0].currentTime") { value ->
                val currentTime = value.toDoubleOrNull() ?: 0.0
                updateRoomControl("currentTime", currentTime + 30)
                webView.loadUrl("javascript:document.getElementsByTagName('video')[0].currentTime = $currentTime + 30;")
            }
        }
    }

    private fun syncRoomData() {
        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isPaused = snapshot.child("isPaused").getValue(Boolean::class.java) ?: true
                    val currentTime = snapshot.child("currentTime").getValue(Double::class.java) ?: 0.0

                    // Sync video only when it's not the leader
                    if (!isLeader) {
                        // Apply the currentTime only if it's different from the current position
                        if (!hasSyncedTime) {
                            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].currentTime = $currentTime;")
                            hasSyncedTime = true  // Mark as synced
                        }

                        if (isPaused) {
                            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
                            isVideoPlaying = false
                        } else {
                            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
                            isVideoPlaying = true
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@YouTubeActivity, "Failed to sync data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRoomControl(controlName: String, value: Any) {
        // Log the action to see if the method is called
        Log.d("YouTubeActivity", "Updating $controlName to $value")

        roomRef.child(controlName).setValue(value).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Successfully updated Firebase
                Log.d("YouTubeActivity", "$controlName updated successfully.")
            } else {
                // Failure to update Firebase
                Log.e("YouTubeActivity", "Failed to update $controlName: ${task.exception?.message}")
            }
        }
    }
}
