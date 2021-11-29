package com.example.philomathy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.drawerlayout.widget.DrawerLayout
import com.example.philomathy.models.Posts
import com.example.philomathy.models.Users
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val TAG = "CreateActivity"
private const val PICK_PHOTO_CODE = 1234
class UploadActivity : AppCompatActivity() {

    private var signedInUser: Users? = null
    private var photoUri: Uri? = null
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var uploadbutton : ImageButton
    private lateinit var postbutton : Button
    private lateinit var etDescription : TextInputLayout
    private lateinit var title : TextInputLayout
    private lateinit var imageView : ImageView
    private lateinit var actionBarToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        uploadbutton = findViewById(R.id.upload_button)
        postbutton  = findViewById(R.id.postbutton)
        etDescription = findViewById(R.id.content_input)

        title = findViewById(R.id.textTitle)


        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.navView)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navHome ->
                {
                    val intent = Intent(this, PostsActivity::class.java)
                    startActivity(intent)
                    finish()
                }


                R.id.myProfile ->
                    Toast.makeText(this, "Clicked Profile", Toast.LENGTH_SHORT).show()

                R.id.uploadImage ->
                {
                    Toast.makeText(this, "Already in Upload Page", Toast.LENGTH_SHORT).show()
                }

                R.id.logout ->
                {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, SignUp::class.java)
                    startActivity(intent)
                    finish()
                }

            }
            true
        }


        storageReference = FirebaseStorage.getInstance().reference
        firestoreDb = FirebaseFirestore.getInstance()
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(Users::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG, "Failure fetching signed in user", exception)
            }

        uploadbutton.setOnClickListener {
            Log.i(TAG, "Open up image picker on device")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            if (imagePickerIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(imagePickerIntent, PICK_PHOTO_CODE)
            }
        }

        postbutton.setOnClickListener {
            handleSubmitButtonClick()
        }
    }

    private fun handleSubmitButtonClick() {

        imageView = findViewById(R.id.uploaded_view)

        if (photoUri == null) {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (etDescription.isEmpty()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser == null) {
            Toast.makeText(this, "No signed in user, please wait", Toast.LENGTH_SHORT).show()
            return
        }





        postbutton.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = storageReference.child("images/${System.currentTimeMillis()}-photo.jpg")
        // Upload photo to Firebase Storage
        photoReference.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                Log.i(TAG, "uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                // Retrieve image url of the uploaded image
                photoReference.downloadUrl
            }.continueWithTask { downloadUrlTask ->
                // Create a post object with the image URL and add that to the posts collection
                val post = Posts(
                    etDescription.editText?.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser,
                    title.editText?.text.toString()
                    )
                firestoreDb.collection("Uploads").add(post)
            }.addOnCompleteListener { postCreationTask ->
                postbutton.isEnabled = true
                if (!postCreationTask.isSuccessful) {
                    Log.e(TAG, "Exception during Firebase operations", postCreationTask.exception)
                    Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()
                }
              //  etDescription.editText?.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
//                val profileIntent = Intent(this, ProfileActivity::class.java)
//                profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
               // startActivity(profileIntent)
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        imageView = findViewById(R.id.uploaded_view)

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                Log.i(TAG, "photoUri $photoUri")
                imageView.setImageURI(photoUri)
            }

            else {
                Toast.makeText(this, "Image picker action canceled", Toast.LENGTH_SHORT).show()
            }

        }
    }


}