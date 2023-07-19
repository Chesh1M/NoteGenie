package com.example.notegenie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.notegenie.databinding.ActivityRegisterBinding
import com.example.notegenie.databinding.ActivitySummaryPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.reflect.typeOf

class SummaryPage : AppCompatActivity() {

    // Global Variables
    val LIST_OF_LANGUAGES_AVAILABLE = arrayOf("En", "Ch", "Tm")
    var DATA_FROM_FIREBASE = ""
    val USERNAME = FirebaseAuth.getInstance().currentUser?.email.toString()


    // Binding the view on create with the view from activity_summary_card.xml
    private lateinit var binding: ActivitySummaryPageBinding

    // Initializing the array list from the SummaryTopic.kt file
    private lateinit var listOfSumaries: ArrayList<SummaryTopic>

    // Database reference initialization
    private lateinit var database: DatabaseReference

    // Firebase
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryPageBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        // Status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)


        // Initializing the pop-up menu

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

        // Loading the cloud
        val firebaseDatabaseSummaries = FirebaseDatabase.getInstance()
            .getReference("Summaries")

        // Loading the values
        firebaseDatabaseSummaries.get().addOnSuccessListener {it ->

            // Loading the data from the database
            val retrievedMap = it.value as Map<String, String>

            // Getting the keys of summaries
            val listOfKeys = retrievedMap.keys.toList()

            // Setting it as strings
            val listOfKeysString = listOfKeys.joinToString()


            // Converting that string into a list
            val listOfSummaryTitles = listOfKeysString.split(", ")

            // Getting the content

            // Getting the values associated with the root
            val listOfValues = retrievedMap.values.toList()

            // Setting it as strings
            var listOfValuesString = listOfValues.joinToString()

            // Removing the first square bracket from the list
            listOfValuesString = listOfValuesString.substring(1)

            // Removing the last square bracket
            listOfValuesString = listOfValuesString.substring(0, listOfValuesString.length-1)


            // Converting that string into a list
            val listOfValuesArray = listOfValuesString.split("}, {")


            // Initializing the last edited list
            val listOfLastEditedDates = mutableListOf<String>()

            // Initializing the content list
            val listofContents = mutableListOf<String>()

//            Toast.makeText(this, USERNAME.toString(), Toast.LENGTH_LONG).show()

            // Looping through the values
            for (i in 0 until listOfValuesArray.count()){

                // Getting one element
                val currentElement = listOfValuesArray[i]

                // Extracting the date
                val currentDate = currentElement.take(10)

                // Adding to the list
                listOfLastEditedDates.add("Date: "+currentDate)

                // Extracting the content
                val currentContent = currentElement.drop(11)

                // Adding to the list
                listofContents.add(currentContent)


            }

            //  Using the declared array list
            listOfSumaries = ArrayList()

            for (i in listOfSummaryTitles.indices){

                // Initializing an object with all the properties
                val summary = SummaryTopic(listOfSummaryTitles[i], listOfLastEditedDates[i], listofContents[i])

                // Setting these values into the array
                listOfSumaries.add(summary)
            }

            // Binding this array into the adapter
            binding.listOfSummariesView.isClickable = true
            binding.listOfSummariesView.adapter =  SummariesArrayAdapter(this, listOfSumaries)


//        // Fo to Mind map page when long pressed
//        binding.listOfSummariesView.onItemLongClickListener{parent, view, position, id ->
//
//            Log.i()
//
//        }

            // Function that determines what will be displayed when the item is clicked
            binding.listOfSummariesView.setOnItemClickListener{parent, view, position, id ->

                // Defining the variables to be pushed to the SummaryContentPage
                val summaryTitle = listOfSummaryTitles[position]
                val summaryLastEditDate = listOfLastEditedDates[position]
                val summaryContent = listofContents[position]

                // Initializing a new intent to go to the next activity
                val displaySummaryContent = Intent(this, DisplaySummaryContent:: class.java)

                // Pushing the data to the next activity
                displaySummaryContent.putExtra("Summary Title", summaryTitle)
                displaySummaryContent.putExtra("Edit Date", summaryLastEditDate)
                displaySummaryContent.putExtra("Summary Content", summaryContent)

                // Switching to the next activity
                startActivity(displaySummaryContent)


            }

            // Open MegaMindMap on long press
            binding.listOfSummariesView.setOnItemLongClickListener { parent, view, position, id ->

                // Defining the variables to be pushed to the SummaryContentPage
                val summaryTitle = listOfSummaryTitles[position]

                // Initializing a new intent to go to the next activity
                val megaMindMapIntent = Intent(this, MegaMindMap:: class.java)

                // Pushing the data to the next activity
                megaMindMapIntent.putExtra("Summary Title", summaryTitle)

                // Switching to the next activity
                startActivity(megaMindMapIntent)

                return@setOnItemLongClickListener false // Must add this
            }



        }

        // Sample of how to use the calling

//        generateNewSummary("Quantum mechanics", "06-07-2023", "Quantum mechanics is a fundamental theory in physics that provides a description of the physical properties of nature at the scale of atoms and subatomic particles.[2]: 1.1  It is the foundation of all quantum physics including quantum chemistry, quantum field theory, quantum technology, and quantum information science.", listOf("Physics", "sub-atomic particles", "Quantum"))





    }

    // Function that is activated to generate a new summary & tags
    fun generateNewSummary(summaryTopic: String, lastEdited: String,
                           summaryContent: String, listOfTags: List<String>){

        // Write a message to the database
        FirebaseDatabase.getInstance().getReference().child(USERNAME).child("Summaries")
            .child(summaryTopic).child(lastEdited).setValue(summaryContent);

        // Generate tags
        FirebaseDatabase.getInstance().getReference().child(USERNAME).child("Tags")
            .child(summaryTopic).setValue(listOfTags);


    }




}