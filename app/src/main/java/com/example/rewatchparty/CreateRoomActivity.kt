package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.database.database
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image


class CreateRoomActivity : AppCompatActivity() {

    private lateinit var createWPText: TextView;
    private lateinit var WPIDEditText: EditText;
    private lateinit var WPPasswordEditText: EditText;
    private lateinit var userSeekBar: SeekBar;
    private lateinit var textView4: TextView;
    private lateinit var CreateWPButton: ImageButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_watch_party);

        createWPText = findViewById(R.id.createWPText);
        WPIDEditText = findViewById(R.id.WPIDEditText);
        userSeekBar = findViewById(R.id.userSeekBar);
        textView4 = findViewById(R.id.textView4);
        CreateWPButton = findViewById(R.id.CreateWPButton);
        WPPasswordEditText = findViewById(R.id.WPPasswordEditText);

        CreateWPButton.setOnClickListener {
            val wpID = WPIDEditText.text.toString().trim();
            val wpPassword = WPPasswordEditText.text.toString().trim();
            val userCount = userSeekBar.progress;

        }
    }
}
