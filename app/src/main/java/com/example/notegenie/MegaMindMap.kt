package com.example.notegenie

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.jagar.mindmappingandroidlibrary.Listeners.OnItemClicked
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView


class MegaMindMap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mega_mind_map)

        // Getting the values from the previous intent
        val topicOfMindMap = intent.getStringExtra("Summary Title")

        // Initializing the text for the title
        val titleText = "Mind Map Of \n"+topicOfMindMap.toString()+":"

        // Initializing the text view
        val titleTextView: TextView = findViewById(R.id.flashCardsTextView)

        // Setting the text
        titleTextView.text = titleText

        // Initializing the button view
        val backButton: ImageView = findViewById(R.id.backButton)

        // Setting up an onclick listener for the button
        backButton.setOnClickListener{
            // Changing the intent back oto the home page
            val goBackToSummariesIntent = Intent(this, SummaryPage::class.java )

            // Switching the activity
            startActivity(goBackToSummariesIntent)
        }


        // Initializing the list of tags
        val tagsOfCalculus = listOf("Mathematics", "Sequences", "Operations") // Tags for Calculus page
        val tagsOfAlgebra = listOf("Mathematics", "Operations", "Equations") // Tags for the Algebra page
        val tagsOfMechanics = listOf("Mathematics", "Physics", "Force", "Objects", "Sequences") // Tags for the mechanics page
        val tagsOfOptics = listOf("Physics", "Light", "Equations") // Tags for the Optics page

        // Initializing the view
        val MegaMindMap: MindMappingView = findViewById(R.id.mind_mapping_view)

        // Initializing the root item (NOTE: PLEASE DO INITIALIZE A ROOT FOR EVERY KEY)
        val calculusRoot = Item(this, "Calculus", "", true)

        // Connecting the view to a root
        MegaMindMap.addCentralItem(calculusRoot, false)

        // Adding child to root
        val algebraRoot = childToRoot(MegaMindMap, "Algebra", calculusRoot, tagsOfCalculus.intersect(tagsOfAlgebra).toTypedArray())
        val mechRoot = childToRoot(MegaMindMap, "Mechanics", calculusRoot, tagsOfCalculus.intersect(tagsOfMechanics).toTypedArray())

        // Adding child to child
        childToRoot(MegaMindMap, "Complex Numbers", mechRoot, tagsOfCalculus.intersect(tagsOfAlgebra).toTypedArray())
        childToRoot(MegaMindMap, "Optics", mechRoot, tagsOfMechanics.intersect(tagsOfOptics).toTypedArray())
        childToRoot(MegaMindMap, "Optics", algebraRoot, tagsOfAlgebra.intersect(tagsOfOptics).toTypedArray())
        childToRoot(MegaMindMap, "Optics", calculusRoot, tagsOfCalculus.intersect(tagsOfOptics).toTypedArray())

        // Setting an onClickListener
        MegaMindMap.setOnItemClicked { item ->
            item.isPressed = true
            Handler().postDelayed({
                item.animate().scaleX(1.0F)
                item.animate().scaleY(1.0F)
            }, 100)
            item.animate().scaleX(0.9090909F)
            item.animate().scaleY(0.9090909F)

        }


    }

    // Function to add a child to a root
    fun childToRoot(MegaMindMap: MindMappingView, childTitle: String,
                    childRootNode: Item, listOfCommonTags: Array<String>): Item {

        // Initializing the child
        val child = Item(this, childTitle, listOfCommonTags.joinToString(", "), true)

        MegaMindMap.addItem(child, childRootNode, 200, 10, ItemLocation.TOP, true, null)

        return child // Returning the child to be used later

    }
}