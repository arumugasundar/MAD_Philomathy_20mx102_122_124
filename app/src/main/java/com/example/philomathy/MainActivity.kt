package com.example.philomathy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt = findViewById<TextView>(R.id.textView2)

        val auth = Firebase.auth
        val user = auth.currentUser

        txt.text = user.toString()

        Toast.makeText(baseContext, "$user", Toast.LENGTH_SHORT).show()
        print(user)
    }
}