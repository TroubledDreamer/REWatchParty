package com.example.rewatchparty

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.example.rewatchparty.ui.theme.REWatchPartyTheme
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)

    private lateinit var LoginButton: TextView
    private lateinit var SignInButton: TextView
    private lateinit var EmailInput: EditText
    private lateinit var PasswordInput: EditText
    private lateinit var UserNameInput: EditText
    private lateinit var EditButton: Button








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        LoginButton = findViewById(R.id.LoginButton)
        SignInButton = findViewById(R.id.SignInButton)
        EmailInput = findViewById(R.id.EmailInput)
        PasswordInput = findViewById(R.id.PasswordInput)
        UserNameInput = findViewById(R.id.UserNameInput)
        EditButton = findViewById(R.id.EditButton)


        EditButton.setOnClickListener {
            // Get input values from EditTexts
            val email = EmailInput.text.toString()
            val password = PasswordInput.text.toString()
            val userName = UserNameInput.text.toString()


            // take this out
            Toast.makeText(
                this,
                "Email: $email\nPassword: $password\nUsername: $userName",
                Toast.LENGTH_LONG
            ).show()


        }

        EmailInput.visibility = View.GONE
        UserNameInput.hint = "Username and Email"



        SignInButton.setOnClickListener {
            // Show the email input when "Sign In" is clicked
            UserNameInput.hint = "Username"
            EmailInput.visibility = View.VISIBLE
        }

        LoginButton.setOnClickListener {
            // Show the email input when "Sign In" is clicked
            UserNameInput.hint = "Username and Email"
            EmailInput.visibility = View.GONE
        }





        }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    REWatchPartyTheme {
        Greeting("Android")
    }
}