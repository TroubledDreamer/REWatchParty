package com.example.rewatchparty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.rewatchparty.data.User
import com.example.rewatchparty.data.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

//    private lateinit var userViewModel: UserViewModel

    private lateinit var LoginButton: TextView
    private lateinit var SignInButton: TextView
    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText
    private lateinit var UserNameInput: EditText
    private lateinit var EditButton: Button
    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

//        FirebaseApp.initializeApp(this);
//        val database3 = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance()

        // Bind views
        LoginButton = findViewById(R.id.LoginButton)
        SignInButton = findViewById(R.id.SignInButton)
        EmailInput = findViewById(R.id.EmailInput)
        PasswordInput = findViewById(R.id.PasswordInput)
        UserNameInput = findViewById(R.id.UserNameInput)
        EditButton = findViewById(R.id.EditButton)

        // Pre-fill the inputs with default credentials for testing
//        UserNameInput.setText("carlyonja@gmail.com")
        UserNameInput.setText("carlionjones@gmail.com")

        EmailInput.setText("carlionjones@gmail.com")
        PasswordInput.setText("123456")

        // Hide EmailInput initially
        EmailInput.visibility = View.GONE
        UserNameInput.hint = "Username and Email"

        // Handle "Sign In" button click (switch to login mode)
        SignInButton.setOnClickListener {
            UserNameInput.hint = "Username"
            EmailInput.visibility = View.VISIBLE
        }

        // Handle "Login" button click (switch to sign-up mode)
        LoginButton.setOnClickListener {
            UserNameInput.hint = "Username and Email"
            EmailInput.visibility = View.GONE
        }

        // Initialize UserViewModel
//        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Handle "Edit" button click (sign-in or sign-up)
        EditButton.setOnClickListener {
            val password = PasswordInput.text.toString()
            val userName = UserNameInput.text.toString()
            val email = EmailInput.text.toString()

            if (inputCheck(userName, email, password)) {
                // Check if user is signing in or signing up
                if (isSignInMode()) {
                    signInUser(email, password)
                } else {
                    createUserInAuth(userName, email, password)
                }
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inputCheck(userName: String, email: String, password: String): Boolean {
        return !(userName.isEmpty() || email.isEmpty() || password.isEmpty())
    }

    private fun isSignInMode(): Boolean {
        // In sign-in mode, UserNameInput's hint is "Username"
        return UserNameInput.hint == "Username"
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(baseContext, "Authentication successful.", Toast.LENGTH_SHORT).show()
                        navigateToCreateOrJoinWPActivity()
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createUserInAuth(userName: String, email: String, password: String) {
        // Create a new user using Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User created successfully, now store the user data in Firestore
                    val user = auth.currentUser
                    val userId = user?.uid ?: return@addOnCompleteListener

                    // Create User object for local database (ViewModel)
                    val userObj = User(0, userName, email, password, "")
//                    userViewModel.insertUser(userObj)

                    // Create Firestore document with user data
                    val userMap = hashMapOf(
                        "userName" to userName,
                        "email" to email,
                        "password" to password,
                        "roomID" to ""
                    )

                    db.collection("users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Successfully added!", Toast.LENGTH_SHORT).show()
                            navigateToCreateOrJoinWPActivity()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to add user to Firestore.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If creating user failed, show a failure message
                    Toast.makeText(this, "User creation failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToCreateOrJoinWPActivity() {
        val intent = Intent(this, CreateOrJoinWPActivity::class.java)
        startActivity(intent)
        finish() // Optionally finish the current activity
    }
}
