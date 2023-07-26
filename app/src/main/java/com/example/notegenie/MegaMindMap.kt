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
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import me.jagar.mindmappingandroidlibrary.Listeners.OnItemClicked
import me.jagar.mindmappingandroidlibrary.Views.ConnectionTextMessage
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView
import kotlin.reflect.typeOf


class MegaMindMap : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        // Status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)

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
                    generateMindMap(MegaMindMap, topicOfMindMap, rootNode, mapOfCommonTags,
                        retrievedMap.toMutableMap())



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



        // Setting an onClickListener
        MegaMindMap.setOnItemClicked { item ->
            item.isPressed = true
            Handler().postDelayed({
                item.animate().scaleX(1.0F)
                item.animate().scaleY(1.0F)
            }, 100)
            item.animate().scaleX(0.9090909F)
            item.animate().scaleY(0.9090909F)

//            FirebaseDatabase.getInstance().getReference("Summaries").get()
//                .addOnSuccessListener{it ->
//
//                    val summaryDataSnapshot = it.value as Map<*, *>
//
//                    // Getting the name of tag
//                    val nameOfTag = item.title.text
//
//                    // Toast to notify the user that the item has been selected
//                    Toast.makeText(this, "Going to "+nameOfTag, Toast.LENGTH_SHORT).show()
//
//                    // Removing the necessary characters
//
//                    // Getting the value
//                    var summaryContentOfItem = summaryDataSnapshot[nameOfTag].toString()
//
//                    // Removing the date
//                    summaryContentOfItem = summaryContentOfItem.drop(12)
//
//                    // Defining the variables to be pushed to the SummaryContentPage
//                    val summaryTitle = nameOfTag
//                    val summaryLastEditDate ="Dummy Variable"
//                    val summaryContent = summaryContentOfItem
//
//                    // Initializing a new intent to go to the next activity
//                    val displaySummaryContent = Intent(this, DisplaySummaryContent:: class.java)
//
//                    // Pushing the data to the next activity
//                    displaySummaryContent.putExtra("Summary Title", summaryTitle)
//                    displaySummaryContent.putExtra("Edit Date", summaryLastEditDate)
//                    displaySummaryContent.putExtra("Summary Content", summaryContent)
//
//                    // Switching to the next activity
//                    startActivity(displaySummaryContent)
//
//
//                }

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
    fun generateMindMap(MegaMindMap: MindMappingView, rootNodeName: String, rootNodeMain: Item,
                        mapOfCommonTagsLocal: Map<String, String>,
                        mapOfCommonTagsInternational: MutableMap<String, String>) {

        // Declaring a visited list
        val visitedList = mapOfCommonTagsLocal.keys.toMutableList()

        // Removing the root node from visited
        visitedList.remove(rootNodeName)


        // Looping through the keys
        mapOfCommonTagsLocal.keys.forEach { key ->

            // Deriving values of a particular key
            val commonTagOfKeys = mapOfCommonTagsLocal[key]

            // Converting from string to array
            val commonTagOfKeyList = commonTagOfKeys.toString().split(",").toTypedArray()

            val childRoot = childToRoot(MegaMindMap, key, rootNodeMain, commonTagOfKeyList)



//            // If the visited list is not empty, recurse
//            if (visitedList.isNotEmpty()) {
//
//                // Removing the previous root element from the map
//                mapOfCommonTagsInternational.remove(rootNodeName)
//
//                // Getting the number of children
//                val numOfChildren = visitedList.size
//                Toast.makeText(this, numOfChildren.toString(), Toast.LENGTH_LONG).show()
//                Toast.makeText(
//                    this,
//                    rootNodeMain.indexOfChild(childRoot).toString(),
//                    Toast.LENGTH_LONG
//                ).show()
//
//                for (i in 1..numOfChildren) {
//
//                    // Finding the common tag values
//                    val listOfCommonTags = findCommonTags(
//                        key, mapOfCommonTagsInternational[key]
//                            .toString().replace("[", "").replace("]", "")
//                            .split(", "), mapOfCommonTagsInternational
//                    )
//
//
//                    val x = generateMindMap(
//                        MegaMindMap,
//                        key,
//                        childRoot,
//                        listOfCommonTags,
//                        mapOfCommonTagsInternational
//                    )
//
//                }
//            }


        }


    }
}