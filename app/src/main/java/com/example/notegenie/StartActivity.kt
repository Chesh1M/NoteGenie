package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notegenie.databinding.ActivityStartBinding
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityStartBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        mBinding.signInPageBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        mBinding.registerPageBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}