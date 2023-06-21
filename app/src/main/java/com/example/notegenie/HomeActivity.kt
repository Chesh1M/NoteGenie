package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notegenie.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

// Replace with actual home page  activity

class HomeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityHomeBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        mBinding.logoutBtn.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            // Log the user out
            FirebaseAuth.getInstance().signOut()
        }
    }
}