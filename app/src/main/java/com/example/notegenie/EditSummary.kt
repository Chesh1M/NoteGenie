package com.example.notegenie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.notegenie.databinding.ActivityDisplaySummaryContentBinding
import com.example.notegenie.databinding.ActivityEditSummaryBinding
import com.example.notegenie.databinding.ActivitySummaryCardBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class EditSummary: AppCompatActivity() {

    // Initializing Global Variables
    var CURRENT_LANGUAGE = "En"

    // Initializing a binding
    private lateinit var binding: ActivityEditSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Getting the variables from the previous activity
        var summaryTitle =  intent.getStringExtra("NAME_KEY")
        val summaryLastEditDate = intent.getStringExtra("Edit Date")
        val summaryContent = intent.getStringExtra("CONTENT_KEY")

        // Receive the content from DisplaySummaryContent activity
        val receivedContent = intent.getStringExtra("Summary Content")

        // Set up the EditText with the received content
        binding.editTextSummaryContent.setText(receivedContent)

        val openHomePage: Button = findViewById(R.id.exit)

        // Set click listener on the button
        openHomePage.setOnClickListener {
            // Create an intent to open the second activity
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            Toast.makeText(this, "Exit",
                Toast.LENGTH_SHORT).show()
        }

        val openDisplaySummaryContent: Button = findViewById(R.id.saveChanges)
        openDisplaySummaryContent.setOnClickListener {
            // Get the edited content from the EditText
            val editedContent = binding.editTextSummaryContent.text.toString()

            // Create an intent to open the DisplaySummaryContent activity
            val intent = Intent(this, DisplaySummaryContent::class.java).apply {
                putExtra("Summary Title", summaryTitle)
                putExtra("Edit Date", summaryLastEditDate)
                putExtra("Summary Content", editedContent) // Pass the edited content back
            }
            startActivity(intent)
            Toast.makeText(this, "Changes have been saved successfully", Toast.LENGTH_SHORT).show()
        }

        // Setting the title
        summaryTitle = "Edit Summary"

        // Setting up the text views accordingly
        binding.summaryTextView.text = summaryTitle
        binding.summaryContentsView.text = summaryContent

        // Setting the value of the currentLanguageTextView onCreate

        // Initializing the view
        val currentLanguageTextView: TextView = findViewById(R.id.currentLanguageTextView)
        // Setting the text into the view
        currentLanguageTextView.text = CURRENT_LANGUAGE


        // Initializing the pop-up menu

        // Firstly initializing the widget
        val languagesMenuView: ImageView = findViewById(R.id.changeLanguageImageView)

        // Now initializing the pop-up menu
        val changeLanguagePopupMenu = PopupMenu(this, languagesMenuView)

        // Adding the elements of the popup menu
        changeLanguagePopupMenu.menu.add(Menu.NONE, 0, 0, "English (En)")
        changeLanguagePopupMenu.menu.add(Menu.NONE, 1, 1, "Chinese (Ch)")
        changeLanguagePopupMenu.menu.add(Menu.NONE, 2, 2, "Tamil (Ta)")
        changeLanguagePopupMenu.menu.add(Menu.NONE, 3, 3, "Malay (Ma)")

        // Handling item clicks
        changeLanguagePopupMenu.setOnMenuItemClickListener { menuItem->

            // Getting id of menu
            val menuID = menuItem.itemId


            // Handling navigation
            if (menuID==0){

                // Switching the Language
                CURRENT_LANGUAGE = "En"
                currentLanguageTextView.text = CURRENT_LANGUAGE

                // Notifying the user of the change
                Toast.makeText(this, "Language Changed to English",
                    Toast.LENGTH_SHORT).show()

            } else if(menuID==1){

                // Switching the Language
                CURRENT_LANGUAGE = "Ch"
                currentLanguageTextView.text = CURRENT_LANGUAGE

                // Notifying the user of the change
                Toast.makeText(this, "Language Changed to Chinese",
                    Toast.LENGTH_SHORT).show()

                // Create an English-Chinese translator:
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.CHINESE)
                    .build()
                val englishChineseTranslator = Translation.getClient(options)

                // Making sure that the model is available
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                englishChineseTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        Log.i("Translation Load Status", "Sucessfully Translated")
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Translation Load Status", "Translation Model Load Failed")
                        // Showing that the translation failed
                        Toast.makeText(this, "Translation Model Load Failed",
                            Toast.LENGTH_LONG).show()
                    }

                // Actually translating
                englishChineseTranslator.translate(binding.summaryContentsView.text.toString())
                    .addOnSuccessListener { translatedText ->
                        binding.summaryContentsView.text = translatedText.toString()
                        Log.i("Translation Status:", "Sucessfuly Translated")
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Translation Status:", "Error Translating")
                    }


            } else if(menuID==2){

                // Switching the Language
                CURRENT_LANGUAGE = "Ta"
                currentLanguageTextView.text = CURRENT_LANGUAGE

                // Notifying the user of the change
                Toast.makeText(this, "Language Changed to Tamil",
                    Toast.LENGTH_SHORT).show()

                // Create an English-Tamil translator:
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.TAMIL)
                    .build()
                val englishTamilTranslator = Translation.getClient(options)

                // Making sure that the model is available
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                englishTamilTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        Log.i("Translation Load Status", "Sucessfully Translated")
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Translation Load Status", "Translation Model Load Failed")
                        // Showing that the translation failed
                        Toast.makeText(this, "Translation Model Load Failed",
                            Toast.LENGTH_LONG).show()
                    }

                // Actually translating
                englishTamilTranslator.translate(binding.summaryContentsView.text.toString())
                    .addOnSuccessListener { translatedText ->
                        binding.summaryContentsView.text = translatedText.toString()
                        Log.i("Translation Status:", "Sucessfuly Translated")
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Translation Status:", "Error Translating")
                    }

            } else if(menuID==3){

                // Switching the Language
                CURRENT_LANGUAGE = "Ma"
                currentLanguageTextView.text = CURRENT_LANGUAGE

                // Notifying the user of the change
                Toast.makeText(this, "Language Changed to Malay",
                    Toast.LENGTH_SHORT).show()

                // Create an English-Spanish translator:
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.MALAY)
                    .build()
                val englishMalayTranslator = Translation.getClient(options)

                // Making sure that the model is available
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                englishMalayTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        Log.i("Translation Load Status", "Sucessfully Translated")
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Translation Load Status", "Translation Model Load Failed")
                        // Showing that the translation failed
                        Toast.makeText(this, "Translation Model Load Failed",
                            Toast.LENGTH_LONG).show()
                    }

                // Actually translating
                englishMalayTranslator.translate(binding.summaryContentsView.text.toString())
                    .addOnSuccessListener { translatedText ->
                        binding.summaryContentsView.text = translatedText.toString()
                        Log.i("Translation Status:", "Sucessfuly Translated")
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Translation Status:", "Error Translating")
                    }
            }

            false



        }

        // Setting an onclick listener for the changeLanguageImageView
        languagesMenuView.setOnClickListener{
            changeLanguagePopupMenu.show()
        }





    }

    // Function to export to pdf
    fun exportToPDF(view: View){

    }
}