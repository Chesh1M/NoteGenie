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
import kotlin.reflect.typeOf


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

            // Getting the values associated with the root
            val rootNodeTags = retrievedMap.values.toList()

            // Setting it as strings
            var rootNodeTagsString = rootNodeTags.joinToString()

            // Removing the first square bracket from the list
            rootNodeTagsString = rootNodeTagsString.substring(1)

            // Removing the last square bracket
            rootNodeTagsString = rootNodeTagsString.substring(0, rootNodeTagsString.length-1)

            // Converting that string into a list
            var rootNodeTagsStringList = rootNodeTagsString.split("], [")


            // Initializing a loop counter
            var counterJ = 0


            // Looping through the values to find the key
            retrievedMap.keys.forEach { key ->



                if (key == topicOfMindMap) { // If the key from the database equals the topic of the page


                    // Initializing the root item (NOTE: PLEASE DO INITIALIZE A ROOT FOR EVERY KEY)
                    val rootNode = Item(this, key, "", true)

                    // Connecting the view to a root
                    MegaMindMap.addCentralItem(rootNode, false)

                    val listFromRootNodeTagsStringList = rootNodeTagsStringList.elementAt(counterJ)
                        .split(", ") // Splitting the string within the list


                    // Deriving the tag values that are common between the root and values
                    val mapOfCommonTags = findCommonTags(key, listFromRootNodeTagsStringList, retrievedMap)

                    // Giving the common tags as output
                    Log.i("Common Tags", mapOfCommonTags.values.toString())

                    // Generating the map
                    generateMindMap(MegaMindMap, rootNode, mapOfCommonTags)



                } else { // If the matching data was not found

                    Log.e("Data not found", "The page is not found in the database")
                }

                // Updating the counter
                counterJ += 1



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

//        // Adding child to root
//        val algebraRoot = childToRoot(MegaMindMap, "Algebra", calculusRoot, tagsOfCalculus.intersect(tagsOfAlgebra).toTypedArray())
//        val mechRoot = childToRoot(MegaMindMap, "Mechanics", calculusRoot, tagsOfCalculus.intersect(tagsOfMechanics).toTypedArray())
//
//        // Adding child to child
//        childToRoot(MegaMindMap, "Complex Numbers", mechRoot, tagsOfCalculus.intersect(tagsOfAlgebra).toTypedArray())
//        childToRoot(MegaMindMap, "Optics", mechRoot, tagsOfMechanics.intersect(tagsOfOptics).toTypedArray())
//        childToRoot(MegaMindMap, "Optics", algebraRoot, tagsOfAlgebra.intersect(tagsOfOptics).toTypedArray())
//        childToRoot(MegaMindMap, "Optics", calculusRoot, tagsOfCalculus.intersect(tagsOfOptics).toTypedArray())

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
                       mapOfTags: Map<String, String>): MutableMap<String, String> {

        // Initializing the values to be returned
        val tagsToBeReturned = mutableMapOf<String, String>()

        // Getting the Values of the map
        val mapOfTagsValues = mapOfTags.values.toList()

        // Converting these values into a string
        var mapOfTagsValuesString = mapOfTagsValues.joinToString()

        // Removing the first square bracket from the list
        mapOfTagsValuesString = mapOfTagsValuesString.substring(1)

        // Removing the last element of the string
        mapOfTagsValuesString = mapOfTagsValuesString.substring(0, mapOfTagsValuesString.length - 1)

        //Converting that string into a list
        val mapOfTagsValuesStringList = mapOfTagsValuesString.split("], [")

        // Introducing a loop counter
        var counterI = 0

        // Looping through the key Tags
        mapOfTags.keys.forEach { key ->

            if (key != rootTagName){ // If the key is not the root tag

                // Getting the list from the index
                val listFromMapOfTagsValuesStringList = mapOfTagsValuesStringList.elementAt(counterI)
                    .split(", ") // Splitting the string within the list

                // Finding the common value between both the lists
                val commonTagValues = rootTagValues.intersect(listFromMapOfTagsValuesStringList.toSet())
                    .toTypedArray()

                if (commonTagValues.isNotEmpty()){ // If there are common tags:

                    // Add the element to tagsToBeReturned
                    tagsToBeReturned.put(key, commonTagValues.joinToString(", "))

                }


            }

            // Updating the counter
            counterI += 1

        }
        return tagsToBeReturned



    }

    // Function to construct the MindMap from the tags and the main root
    fun generateMindMap(MegaMindMap: MindMappingView, rootNodeMain: Item,
                        mapOfCommonTags: Map<String, String>){


        // Looping through the keys
        mapOfCommonTags.keys.forEach { key ->

            // Deriving values of a particular key
            val commonTagOfKey = mapOfCommonTags[key]

            // Converting from string to array
            val commonTagOfKeyList = commonTagOfKey.toString().split(",").toTypedArray()



            childToRoot(MegaMindMap, key, rootNodeMain, commonTagOfKeyList)


        }
    }
}