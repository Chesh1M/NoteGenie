package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.notegenie.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.signInPageBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        mBinding.registerPageBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}