package com.example.notegenie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.notegenie.databinding.ActivitySummaryPageBinding

class SummaryPage : AppCompatActivity() {

    // Global Variables
    val LIST_OF_LANGUAGES_AVAILABLE = arrayOf("En", "Ch", "Tm")


    // Binding the view on create with the view from activity_summary_card.xml
    private lateinit var binding: ActivitySummaryPageBinding

    // Initializing the array list from the SummaryTopic.kt file
    private lateinit var listOfSumaries: ArrayList<SummaryTopic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryPageBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        //  Initializing a dummy array for testing
        val listOfSummaryTitles = arrayOf("Calculus", "Algebra", "Optics","Mechanics")
        val listOfLastEditedDates = arrayOf("Date: 17-03-2023", "Date: 19-03-2023", "Date: 17-03-2023"
            ,"Date: 27-03-2023")


        //  Using the declared array list
        listOfSumaries = ArrayList()

         for (i in listOfSummaryTitles.indices){

            // Initializing an object with all the properties
             val summary = SummaryTopic(listOfSummaryTitles[i], listOfLastEditedDates[i])

             // Setting these values into the array
             listOfSumaries.add(summary)
         }

        // Binding this array into the adapter
        binding.listOfSummariesView.adapter =  SummariesArrayAdapter(this, listOfSumaries)


    }
}