package com.example.notegenie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase


class HomePage : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
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
                val switchActivity = Intent(this, FlashcardTranslation::class.java)
                startActivity(switchActivity)
            } else if(menuID==3){
                val switchActivity = Intent(this, SettingsPage::class.java)
                startActivity(switchActivity)
            }

            false


        }

        // Setting an on click listener for the popup view
        navigatePagesMenuView.setOnClickListener{
            navPopupMenu.show()
        }
    }

    // Function to add a file
    fun addFile(view: View){

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        val uri = Uri.parse("") // a directory

        intent.setDataAndType(uri, "*/*")
        startActivity(Intent.createChooser(intent, "Open folder"))


    }

    // Function for the finder
    fun finder(view: View){

        // Initializing the text view
        val finderEditText: EditText = findViewById(R.id.finderEditText)

        // Getting the text from the textview
        val searchPrompt = finderEditText.text

        // Opening the Firebase Database
        val firebaseDatabaseSummaries = FirebaseDatabase.getInstance()
            .getReference("Summaries")

        // Loading the values
        firebaseDatabaseSummaries.get().addOnSuccessListener {it ->

            // Loading the data from the database
            val retrievedMap = it.value as Map<String, String>

            // Getting the list of keys
            val listOfKeys = retrievedMap.keys.toList()

            // Initializing a boolean if match found
            var promptFound = false

            // Initializing a counter for the values list
            var counterI = 0

            // Looping through the prompt to get a match
            listOfKeys.forEach { key ->

                // Looking for a match
                if (key.toString() == searchPrompt.toString()){
                    // If there is a match
                    promptFound = true

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

                    // Getting the date
                    val lastEditedDate = listOfValuesArray[counterI].take(10)

                    // Getting the content
                    val summaryContent = listOfValuesArray[counterI].drop(11)


                    // Initializing a new intent to go to the next activity
                    val displaySummaryContent = Intent(this, DisplaySummaryContent:: class.java)

                    // Pushing the data to the next activity
                    displaySummaryContent.putExtra("Summary Title", key)
                    displaySummaryContent.putExtra("Edit Date", lastEditedDate)
                    displaySummaryContent.putExtra("Summary Content", summaryContent)

                    // Switching to the next activity
                    startActivity(displaySummaryContent)

                    // Setting the text to nothing once entered
                    finderEditText.setText("")

                }

                // Updating the counter
                counterI += 1


            }

            // If the prompt is false, toast an error message

            if(promptFound == false){
                // Toasting that the file does not exist
                Toast.makeText(this, "File does not exist in database", Toast.LENGTH_LONG).show()
            }



        }
    }
}