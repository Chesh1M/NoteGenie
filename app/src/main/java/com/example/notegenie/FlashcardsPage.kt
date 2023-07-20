package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.notegenie.databinding.ActivityFlashcardsPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FlashcardsPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityFlashcardsPageBinding

    // Initializing the array list from the FlashCard Data.kt file
    private lateinit var listOfFlashCards: ArrayList<FlashCardData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardsPageBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        // Status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)


        // Firstly initializing the widget
        val navigatePagesMenuView: ImageView = findViewById(R.id.navigatePagesMenu)

        // Now initializing the pop-up menu
        val navPopupMenu = PopupMenu(this, navigatePagesMenuView)

        // Adding the elements of the popup menu
        navPopupMenu.menu.add(Menu.NONE, 0, 0, "Home")
        navPopupMenu.menu.add(Menu.NONE, 1, 1, "Summaries")
        navPopupMenu.menu.add(Menu.NONE, 2, 2, "Flashcards")
        navPopupMenu.menu.add(Menu.NONE, 3, 3, "Settings")

        // Handling item clicks
        navPopupMenu.setOnMenuItemClickListener { menuItem->

            // Getting id of menu
            val menuID = menuItem.itemId


            // Handling navigation
            if (menuID==0){

                // Initializing an intent to switch activity
                val switchActivity = Intent(this, HomePage::class.java)
                startActivity(switchActivity)

            } else if(menuID==1){
                // Initializing an intent to switch activity
                val switchActivity = Intent(this, SummaryPage::class.java)
                startActivity(switchActivity)

            } else if(menuID==2){
                // Initializing an intent to switch activity
                val switchActivity = Intent(this, FlashcardsPage::class.java)
                startActivity(switchActivity)
            } else if(menuID==3){
                // Initializing an intent to switch activity
                val switchActivity = Intent(this, SettingsPage::class.java)
                startActivity(switchActivity)
            }

            false

        }

        // Setting an on click listener for the popup view
        navigatePagesMenuView.setOnClickListener{
            navPopupMenu.show()
        }

        getDataFromDatabase()
    }

    // Function to get the map from Firebase Database
    fun getDataFromDatabase(){

        // Loading the cloud
        val firebaseDatabaseSummaries = FirebaseDatabase.getInstance()
            .getReference("Flashcards")

        // Loading the values
        firebaseDatabaseSummaries.get().addOnSuccessListener {it ->

            // Initializing a list of FlashCard Data Objects
            val listOfFlashCardDataObjects = mutableListOf<FlashCardData>()

            // Loading the values from the database
            val retrievedMap = it.value as Map<String, Map<String, String>>

            // Initializing the titles
            val titles = retrievedMap.keys.toList()

            // Loading the question and answer pairs
            val questionAndAnswerPair = retrievedMap.values.toList()

            // Initializing a counter to loop through the summaries
            var counterSummary = 0

            // Looping through the values
            questionAndAnswerPair.forEach { pair ->

                // Initializing the object
                val flashCardDataObject = FlashCardData(titles[counterSummary], pair)

                // Adding this object to the list
                listOfFlashCardDataObjects.add(flashCardDataObject)

                // Incrementing the counter
                counterSummary += 1
            }

            //  Using the declared array list
            listOfFlashCards = ArrayList()

            for (i in titles.indices){

                // Initializing an object with all the properties
                val flashCard = FlashCardData(titles[i], questionAndAnswerPair[i])

                // Setting these values into the array
                listOfFlashCards.add(flashCard)
            }

            // Binding this array into the adapter
            binding.listOfFlashCardsView.isClickable = true
            binding.listOfFlashCardsView.adapter =  FlashCardsArrayAdapter(this,
                listOfFlashCards
            )

            // Open the new intent on click
            binding.listOfFlashCardsView.setOnItemClickListener { parent, view, position, id ->

                // Defining the variables to be pushed to the SummaryContentPage
                val flashCardTitle = titles[position]

                // Initializing a new intent to go to the next activity
                val flashCardTranslation = Intent(this, FlashcardTranslation:: class.java)

                // Pushing the data to the next activity
                flashCardTranslation.putExtra("Flash Card Title", flashCardTitle)

                // Switching to the next activity
                startActivity(flashCardTranslation)
            }

    }

    }
}