package com.example.notegenie

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.Equalizer.Settings
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApi
import com.google.firebase.auth.FirebaseAuth
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class HomePage : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isManagePermissionGranted = false
    private lateinit var selectedFileURI: Uri
    private val RECORD_REQUEST_CODE = 111
    private val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        firebaseAuth = FirebaseAuth.getInstance()

        // Initializing by checking for permissions
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permission ->
            isReadPermissionGranted = permission[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isManagePermissionGranted = permission[android.Manifest.permission.MANAGE_EXTERNAL_STORAGE] ?: isManagePermissionGranted
        }

        requestPermission()

        // Status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.bgColor)

        // Initializing the pop-up menu

        // Firstly initializing the widget
        val navigatePagesMenuView: ImageView = findViewById(R.id.navigatePagesMenu)

        // Now initializing the pop-up menu
        val navPopupMenu = PopupMenu(this, navigatePagesMenuView)

        // Adding the elements of the popup menu
        navPopupMenu.menu.add(Menu.NONE, 0, 0, "Home")
        navPopupMenu.menu.add(Menu.NONE, 1, 1, "Summaries")
        navPopupMenu.menu.add(Menu.NONE, 2, 2, "Flashcards")
        navPopupMenu.menu.add(Menu.NONE, 3, 3, "Settings")

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
                val switchActivity = Intent(this, FlashcardTranslation::class.java)
                startActivity(switchActivity)
            } else if(menuID==3){
                val switchActivity = Intent(this, SettingsPage::class.java)
                startActivity(switchActivity)
            }

            false


        }

        // Setting an on click listener for the popup view
        navigatePagesMenuView.setOnClickListener{
            navPopupMenu.show()
        }

    }

    // Function to add a file
    fun addFile(view: View){

        // Initializing an intent
        val intentAudio: Intent = Intent()

        // Initializing the pop-up menu

        // Firstly initializing the widget
        val navigatePagesMenuView: Button = findViewById(R.id.addFileButton)

        // Now initializing the pop-up menu
        val navPopupMenu = PopupMenu(this, navigatePagesMenuView)

        // Adding the elements of the popup menu
        navPopupMenu.menu.add(Menu.NONE, 0, 0, "From Slides")
        navPopupMenu.menu.add(Menu.NONE, 1, 1, "From Audio Recording")
        // Handling item clicks
        navPopupMenu.setOnMenuItemClickListener { menuItem->

            // Getting id of menu
            val menuID = menuItem.itemId


            // Handling navigation
            if (menuID==0){

                // Setting the type to get
                intentAudio.setType("application/pdf")

                // Setting the action
                intentAudio.action = Intent.ACTION_OPEN_DOCUMENT

//                // Starting the activity
                startActivityForResult(Intent.createChooser(intentAudio, "Select File"), 1)

            } else if(menuID==1){
                // Setting the type to get
                intentAudio.setType("audio/*")

                // Setting the action
                intentAudio.action = Intent.ACTION_OPEN_DOCUMENT

//                // Starting the activity
                startActivityForResult(Intent.createChooser(intentAudio, "Select File"), 1)
            }

            false

        }

        // Activating the pop-up menu
        navPopupMenu.show()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {

            // Audio is Picked in format of URI
            if (data != null) {
                selectedFileURI = data.getData()!!

            }

            // Getting the type of the file
            val fileType = selectedFileURI.toString().split(".").last().toString()

            // If the file is an audio file
            Toast.makeText(this, fileType, Toast.LENGTH_LONG).show()
            if (fileType == "m4a" || fileType=="mp3" || fileType=="wav"){

                callWhisperModel(selectedFileURI){response ->
                    runOnUiThread {
                        Log.i("Response from whisper", response.toString())
                    }
                }

            } else{
                getTextFromPDF(selectedFileURI)
            }
        }
    }

    // Function to call the Whisper model
    @RequiresApi(Build.VERSION_CODES.O)
    fun callWhisperModel(audioFilePath: Uri, callBack: (String) -> Unit){


        // Setting the audio file path
        val audioFilePathString = audioFilePath.path.toString()
        val path = Environment.getExternalStorageDirectory().path.toString()

        Log.i("File Path", path+"/Lecture Recordings/Part 1.m4a")
        val audioFileMediaPlayer = File("/storage/emulated/Lecture Recordings/Part 1.m4a")

        val fileData = ByteArray(audioFileMediaPlayer.length().toInt())
        val inputStream: FileInputStream = FileInputStream(audioFileMediaPlayer)
        val audioFilePlayer = inputStream.read(fileData)
        inputStream.close()

        // Initializing the api key & link
        val apiKey = "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"
        val apiLink = "https://api.openai.com/v1/audio/transcriptions"

        // Getting the parameters from OpenAI
        val requestBody = """{
                                "file": "$audioFilePlayer",
                                "model": "whisper-1"
                                }
                                """

        // Getting a request from Openhttp
        val request = Request.Builder().url(apiLink)
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("file", audioFileMediaPlayer.name)
            .addHeader("model", "whisper-1")
            .post(requestBody.toRequestBody("multipart/form-data".toMediaTypeOrNull()))
            .build()


        // Getting the value from whisper
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OPENAI call error", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val transcribedText = response.body?.string()
                Log.i("Transcribed text", transcribedText.toString())
            }
        })






    }

    // FUnction to get text from pdf
    fun getTextFromPDF(pdfFilePath: Uri){
        try {
            var parsedText = ""
            val reader = PdfReader(pdfFilePath.path.toString())
            val n: Int = reader.getNumberOfPages()
            for (i in 0 until n) {
                parsedText = """
            ${parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim()}
            
            """.trimIndent() //Extracting the content from the different pages
            }
            Toast.makeText(this, parsedText, Toast.LENGTH_LONG).show()
            reader.close()
        } catch (e: Exception) {
            Log.e("Loading PDF Error", e.toString())
        }
    }

    private fun requestPermission(){
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isManagePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if(!isReadPermissionGranted){

            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        }

        if(!isManagePermissionGranted){

            permissionRequest.add(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        }

        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }


    }








    }