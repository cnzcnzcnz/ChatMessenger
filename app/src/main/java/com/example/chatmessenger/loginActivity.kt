package com.example.chatmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class loginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val submitButton = findViewById(R.id.submit_button) as Button

        submitButton.setOnClickListener(){
            val password = password_login.text.toString()
            val email = email_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).isSuccessful
        }
    }
}
