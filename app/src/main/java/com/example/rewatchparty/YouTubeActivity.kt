package com.example.rewatchparty

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class YouTubeActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var currentTimeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)  // Make sure this layout is correct

        // Initialize WebView and buttons
        webView = findViewById(R.id.youtube_webview)
        currentTimeTextView = findViewById(R.id.current_time_text_view)

        val playButton: Button = findViewById(R.id.play_button)
        val pauseButton: Button = findViewById(R.id.pause_button)
        val seekButton: Button = findViewById(R.id.seek_button)

        // Enable JavaScript in WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Set up WebViewClient to ensure navigation stays inside the WebView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                // Start tracking video time once the page is fully loaded
                trackVideoTime()
            }
        }

        // Load YouTube embed URL (Replace with your actual YouTube video ID)
        val videoId = "XVvfueWFC_Q"  // Your YouTube video ID (without additional query params)
        val embedUrl = "https://www.youtube.com/embed/$videoId?enablejsapi=1"
        webView.loadUrl(embedUrl)

        // Add JavaScript Interface for communication between JavaScript and Kotlin
        webView.addJavascriptInterface(object {
            @android.webkit.JavascriptInterface
            fun updateTime(currentTime: Double) {
                runOnUiThread {
                    // Update the UI with the current time of the video
                    currentTimeTextView.text = "Current Time: ${currentTime.toInt()} seconds"
                }
            }
        }, "Android")

        // Play button: Play the video
        playButton.setOnClickListener {
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].play();")
        }

        // Pause button: Pause the video
        pauseButton.setOnClickListener {
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].pause();")
        }

        // Seek button: Seek to 30 seconds
        seekButton.setOnClickListener {
            webView.loadUrl("javascript:document.getElementsByTagName('video')[0].currentTime = 30;")
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
}
