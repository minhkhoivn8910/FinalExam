package com.example.finalexam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony.Sms.Intents
import android.widget.Toast
import com.example.finalexam.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
            val name = binding.txtName.text.toString()
            val birth = binding.txtBirth.text.toString()
            val email = binding.txtEmail.text.toString()
            val pass = binding.txtPass.text.toString()
            val repass = binding.txtRePass.text.toString()

            if(name.isEmpty()){
                Toast.makeText(this,"Please enter your name!",Toast.LENGTH_LONG).show()
            }
            else if(birth.isEmpty()){
                Toast.makeText(this,"Please enter your birthday!",Toast.LENGTH_LONG).show()
            }
            else if (email.isEmpty()){
                Toast.makeText(this,"Please enter your email!",Toast.LENGTH_LONG).show()
            }
            else if (pass.isEmpty()){
                Toast.makeText(this,"Please enter your password!",Toast.LENGTH_LONG).show()
            }
            else if (repass.isEmpty()){
                Toast.makeText(this,"Please enter your confirm password!",Toast.LENGTH_LONG).show()
            }
            else if (pass != repass){
                Toast.makeText(this,"Your confirm password is incorrect!",Toast.LENGTH_LONG).show()
            }
            else{
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
                    if(it.isSuccessful){
                        uploadData()
                        Toast.makeText(this,"Register successful!",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,Login::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.txtBackLogin.setOnClickListener {
            val itLogin = Intent(this,Login::class.java)
            startActivity(itLogin)
        }
    }
    private fun uploadData() {
        val email = binding.txtEmail.text.toString()
        val name = binding.txtName.text.toString()
        val birth = binding.txtBirth.text.toString()

        val user = User(uEmail = email, uName = name, uBirth = birth, uImage = "none")

        val userReference = FirebaseDatabase.getInstance().getReference("Users").push()
        userReference.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = userReference.key

                userReference.child("uId").setValue(userId)

                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}