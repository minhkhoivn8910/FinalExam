package com.example.finalexam

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.finalexam.databinding.ActivitySettingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class Setting : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        userId = intent.getStringExtra("UserId") ?: ""
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        if (userId.isEmpty()) {
            // Handle the case where userId is not provided or is empty
            Toast.makeText(this, "User ID not available", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // Load user data
            loadData()
        }

        binding.btnUpdate.setOnClickListener {
            // Handle update button click
            updateUserInformation(userId)
        }

        binding.btnChangePass.setOnClickListener {
            // Handle change password button click
        }

        binding.imgUser.setOnClickListener {
            // Launch the image picker when the user clicks on the profile picture
            pickImage.launch("image/*")
        }
        binding.btnChangePass.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun loadData() {
        val query: Query = databaseReference.child(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                if (user != null) {
                    // Populate UI with user data
                    binding.txtUserName.setText(user.uName.orEmpty())
                    binding.txtUserBirthday.setText(user.uBirth.orEmpty())

                    // Load user image if available
                    if (user.uImage != "none") {
                        Glide.with(this@Setting).load(user.uImage).into(binding.imgUser)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Glide.with(this).load(uri).into(binding.imgUser)
        }
    }

    private fun updateUserInformation(userId: String) {
        val newName = binding.txtUserName.text.toString()
        val newBirth = binding.txtUserBirthday.text.toString()

        val query: Query = databaseReference.child(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                val existingUser = dataSnapshot.getValue(User::class.java)

                if (existingUser != null) {

                    val updatedUser = User(
                        existingUser.uId,
                        existingUser.uEmail,
                        newName,
                        newBirth,
                        selectedImageUri?.toString() ?: existingUser.uImage ?: "none"
                    )

                    databaseReference.child(userId).setValue(updatedUser)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@Setting, "User information updated!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@Setting, "Failed to update user information", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun showChangePasswordDialog() {
        val dialog = ChangePasswordDialog()
        dialog.show(supportFragmentManager, "ChangePasswordDialog")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Navigate back when the up button is clicked
                return true
            }
            // Add other menu item handling if needed
            else -> return super.onOptionsItemSelected(item)
        }
    }

}
