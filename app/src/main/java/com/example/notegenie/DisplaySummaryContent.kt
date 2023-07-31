package com.example.notegenie

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.notegenie.databinding.ActivityDisplaySummaryContentBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.io.File

class DisplaySummaryContent : AppCompatActivity() {

    // Initializing Global Variables
    var CURRENT_LANGUAGE = "En"

    private val STORAGE_CODE: Int = 100

    // Initializing a binding
    private lateinit var binding: ActivityDisplaySummaryContentBinding
    private lateinit var exportButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplaySummaryContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.summaryDisplayBgColor)

        // Getting the variables from the previous activity
        var summaryTitle =  intent.getStringExtra("Summary Title")
        val summaryLastEditDate = intent.getStringExtra("Edit Date")
        val summaryContent = intent.getStringExtra("Summary Content")

        // Setting up the export button
        exportButton = findViewById(R.id.exportButton)

        // Setting the title
        summaryTitle = "Summary Of:\n" + summaryTitle

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

        exportButton.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
                //system OS >= Marshmallow(6.0), check permission is enabled or not
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                    //permission was not granted, request it
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions, STORAGE_CODE)
                }
                else{
                    //permission already granted, call savePdf() method
                    exportToPDF()
                }
            }
            else{
                //system OS < marshmallow, call savePdf() method
                exportToPDF()
            }
        }



    }

    // Function to export to pdf
    fun exportToPDF(){
        val pdf = PdfDocument()
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(
            792, 1120, 1).create())
        val canvas = page.canvas
        val paint = Paint()

        val text = ""
        try {
            paint.textSize = 15F
            canvas.drawText(text,100F,200F,paint )
            pdf.finishPage(page)

            val fileName = "doc${System.currentTimeMillis()}.pdf"
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            pdf.writeTo(file.outputStream())
            pdf.close()

            Toast.makeText(applicationContext, "$fileName has been created", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            Toast.makeText(applicationContext, "Error: ${e.toString()}", Toast.LENGTH_SHORT).show()
        }
    }
}