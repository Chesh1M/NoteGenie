package com.example.notegenie

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.jagar.mindmappingandroidlibrary.Listeners.OnItemClicked
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView


class MegaMindMap : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mega_mind_map)

        // Initializing the view
        val MegaMindMap: MindMappingView = findViewById(R.id.mind_mapping_view)

        // Loading the data from the database
        // Initializing the map to be returned
        var retrievedMap = mapOf("" to "")

        // Getting the values from the previous intent
        val topicOfMindMap = intent.getStringExtra("Summary Title")

        // Initializing the Database
        database = FirebaseDatabase.getInstance().getReference("Tags")

        // Retrieving the data
        database.get().addOnSuccessListener {

            // Loading the data from the database
            retrievedMap = it.value as Map<String, String>


            // Getting all the values within the map


            // Looping through the values to find the key
            retrievedMap.keys.forEach { key ->



                if (key == topicOfMindMap) { // If the key from the database equals the topic of the page


                    // Initializing the root item (NOTE: PLEASE DO INITIALIZE A ROOT FOR EVERY KEY)
                    val rootNode = Item(this, key, "", true)

                    // Connecting the view to a root
                    MegaMindMap.addCentralItem(rootNode, false)

                    // Getting the values associated with the root
                    val rootNodeTags = retrievedMap.values.toList()


                    // Deriving the tag values that are common between the root and values
                    val mapOfCommonTags = findCommonTags(key, rootNodeTags, retrievedMap)



                } else { // If the matching data was not found

                    Log.e("Data not found", "The page is not found in the database")
                }


            }

        }

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

    // Function to find the common tags
    fun findCommonTags(rootTagName: String, rootTagValues: List<String>,
                       mapOfTags: Map<String, String>): MutableMap<String, List<String>> {

        Log.i("Common", mapOfTags.toString())

        // Initializing the values to be returned
        val tagsToBeReturned = mutableMapOf<String, List<String>>()



        // Looping through the key Tags
        mapOfTags.keys.forEach { key ->

            // Getting the values associated with the nodes
            var nodeTagsValues = mapOfTags.values.toList()

            // Initializing a value for iteration
            var i = 0

            // If the key is equal to the root tag name
            if (key != rootTagName){ // If the key is not the root

                nodeTagsValues = nodeTagsValues.slice(i..i+1)

                Log.i("List value", nodeTagsValues.toString())

                val commonTagValues = rootTagValues.intersect(nodeTagsValues).toList()// Getting the common elements within the array

                if (commonTagValues.isNotEmpty()){ // If there are common tags:

                    // Add the element to tagsToBeReturned
                    tagsToBeReturned.put(key, commonTagValues as List<String>)

                }

                // Updating the counter
                i += 1

            }
        }

        return tagsToBeReturned

    }
}