package com.example.rewatchparty

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth

class YouTubeActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var currentTimeTextView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var seekButton: Button

    private lateinit var room: Room
    private lateinit var database: FirebaseDatabase
    private lateinit var roomRef: DatabaseReference

    private var isLeader: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        // Initialize WebView and buttons
        webView = findViewById(R.id.youtube_webview)
        currentTimeTextView = findViewById(R.id.current_time_text_view)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        seekButton = findViewById(R.id.seek_button)

        // Initialize WebView settings
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Get room ID and password from the Intent (simulating the leader's room creation)
        val roomId = intent.getStringExtra("roomId") ?: "defaultRoomId"
        val password = intent.getStringExtra("password") ?: "defaultPassword"

        // Get the leader status from Intent or set as default
        isLeader = intent.getBooleanExtra("leaderStatus", false)

        // Set the room reference in the Firebase Realtime Database
        roomRef = database.reference.child("rooms").child(roomId)

        // Create the Room object and initialize the Firebase data
        room = Room(
            roomId = roomId,
            password = password,
            leaderStatus = isLeader
        )

        // Load YouTube embed URL
        val videoId = "XVvfueWFC_Q"  // Replace with the actual video ID
        val embedUrl = "https://www.youtube.com/embed/$videoId?enablejsapi=1"
        webView.loadUrl(embedUrl)

        // WebViewClient to keep navigation inside the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                trackVideoTime()
            }
        }

        // JavaScript Interface to communicate with the WebView and update the UI
        webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun updateTime(currentTime: Double) {
                runOnUiThread {
                    currentTimeTextView.text = "Current Time: ${currentTime.toInt()} seconds"
                    // Update Firebase with the current time
                    updateRoomTime(currentTime)
                }
            }
        }, "Android")

        // Firebase listener to sync video controls from Firebase
        syncRoomData()

        // Play button: Play the video
        playButton.setOnClickListener {
            updateRoomControl("isPaused", false)
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
        }

        // Pause button: Pause the video
        pauseButton.setOnClickListener {
            updateRoomControl("isPaused", true)
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
        }

        // Seek button: Seek to 30 seconds
        seekButton.setOnClickListener {
            val seekToTime = 30.0
            updateRoomControl("currentTime", seekToTime)
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].currentTime = $seekToTime;")
        }
    }

    private fun trackVideoTime() {
        // Inject JavaScript to track video time every second and call Android method to update UI
        webView.loadUrl(
            "javascript:(function() {" +
                    "setInterval(function() {" +
                    "  var video = document.getElementsByTagName('video')[0];" +
                    "  if (video) {" +
                    "    var currentTime = video.currentTime;" +
                    "    Android.updateTime(currentTime);" +  // Call Java method to update UI
                    "  }" +
                    "}, 1000);" +  // Update every 1 second
                    "})()"
        )
    }

    private fun updateRoomControl(controlName: String, value: Any) {
        // Update the room control in Firebase
        roomRef.child(controlName).setValue(value)
    }

    private fun updateRoomTime(currentTime: Double) {
        // Update the current time in the Firebase database
        roomRef.child("currentTime").setValue(currentTime)
    }

    private fun syncRoomData() {
        // Listen for changes in the room data from Firebase
        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val roomData = snapshot.getValue(Room::class.java)
                    roomData?.let {
                        // Sync the video controls across users
                        if (it.isPaused) {
                            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
                        } else {
                            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
                        }
                        webView.loadUrl("javascript:document.getElementsByTagName('video')[0].currentTime = ${it.currentTime};")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@YouTubeActivity, "Failed to sync data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
