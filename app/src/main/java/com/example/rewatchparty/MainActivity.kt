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
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var LoginButton: TextView
    private lateinit var SignInButton: TextView
    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText
    private lateinit var UserNameInput: EditText
    private lateinit var EditButton: Button
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
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
            val userName = UserNameInput.text.toString()
            val email = EmailInput.text.toString()

            insertDataToDatabase()

            if (password.isNotEmpty()) {
                val intent = Intent(this, CreateOrJoinWPActivity::class.java)
                startActivity(intent)
            } else {
                // Inform the user that the password is required
                Toast.makeText(this, "Please enter all your credentials.", Toast.LENGTH_SHORT).show()
            }

}

    }
    private fun inputCheck(userName: String, email: String, password: String): Boolean {
        return !(userName.isEmpty() && email.isEmpty() && password.isEmpty())

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
            val userMap = hashMapOf(
                "userName" to userName,
                "email" to email,
                "password" to password,
                "roomID" to roomID
            )

            val userId = auth.currentUser!!.uid

            db.collection("users").document(userId).set(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully added!", Toast.LENGTH_SHORT).show()
                    EmailInput.text.clear()
                    PasswordInput.text.clear()
                    UserNameInput.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add!", Toast.LENGTH_SHORT).show()
                }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user2 = auth.currentUser
                    Toast.makeText(
                        baseContext, "Authentication successful.",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                }
        }
    }




}
