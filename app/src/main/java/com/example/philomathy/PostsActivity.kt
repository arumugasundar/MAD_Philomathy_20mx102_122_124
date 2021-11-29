package com.example.philomathy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.philomathy.models.Posts
import com.example.philomathy.models.Users
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


private const val TAG = "PostsActivity"
private const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {

    private lateinit var firestoreDb : FirebaseFirestore
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private  lateinit var posts : MutableList<Posts>
    private lateinit var adapter: PostsAdapter
    private  lateinit var rvPosts : RecyclerView
    private  lateinit var plusbutton : FloatingActionButton
    private var signedInUser: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        rvPosts = findViewById(R.id.rvPosts)
        firestoreDb = FirebaseFirestore.getInstance()
        posts = mutableListOf()
        adapter = PostsAdapter(this, posts)
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        plusbutton = findViewById(R.id.upload_button)

        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(Users::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener {exception ->
                Log.i(TAG, "Failure fetching signed in User", exception)
            }

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
                    startActivity(Intent(this, PostsActivity::class.java))
                }

                R.id.myProfile ->
                {
                    Toast.makeText(this, "Clicked Profile", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                    startActivity(intent)
                }
                R.id.uploadImage ->
                {
                    Toast.makeText(this, "Upload Image", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UploadActivity::class.java)
                    startActivity(intent)
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




        var postReference = firestoreDb
            .collection("Uploads")
            .limit(20)
            .orderBy("creationtime", Query.Direction.DESCENDING)


        val username = intent.getStringExtra(EXTRA_USERNAME)
        if(username != null)
        {
            supportActionBar?.title = username
            postReference = postReference.whereEqualTo("user.username", username)
        }


        postReference.addSnapshotListener{ snapshot, exception ->
            if(exception != null || snapshot ==null)
            {
                Log.e(TAG, "Exception when querying posts", exception)
                return@addSnapshotListener
            }

            val postList = snapshot.toObjects(Posts :: class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
            for(post in postList)
                Log.i(TAG, "Post: $post")
        }




        plusbutton.setOnClickListener{

            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}










