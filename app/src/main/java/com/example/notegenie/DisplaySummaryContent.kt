package com.example.notegenie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.notegenie.databinding.ActivityDisplaySummaryContentBinding
import com.example.notegenie.databinding.ActivitySummaryCardBinding

class DisplaySummaryContent : AppCompatActivity() {

    // Initializing a binding
    private lateinit var binding: ActivityDisplaySummaryContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplaySummaryContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Getting the variables from the previous activity
        var summaryTitle =  intent.getStringExtra("Summary Title")
        val summaryLastEditDate = intent.getStringExtra("Edit Date")
        val summaryContent = intent.getStringExtra("Summary Content")

        // Setting the title
        summaryTitle = "Summary Of:\n" + summaryTitle

        // Setting up the text views accordingly
        binding.summaryTextView.text = summaryTitle
        binding.summaryContentsView.text = summaryContent

    }
}