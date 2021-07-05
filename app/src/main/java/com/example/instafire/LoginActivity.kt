package com.example.instafire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            goPostActivity()
        }
        var blogin = findViewById<Button>(R.id.login)
        blogin.setOnClickListener {
            blogin.isEnabled = false
            var email = emailid.text.toString()
            var pass = password.text.toString()
            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            //Firebase authentication use
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                blogin.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    goPostActivity()
                } else {
                    Log.i(TAG, "Signin with email failed", task.exception)
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    private fun goPostActivity() {
        Log.i(TAG, "goonPostactivity")
        val intent = Intent(this,PostActivity::class.java)
        startActivity(intent)
        finish()

    }
}
