package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Changing intent to another page
        val goToSummaryPage = Intent(this, SummaryPage::class.java)
        startActivity(goToSummaryPage)
    }
}