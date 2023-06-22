package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.notegenie.databinding.ActivitySettingsPageBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsPage : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)
        firebaseAuth = FirebaseAuth.getInstance()

        // Status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)


        val langBtn = findViewById<ImageButton>(R.id.fab)
        langBtn.setOnClickListener{
            val intent = Intent(this, LanguagesSetPage::class.java)
            startActivity(intent)
        }

        val exitSettingsBtn = findViewById<Button>(R.id.exitSettingsBtn)
        exitSettingsBtn.setOnClickListener{
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener{
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            firebaseAuth.signOut()
            Toast.makeText(this, "Sign out success!", Toast.LENGTH_SHORT).show()
        }
    }
}