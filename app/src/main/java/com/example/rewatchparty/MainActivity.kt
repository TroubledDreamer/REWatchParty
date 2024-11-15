package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.rewatchparty.data.User
import com.example.rewatchparty.data.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.database

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var LoginButton: TextView
    private lateinit var SignInButton: TextView
    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText
    private lateinit var UserNameInput: EditText
    private lateinit var EditButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginn_activity)

        // Bind views
        LoginButton = findViewById(R.id.LoginButton)
        SignInButton = findViewById(R.id.SignInButton)
        EmailInput = findViewById(R.id.EmailInput)
        PasswordInput = findViewById(R.id.PasswordInput)
        UserNameInput = findViewById(R.id.UserNameInput)
        EditButton = findViewById(R.id.EditButton)

        // Hide EmailInput initially
        EmailInput.visibility = View.GONE
        UserNameInput.hint = "Username and Email"

        // Handle "Sign In" button click
        SignInButton.setOnClickListener {
            UserNameInput.hint = "Username"
            EmailInput.visibility = View.VISIBLE
        }

        // Handle "Login" button click
        LoginButton.setOnClickListener {
            UserNameInput.hint = "Username and Email"
            EmailInput.visibility = View.GONE
        }

        // Initialize UserViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Handle "Edit" button click (navigate to waiting room when password is entered)
        EditButton.setOnClickListener {
            val password = PasswordInput.text.toString()

            insertDataToDatabase()



            if (password.isNotEmpty()) {
                // Navigate to WaitRoomActivity once password is entered
                val intent = Intent(this, CreateRoomActivity::class.java)
                startActivity(intent)


            } else {
                // Inform the user that the password is required
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
            }


        }

        // Write a message to the database
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Testing!")
    }

    private fun insertDataToDatabase() {
        val userName = UserNameInput.text.toString()
        val email = EmailInput.text.toString()
        val password = PasswordInput.text.toString()
        val roomID = ""

        if (inputCheck(userName, email, password)) {
            // Create User object
            val user = User(0, userName, email, password, roomID)
            // Add data to database
            userViewModel.insertUser(user)
            Toast.makeText(this, "Successfully added!", Toast.LENGTH_LONG).show()

        }
    }

    private fun inputCheck(userName: String, email: String, password: String): Boolean {
        return !(userName.isEmpty() && email.isEmpty() && password.isEmpty())

    }
}
