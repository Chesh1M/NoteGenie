package com.example.notegenie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notegenie.databinding.ActivitySummaryPageBinding
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


    // Binding the view on create with the view from activity_summary_card.xml
    private lateinit var binding: ActivitySummaryPageBinding

    // Initializing the array list from the SummaryTopic.kt file
    private lateinit var listOfSumaries: ArrayList<SummaryTopic>

    // Database reference initialization
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryPageBinding.inflate(this.layoutInflater)
        setContentView(binding.root)


        // Initializing the pop-up menu

        // Firstly initializing the widget
        val navigatePagesMenuView: ImageView = findViewById(R.id.navigatePagesMenu)

        // Now initializing the pop-up menu
        val navPopupMenu = PopupMenu(this, navigatePagesMenuView)

        // Adding the elements of the popup menu
        navPopupMenu.menu.add(Menu.NONE, 0, 0, "Home")
        navPopupMenu.menu.add(Menu.NONE, 1, 1, "Summaries")
        navPopupMenu.menu.add(Menu.NONE, 2, 2, "Flashcards")

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
            }

            false


        }

        // Setting an on click listener for the popup view
        navigatePagesMenuView.setOnClickListener{
            navPopupMenu.show()
        }





        //  Initializing a dummy array for testing
        val listOfSummaryTitles = arrayOf("Calculus", "Algebra", "Optics","Mechanics",
            "Complex Numbers")
        val listOfLastEditedDates = arrayOf("Date: 17-03-2023", "Date: 19-03-2023", "Date: 17-03-2023"
            ,"Date: 27-03-2023", "Date: 27-07-2023")
        val listofContents = arrayOf("Calculus[nb 1] is the mathematical study of continuous change, in the same way that geometry is the study of shape, and algebra is the study of generalizations of arithmetic operations.\n" +
                "\n" +
                "It has two major branches, differential calculus and integral calculus; the former concerns instantaneous rates of change, and the slopes of curves, while the latter concerns accumulation of quantities, and areas under or between curves. These two branches are related to each other by the fundamental theorem of calculus, and they make use of the fundamental notions of convergence of infinite sequences and infinite series to a well-defined limit",
            "Algebra (from Arabic (al-jabr) 'reunion of broken parts,[1] bonesetting'[2]) is the study of variables and the rules for manipulating these variables in formulas;[3] it is a unifying thread of almost all of mathematics.[4]",
            "Optics is the branch of physics that studies the behaviour and properties of light, including its interactions with matter and the construction of instruments that use or detect it.[1] Optics usually describes the behaviour of visible, ultraviolet, and infrared light. Because light is an electromagnetic wave, other forms of electromagnetic radiation such as X-rays, microwaves, and radio waves exhibit similar properties.[1]\n" +
                    "\n" +
                    "Most optical phenomena can be accounted for by using the classical electromagnetic description of light, however complete electromagnetic descriptions of light are often difficult to apply in practice. Practical optics is usually done using simplified models. The most common of these, geometric optics, treats light as a collection of rays that travel in straight lines and bend when they pass through or reflect from surfaces. Physical optics is a more comprehensive model of light, which includes wave effects such as diffraction and interference that cannot be accounted for in geometric optics. Historically, the ray-based model of light was developed first, followed by the wave model of light. Progress in electromagnetic theory in the 19th century led to the discovery that light waves were in fact electromagnetic radiation. Some phenomena depend on light having both wave-like and particle-like properties. Explanation of these effects requires quantum mechanics. When considering light's particle-like properties, the light is modelled as a collection of particles called \"photons\". Quantum optics deals with the application of quantum mechanics to optical systems.\n" +
                    "\n" +
                    "Optical science is relevant to and studied in many related disciplines including astronomy, various engineering fields, photography, and medicine (particularly ophthalmology and optometry, in which it is called physiological optics). Practical applications of optics are found in a variety of technologies and everyday objects, including mirrors, lenses, telescopes, microscopes, lasers, and fibre optics.",
            "Mechanics (from Ancient Greek: μηχανική, mēkhanikḗ, lit. \"of machines\")[1][2] is the area of mathematics and physics concerned with the relationships between force, matter, and motion among physical objects.[3] Forces applied to objects result in displacements or changes of an object's position relative to its environment.\n" +
                    "\n" +
                    "Theoretical expositions of this branch of physics has its origins in Ancient Greece, for instance, in the writings of Aristotle and Archimedes[4][5][6] (see History of classical mechanics and Timeline of classical mechanics). During the early modern period, scientists such as Galileo, Kepler, Huygens, and Newton laid the foundation for what is now known as classical mechanics.",
            "NULL")


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

    // Function that is activated to generate a new summary
    fun generateNewSummary(view: View){

        // Write a message to the database
        FirebaseDatabase.getInstance().getReference().child("Summaries").child("Calculus").child("17-03-2023").setValue("Calculus[nb 1] is the mathematical study of continuous change, in the same way that geometry is the study of shape, and algebra is the study of generalizations of arithmetic operations.\n" +
                "\n" +
                "It has two major branches, differential calculus and integral calculus; the former concerns instantaneous rates of change, and the slopes of curves, while the latter concerns accumulation of quantities, and areas under or between curves. These two branches are related to each other by the fundamental theorem of calculus, and they make use of the fundamental notions of convergence of infinite sequences and infinite series to a well-defined limit");

        // Write a message to the database
        FirebaseDatabase.getInstance().getReference().child("Summaries").child("Algebra").child("19-03-2023").setValue("Algebra (from Arabic (al-jabr) 'reunion of broken parts,[1] bonesetting'[2]) is the study of variables and the rules for manipulating these variables in formulas;[3] it is a unifying thread of almost all of mathematics.[4]");

        // Write a message to the database
        FirebaseDatabase.getInstance().getReference().child("Summaries").child("Optics").child("17-03-2023").setValue("Optics is the branch of physics that studies the behaviour and properties of light, including its interactions with matter and the construction of instruments that use or detect it.[1] Optics usually describes the behaviour of visible, ultraviolet, and infrared light. Because light is an electromagnetic wave, other forms of electromagnetic radiation such as X-rays, microwaves, and radio waves exhibit similar properties.[1]\n" +
                "\n" +
                "Most optical phenomena can be accounted for by using the classical electromagnetic description of light, however complete electromagnetic descriptions of light are often difficult to apply in practice. Practical optics is usually done using simplified models. The most common of these, geometric optics, treats light as a collection of rays that travel in straight lines and bend when they pass through or reflect from surfaces. Physical optics is a more comprehensive model of light, which includes wave effects such as diffraction and interference that cannot be accounted for in geometric optics. Historically, the ray-based model of light was developed first, followed by the wave model of light. Progress in electromagnetic theory in the 19th century led to the discovery that light waves were in fact electromagnetic radiation. Some phenomena depend on light having both wave-like and particle-like properties. Explanation of these effects requires quantum mechanics. When considering light's particle-like properties, the light is modelled as a collection of particles called \"photons\". Quantum optics deals with the application of quantum mechanics to optical systems.\n" +
                "\n" +
                "Optical science is relevant to and studied in many related disciplines including astronomy, various engineering fields, photography, and medicine (particularly ophthalmology and optometry, in which it is called physiological optics). Practical applications of optics are found in a variety of technologies and everyday objects, including mirrors, lenses, telescopes, microscopes, lasers, and fibre optics.");
        // Write a message to the database
        FirebaseDatabase.getInstance().getReference().child("Summaries").child("Mechanics").child("27-03-2023").setValue("Mechanics (from Ancient Greek: μηχανική, mēkhanikḗ, lit. \\\"of machines\\\")[1][2] is the area of mathematics and physics concerned with the relationships between force, matter, and motion among physical objects.[3] Forces applied to objects result in displacements or changes of an object's position relative to its environment.\\n\" +\n" +
                "                    \"\\n\" +\n" +
                "                    \"Theoretical expositions of this branch of physics has its origins in Ancient Greece, for instance, in the writings of Aristotle and Archimedes[4][5][6] (see History of classical mechanics and Timeline of classical mechanics). During the early modern period, scientists such as Galileo, Kepler, Huygens, and Newton laid the foundation for what is now known as classical mechanics.");
        // Write a message to the database
        FirebaseDatabase.getInstance().getReference().child("Summaries").child("Complex Numbers").child("27-07-2023").setValue("N/A");


    }




}