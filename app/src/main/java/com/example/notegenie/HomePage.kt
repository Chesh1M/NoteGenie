package com.example.notegenie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.notegenie.databinding.ActivityHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import java.net.URI


class HomePage : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var audioURI: Uri


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

        // Initializing an intent
        val intentAudio: Intent = Intent()

        // Setting the type to get
        intentAudio.setType("audio/*")

        // Setting the action
        intentAudio.action = Intent.ACTION_OPEN_DOCUMENT

        // Starting the activity
        startActivityForResult(Intent.createChooser(intentAudio, "Select File"), 1)




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {

            // Audio is Picked in format of URI
            if (data != null) {
                audioURI = data.getData()!!
            }

            Log.i("Path cocomelon", audioURI.toString())
        }
    }


    }