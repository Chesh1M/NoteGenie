package com.example.notegenie

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.notegenie.databinding.ActivityFlashcardTranslationBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


class FlashcardTranslation : AppCompatActivity() {

    // Initializing Global Variables
    var CURRENT_LANGUAGE = "En"
    var menuID = 0

    private lateinit var binding: ActivityFlashcardTranslationBinding
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardTranslationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)

        binding.flashcardsTranslationBackBtn.setOnClickListener {
            val switchActivity = Intent(this, FlashcardsPage::class.java)
            startActivity(switchActivity)
        }

        /* HANDLING FLASHCARDS */
        // Get topic from previous intent
        val intent = intent
        val topic = intent.getStringExtra("Flash Card Title").toString()

        // Initialize flashcard views
        val flashcardsTextView: TextView = findViewById(R.id.flashCardText)
        val flashcardCard: LinearLayout = findViewById(R.id.questionCard)
        val flashcardsTitle: TextView = findViewById(R.id.flashcardTitle)
        val summaryContentsView: TextView = findViewById(R.id.flashCardText)

        // Function to translate text
        fun translateText(textToTranslate: String, menuID: Int): String {

            if (menuID == 1) {
                /* START OF EN-CH TRANSLATION */
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
                        Toast.makeText(
                            this, "Translation Model Load Failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                // Actually translating
                englishChineseTranslator.translate(textToTranslate)
                    .addOnSuccessListener { translatedText ->
                        summaryContentsView.text = translatedText.toString()
                        Log.i("Translation Status:", "Sucessfuly Translated")
                    }
                    .addOnFailureListener { exception ->
                        Log.i("Translation Status:", "Error Translating")
                    }

                /* END OF EN-CH TRANSLATION */
            }
            return textToTranslate
        }

        val database = FirebaseDatabase.getInstance().getReference("Flashcards").child("$topic")
        database.get().addOnSuccessListener {
            // Loading the data from the database
            val retrievedMap = it.value as Map<String, String>
            // Getting the keys of summaries
            val listOfKeys = retrievedMap.keys.toList()
            // Setting it as strings
            val listOfKeysString = listOfKeys.joinToString()
            // Converting that string into a list
            val listOfQuestions = listOfKeysString.split(", ")

            // keep track of current qns/ans pair in the list
            val sizeOfList = listOfQuestions.size - 1
            var i = 0

            // Initialize `question to be displayed` variable
            var questionToDisplay: String
            var answerToDisplay: String?

            // to determine whether user has clicked on the flashcards to start
            var k = 0

            // track whether current display is either question or answer
            var a = 0
            var b = 0

            // display flashcard welcome text
            flashcardsTitle.text = "$topic"
            flashcardsTextView.text = "Click here to start!"

            // Changes view to next question (only if current view is the answer)
            binding.correctBtn.setOnClickListener {
                if (a != b) {
                    if (i == 0) {
                    }
                    else if (i < sizeOfList) {
                        translateText(listOfQuestions[i], menuID)
                        flashcardCard.setBackgroundColor(Color.parseColor("#EC5800"))
                        a++
                    } else {
                        Toast.makeText(this, "End of flashcards", Toast.LENGTH_SHORT).show()
                    }
                } else {}
            }
            // Same as correctBtn
            binding.wrongBtn.setOnClickListener {
                if (a != b) {
                    if (i == 0) {
                    }
                    else if (i < sizeOfList) {
                        translateText(listOfQuestions[i], menuID)
                        flashcardCard.setBackgroundColor(Color.parseColor("#EC5800"))
                        a++
                    } else {
                        Toast.makeText(this, "End of flashcards", Toast.LENGTH_SHORT).show()
                    }
                } else {}
            }
            // Changes view to answer (only if current view is on the question)
            binding.questionCard.setOnClickListener {
                if (b == a) {
                    if (i == 0 && k == 0) {
                        translateText(listOfQuestions[i], menuID)
                        k++
                    } else if (i < sizeOfList) {
                        translateText(retrievedMap[listOfQuestions[i]]!!, menuID)
                        flashcardCard.setBackgroundColor(Color.parseColor("#006DEC"))
                        i++
                        b++
                    } else {
                        Toast.makeText(this, "End of flashcards", Toast.LENGTH_SHORT).show()
                    }
                } else {}
            }
            // On click listener for the text itself (without this if user clicks the text itself the onclicklistener wouldn't respond, i.e. text doesn't count as the questionCard)
            binding.flashCardText.setOnClickListener {
                if (b == a) {
                    if (i == 0 && k == 0) {
                        translateText(listOfQuestions[i], menuID)
                        k++
                        println(menuID)
                    } else if (i < sizeOfList) {
                        translateText(retrievedMap[listOfQuestions[i]]!!, menuID)
                        flashcardCard.setBackgroundColor(Color.parseColor("#006DEC"))
                        i++
                        b++
                        println(menuID)
                    } else {
                        Toast.makeText(this, "End of flashcards", Toast.LENGTH_SHORT).show()
                    }
                } else {}
            }
            /* END OF HANDLING FLASHCARDS */


            // Initializing the view
            val currentLanguageTextView: TextView = findViewById(R.id.currentLanguageTextView)

            // Firstly initializing the widget
            val languagesMenuView: ImageView = findViewById(R.id.changeLanguageImageView)


            // Giving the scrollbar to the text view
            summaryContentsView.movementMethod = ScrollingMovementMethod()

            // Now initializing the pop-up menu
            val changeLanguagePopupMenu = PopupMenu(this, languagesMenuView)

            // Adding the elements of the popup menu
            changeLanguagePopupMenu.menu.add(Menu.NONE, 0, 0, "English (En)")
            changeLanguagePopupMenu.menu.add(Menu.NONE, 1, 1, "Chinese (Ch)")
            changeLanguagePopupMenu.menu.add(Menu.NONE, 2, 2, "Tamil (Ta)")
            changeLanguagePopupMenu.menu.add(Menu.NONE, 3, 3, "Malay (Ma)")

            // Handling item clicks
            changeLanguagePopupMenu.setOnMenuItemClickListener { menuItem ->

                // Getting id of menu
                //val menuID = menuItem.itemId
                menuID = menuItem.itemId
                println(menuID)

                // Handling navigation
                if (menuID == 0) {

                    // Switching the Language
                    CURRENT_LANGUAGE = "En"
                    currentLanguageTextView.text = CURRENT_LANGUAGE

                    // Notifying the user of the change
                    Toast.makeText(
                        this, "Language Changed to English",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (menuID == 1) {

                    // Switching the Language
                    CURRENT_LANGUAGE = "Ch"
                    currentLanguageTextView.text = CURRENT_LANGUAGE

                    // Notifying the user of the change
                    Toast.makeText(
                        this, "Language Changed to Chinese",
                        Toast.LENGTH_SHORT
                    ).show()

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
                            Toast.makeText(
                                this, "Translation Model Load Failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    // Actually translating
                    englishChineseTranslator.translate(summaryContentsView.text.toString())
                        .addOnSuccessListener { translatedText ->
                            summaryContentsView.text = translatedText.toString()
                            Log.i("Translation Status:", "Sucessfuly Translated")
                        }
                        .addOnFailureListener { exception ->
                            Log.i("Translation Status:", "Error Translating")
                        }


                } else if (menuID == 2) {

                    // Switching the Language
                    CURRENT_LANGUAGE = "Ta"
                    currentLanguageTextView.text = CURRENT_LANGUAGE

                    // Notifying the user of the change
                    Toast.makeText(
                        this, "Language Changed to Tamil",
                        Toast.LENGTH_SHORT
                    ).show()

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
                            Toast.makeText(
                                this, "Translation Model Load Failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    // Actually translating
                    englishTamilTranslator.translate(summaryContentsView.text.toString())
                        .addOnSuccessListener { translatedText ->
                            summaryContentsView.text = translatedText.toString()
                            Log.i("Translation Status:", "Sucessfuly Translated")
                        }
                        .addOnFailureListener { exception ->
                            Log.i("Translation Status:", "Error Translating")
                        }

                } else if (menuID == 3) {

                    // Switching the Language
                    CURRENT_LANGUAGE = "Ma"
                    currentLanguageTextView.text = CURRENT_LANGUAGE

                    // Notifying the user of the change
                    Toast.makeText(
                        this, "Language Changed to Malay",
                        Toast.LENGTH_SHORT
                    ).show()

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
                            Toast.makeText(
                                this, "Translation Model Load Failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    // Actually translating
                    englishMalayTranslator.translate(summaryContentsView.text.toString())
                        .addOnSuccessListener { translatedText ->
                            summaryContentsView.text = translatedText.toString()
                            Log.i("Translation Status:", "Sucessfuly Translated")
                        }
                        .addOnFailureListener { exception ->
                            Log.i("Translation Status:", "Error Translating")
                        }
                }
                false
            }

            // Setting an onclick listener for the changeLanguageImageView
            languagesMenuView.setOnClickListener {
                changeLanguagePopupMenu.show()
            }
        }
    }
}