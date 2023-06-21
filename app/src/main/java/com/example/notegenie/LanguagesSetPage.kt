package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class LanguagesSetPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages_set_page)

        val langBtn = findViewById<ImageButton>(R.id.backBtn)
        langBtn.setOnClickListener {
            val intent = Intent(this, SettingsPage::class.java)
            startActivity(intent)
        }
    }
}