package com.example.notegenie

import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException


class HomePage : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isManagePermissionGranted = false
    private var isAccessNetworkStatePermissionGranted = false
    private var isInternetPermissionGranted = false
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
            isInternetPermissionGranted = permission[android.Manifest.permission.INTERNET] ?: isInternetPermissionGranted
            isAccessNetworkStatePermissionGranted = permission[android.Manifest.permission.ACCESS_NETWORK_STATE] ?: isAccessNetworkStatePermissionGranted
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
            val fileType = "folder"

            if (fileType == "m4a" || fileType=="mp3" || fileType=="wav"){

                callCHATGPT("How are you today?"){response ->
                    runOnUiThread {
                        Log.i("Response from GPT", response.toString())
                    }
                }

//                callWhisperModel(selectedFileURI){response ->
//                    runOnUiThread {
//                        Log.i("Response from whisper", response.toString())
//                    }
//                }

            } else{ // The file is a collection of lecture slides

                // Getting the folder path from user
                val folderPathFromUser = "/Documents/Lecture Slides"

                // Appending it to make it look like a legit directory
                val actualFolderPath = Environment.getExternalStorageDirectory().path.toString()+folderPathFromUser

                // Getting all the files inside the directory
                val lectureSlideFile = File(actualFolderPath)
                val arrayOfLectureSlideFiles = lectureSlideFile.listFiles()

                // Initializing a list for the content
                val listOfContentPerPage = mutableListOf<String>()

                // Google ML-Kit
                // When using Latin script library
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                // Looping through all the slide images
                for (i in arrayOfLectureSlideFiles.indices){

                    // Getting the current file name
                    val currFileDirectory = arrayOfLectureSlideFiles.get(i).toString()

                    // Applying the Text recognition api from ML tool kit

                    val image: InputImage
                    try {
                        // Input Image
                        image = InputImage.fromFilePath(this, Uri.fromFile(File(currFileDirectory)))

                        // Handling the request
                        val result = recognizer.process(image)
                            .addOnSuccessListener { visionText ->

                                // Getting the content that ML Kit read
                                listOfContentPerPage.add(visionText.text)

                                // If it is the last page
                                if (i == (arrayOfLectureSlideFiles.size)-1){

                                    // Combining all the elements within that list
                                    val rawContentOfLecture = listOfContentPerPage.joinToString("NEWPAGE")

                                    // Changing the Intent to the Add Summary page
                                    // Initializing a new intent to go to the next activity
                                    val addSummaryPageIntent = Intent(this, AddSummaryPage:: class.java)

                                    // Pushing the data to the next activity
                                    addSummaryPageIntent.putExtra("Summary Content", rawContentOfLecture)

                                    // Switching to the next activity
                                    startActivity(addSummaryPageIntent)

                                    Log.i("Lecture content", rawContentOfLecture)

                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("Error extracting text from image", e.toString())
                            }

                    } catch (e: IOException) {
                        Log.i("The URI", Uri.fromFile(File(currFileDirectory)).toString())
                        e.printStackTrace()
                    }
                }






            }
        }
    }

    // Function to call the Whisper model
    @OptIn(ExperimentalUnsignedTypes::class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun callWhisperModel(audioFilePath: Uri, callBack: (String) -> Unit){


        // Setting the audio file path
        val audioFilePathString = audioFilePath.path.toString()
        val path = Environment.getExternalStorageDirectory().path.toString()

        Log.i("File Path", path+"/Lecture Recordings/Part 1.m4a")
        val audioFileMediaPlayer = File("/storage/emulated/Lecture Recordings/whisperTest.wav")
//        val audioFileMediaPlayerBytes = File("/storage/emulated/Lecture Recordings/Test.m4a").readBytes().asUByteArray()

//        val inputStream: FileInputStream = FileInputStream(audioFileMediaPlayer)
//        val base64File = Base64InputStream(inputStream, 1)


        // Initializing the api key & link
        val apiKey = "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"
        val apiLink = "https://api.openai.com/v1/audio/transcriptions"

        // Getting the parameters from OpenAI
        val requestBody = """{
                                "file": "audio.mp3",
                                "model": "whisper-1"
                                }
                                """

        // Getting a request from Openhttp
        val request = Request.Builder().url(apiLink)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("file", "$audioFileMediaPlayer")
            .addHeader("model", "whisper-1")
            .post(audioFileMediaPlayer.asRequestBody(MEDIA_TYPE_AUDIO))
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

    // Function to call CHATGPT
    fun callCHATGPT(question: String, param: (Any) -> Unit){

        // Initializing the api key & link
        val apiKey = "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"
        val apiLink = "https://api.openai.com/v1/completions"

        // Getting the parameters from OpenAI
        val requestBody = """{
  "model": "text-davinci-003",
  "prompt": "Say this is a test",
  "max_tokens": 7,
  "temperature": 0,
  "top_p": 1,
  "n": 1,
  "stream": false,
  "logprobs": null,
  "stop": "\n"
}
"""

        // Getting a request from Openhttp
        val request = Request.Builder().url(apiLink)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        // Getting the value from Chat GPT
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OPENAI call error", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val transcribedText = response.body?.string()
                Log.i("Question: $question", transcribedText.toString())
            }
        })

    }

    private fun extractPDF() {
        try {
            // creating a string for
            // storing our extracted text.
            var extractedText = ""

            // creating a variable for pdf reader
            // and passing our PDF file in it.
            val reader = PdfReader("res/raw/amiya_rout.pdf")

            // below line is for getting number
            // of pages of PDF file.
            val n = reader.numberOfPages

            // running a for loop to get the data from PDF
            // we are storing that data inside our string.
            for (i in 0 until n) {
                extractedText = """
                $extractedText${
                    PdfTextExtractor.getTextFromPage(reader, i + 1).trim { it <= ' ' }
                }
                
                """.trimIndent()
                // to extract the PDF content from the different pages
            }

            // after extracting all the data we are
            // setting that string value to our text view.
            Log.i("Text From PDF", extractedText.toString())

            // below line is used for closing reader.
            reader.close()
        } catch (e: java.lang.Exception) {
            // for handling error while extracting the text file.
            Log.i("Error","Error found is : \n$e")
        }
    }


    // Official function to request for permissions
    private fun requestPermission(){
        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isManagePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isInternetPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED

        isAccessNetworkStatePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()

        if(!isReadPermissionGranted){

            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        }

        if(!isManagePermissionGranted){

            permissionRequest.add(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        }

        if(!isInternetPermissionGranted){

            permissionRequest.add(android.Manifest.permission.INTERNET)

        }

        if(!isAccessNetworkStatePermissionGranted){

            permissionRequest.add(android.Manifest.permission.ACCESS_NETWORK_STATE)

        }

        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }



    }

    // Function to convert from pdf to images for ML Kit to recognise



    companion object {
        val MEDIA_TYPE_AUDIO = "multipart/form-data".toMediaType()
    }










}