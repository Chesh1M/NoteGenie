package com.example.notegenie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import me.jagar.mindmappingandroidlibrary.Views.Item
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView

class MegaMindMap : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mega_mind_map)

        // Initializing the view
        val MegaMindMap: MindMappingView = findViewById(R.id.mind_mapping_view)

        // Initializing the root item (NOTE: PLEASE DO INITIALIZE A ROOT FOR EVERY KEY)
        val calculusRoot = Item(this, "Calculus", "", false)

        // Connecting the view to a root
        MegaMindMap.addCentralItem(calculusRoot, false)

        // Initializing the root item (NOTE: PLEASE DO INITIALIZE A ROOT FOR EVERY KEY)
        val algebraRoot = Item(this, "Calculus", "", false)

        // Connecting the view to a root
        MegaMindMap.addCentralItem(algebraRoot, false)

        // Connecting multiple children child
        addChild(MegaMindMap, "Algebra", "", calculusRoot)
        addChild(MegaMindMap, "Complex Numbers", "", calculusRoot)
        addChild(MegaMindMap, "Optics", "", calculusRoot)



//        // Initializing the mindmap view
//        val MegaMindMap: MindMappingView = findViewById(R.id.mind_mapping_view)
//
//
//        // Connecting a child
//        // Initializing the child
//        val child = Item(this, "Child", "This is a child node", true)
//
//        // Connecting the child to the main view
//        MegaMindMap.addItem(child, calculusItem, 200, 100, ItemLocation.TOP, true, null)



    }

    // Function to add a child node onto another
    fun addChild(MegaMindMap: MindMappingView,childTitle: String, childMainTopic: String,
                 childRootNode: Item){

        // Connecting the child
        val child = Item(this, childTitle, childMainTopic, false)

        // Connecting the child to root
        MegaMindMap.addItem(child, childRootNode, 200, 100, ItemLocation.BOTTOM,
            true, null)
    }
}