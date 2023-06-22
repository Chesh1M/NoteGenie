package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class SettingsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)

        val langBtn = findViewById<ImageButton>(R.id.fab)
        langBtn.setOnClickListener{
            val intent = Intent(this, LanguagesSetPage::class.java)
            startActivity(intent)
        }
    }
}