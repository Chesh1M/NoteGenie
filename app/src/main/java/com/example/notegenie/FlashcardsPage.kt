package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.notegenie.databinding.ActivityFlashcardsPageBinding
import com.google.firebase.auth.FirebaseAuth

class FlashcardsPage : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityFlashcardsPageBinding

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
        navPopupMenu.menu.add(Menu.NONE, 3, 3, "Logout")

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
                val switchActivity = Intent(this, StartActivity::class.java)
                startActivity(switchActivity)
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Sign out success!", Toast.LENGTH_SHORT).show()
            }

            false

        }

        // Setting an on click listener for the popup view
        navigatePagesMenuView.setOnClickListener{
            navPopupMenu.show()
        }
    }
}