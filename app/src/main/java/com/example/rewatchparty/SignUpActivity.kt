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

class SignUpActivity : ComponentActivity() {

    private lateinit var userViewModel: UserViewModel

    private lateinit var LoginButton: TextView
    private lateinit var SignUpButton: TextView
    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText
    private lateinit var UserNameInput: EditText
    private lateinit var EditButton: Button
    private lateinit var PasswordConfirm: EditText
    private lateinit var auth: FirebaseAuth
    private var db = Firebase.firestore
    private val TAG = "SignUpActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

//        FirebaseApp.initializeApp(this);
//        val database3 = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance()

        // Bind views
        LoginButton = findViewById(R.id.LoginButton)
        SignUpButton = findViewById(R.id.SignUpButton)
        EmailInput = findViewById(R.id.EmailInput)
        PasswordInput = findViewById(R.id.PasswordInput)
        UserNameInput = findViewById(R.id.UserNameInput)
        EditButton = findViewById(R.id.EditButton)
        PasswordConfirm = findViewById(R.id.confirmPassword)


        // Handle "Login" button click
        LoginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        SignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Initialize UserViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Handle "Edit" button click (navigate to waiting room when password is entered)
        EditButton.setOnClickListener {
            val password = PasswordInput.text.toString()
            val userName = UserNameInput.text.toString()
            val email = EmailInput.text.toString()
            val passwordConfirm = PasswordConfirm.text.toString()

            if (password.isNotEmpty() && passwordConfirm.isNotEmpty() && inputCheck(userName, email, password, passwordConfirm)) {
                if (password == passwordConfirm) {
                    insertDataToDatabase()
                    val intent = Intent(this, CreateOrJoinWPActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Inform the user that the passwords do not match
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Inform the user that the password is required
                Toast.makeText(this, "Please enter all your credentials.", Toast.LENGTH_SHORT).show()
            }

}

    }
    private fun inputCheck(userName: String, email: String, password: String, passwordConfirm: String): Boolean {
        return !(userName.isEmpty() && email.isEmpty() && password.isEmpty() && password.isEmpty())

    }
    private fun insertDataToDatabase() {
        val userName = UserNameInput.text.toString()
        val email = EmailInput.text.toString()
        val password = PasswordInput.text.toString()
        val passwordConfirm = PasswordConfirm.text.toString()
        val roomID = ""

        if (inputCheck(userName, email, password, passwordConfirm)) {
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

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext, "Authentication successful.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

                }
                }
        }
