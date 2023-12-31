package com.example.finalexam

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.finalexam.databinding.ActivityLoginBinding
import com.example.finalexam.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnLogin.setOnClickListener{
            val email = binding.txtEmail.text.toString()
            val pass = binding.txtPass.text.toString()
            if(email.isEmpty()){
                Toast.makeText(this,"Please enter your email", Toast.LENGTH_LONG).show()
            }
            else if(pass.isEmpty()){
                Toast.makeText(this,"Please enter your password",Toast.LENGTH_LONG).show()
            }
            else{
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener{
                    if (it.isSuccessful){
                        val firebaseHelper = FirebaseHelper()
                        firebaseHelper.getUserIdByEmail(email) { userId ->
                            if (userId != null) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("UserId", userId)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    else{
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.txtRegister.setOnClickListener {
            val itRegister = Intent(this, Register::class.java)
            startActivity(itRegister)
        }

        binding.txtForgot.setOnClickListener {
            val build = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.dialog_forgot,null)
            val forgotEmail= view.findViewById<EditText>(R.id.editTextText)

            build.setView(view)
            val dialog = build.create()

            view.findViewById<Button>(R.id.btnCancel).setOnClickListener{
                dialog.dismiss()
            }
            view.findViewById<Button>(R.id.btnSubmitForgot).setOnClickListener{
                checkEmail(forgotEmail)
                dialog.dismiss()
            }
            if(dialog.window != null){
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        }
    }

    private fun checkEmail(email: EditText){
        if(email.text.toString().isEmpty()){
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return
        }
        firebaseAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                Toast.makeText(this,"Please check your email!",Toast.LENGTH_SHORT).show()
            }
        }
    }
}