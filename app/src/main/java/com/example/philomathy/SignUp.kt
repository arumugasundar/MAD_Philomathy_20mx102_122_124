package com.example.philomathy

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.philomathy.models.Posts
import com.example.philomathy.models.Users
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore


//getCurrentUser

class SignUp : AppCompatActivity() {

    private lateinit var signupbutton : Button
    private lateinit var usernameInput : TextInputLayout
    private lateinit var emailInput : TextInputLayout
    private lateinit var passwordInput : TextInputLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var loginbutton : Button

    //private lateinit var newuser : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        firestoreDb = FirebaseFirestore.getInstance()
        signupbutton = findViewById(R.id.loginButton)
        emailInput = findViewById(R.id.textUsernameLayout)
        passwordInput = findViewById(R.id.textPasswordInput)
        usernameInput = findViewById(R.id.textUsername1Layout)
        loginbutton = findViewById(R.id.loginredirect)

        signupbutton.setOnClickListener {

            onsignUpClicked()
        }

        loginbutton.setOnClickListener {

            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
        }

        emailInput
            .editText
            ?.addTextChangedListener(createTextWatcher(emailInput))
        passwordInput
            .editText
            ?.addTextChangedListener(createTextWatcher(passwordInput))
        usernameInput
            .editText
            ?.addTextChangedListener(createTextWatcher(usernameInput)
            )


    }

    private fun onsignUpClicked() {
        val username: String = usernameInput.editText?.text.toString()
        val password: String = passwordInput.editText?.text.toString()
        val email : String = emailInput.editText?.text.toString()

        when {
            username.isEmpty() -> {
                usernameInput.error = "Username must not be empty"
            }
            password.isEmpty() -> {
                passwordInput.error = "Password must not be empty"
            }
            email.isEmpty() ->
            {
                emailInput.error = "Email must not be empty"
            }

            password.length < 6 -> {
                passwordInput.error = "Minimum of 6 Characters"
            }

            else -> {
                performSignup()
            }
        }

    }

    private fun performSignup() {
        val email = findViewById<TextInputLayout>(R.id.textUsernameLayout).editText?.text.toString()
        val password =
            findViewById<TextInputLayout>(R.id.textPasswordInput).editText?.text.toString()
        val username = findViewById<TextInputLayout>(R.id.textUsername1Layout).editText?.text.toString()


        signup(email, password, username)

    }

    private fun signup(email: String, password: String, username : String) {

        auth = Firebase.auth

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")

                    val user = auth.currentUser
                    firestoreDb = FirebaseFirestore.getInstance()
                    Toast.makeText(baseContext, "Success", Toast.LENGTH_SHORT).show()

//                    var db = FirebaseFirestore.getInstance()
//                    val user_details = hashMapOf(
//
//                        "username" to "{$username}",
//                        "email" to "{$email}"
//
//                    )
//
//                    db.collection("users").document()
//                        .set(user_details)
//                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
//                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                   val uid = user?.uid.toString()

                    val signup_user = Users(
                        usernameInput.editText?.text.toString(),
                        emailInput.editText?.text.toString()
                    )
                    firestoreDb.collection("users").document(uid).set(signup_user)




                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }


    }


    private fun createTextWatcher(textPasswordInput: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int, count: Int, after: Int
            ) {
                // not needed
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int, before: Int, count: Int
            ) {
                textPasswordInput.error = null
            }

            override fun afterTextChanged(s: Editable) {
                // not needed
            }
        }
    }




}