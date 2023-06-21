package com.example.notegenie

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notegenie.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySignInBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySignInBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()

        mBinding.loginBtn.setOnClickListener {
            val email = mBinding.emailEt.text.toString()
            val password = mBinding.passwordEt.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, HomePage::class.java)
                        startActivity(intent)
                        Toast.makeText(this, "Sign in success!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Incorrect Email or Password keyed in!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.loginWithGoogleBtn.setOnClickListener {
            // To be implemented
            {}
        }

        mBinding.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        setContentView(mBinding.root)
    }
}