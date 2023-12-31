package com.example.notegenie

import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.documentfile.provider.DocumentFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


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
    private val numOfRecents = 5 // Get the total number of recent files from settings


    @RequiresApi(Build.VERSION_CODES.O)
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

        // Getting today's date
        val formatter = DateTimeFormatter.ofPattern("dd")
        val todayDate = LocalDateTime.now().format(formatter)

        // Initializing the widgets
        val todaysDayTextView: TextView = findViewById(R.id.todaysDayTextView)
        todaysDayTextView.text = "Active Recall: "+todayDate.toString()+dateSuffix(todayDate.toString())

        // Loading the list for active recall
        getListForActiveRecall()

        // Loading the Last-edited dates
        getRecentlyAddedSummaries()

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
                val switchActivity = Intent(this, FlashcardsPage::class.java)
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
                selectedFileURI = data.data!!
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
                var filepath = selectedFileURI.path.toString()

                // Removing the unnecessary string bits
                filepath = filepath.drop(18)

                // Appending it to make it look like a legit directory (For Local Device)
                val actualFolderPath = Environment.getExternalStorageDirectory()
                    .path.toString()+"/"+filepath

//                // For my s20+
//                // Appending it to make it look like a legit directory
//                val actualFolderPath = "/storage/self/primary/"+filepath


                // Extracting text from pdf
                val extractedTextFromPDF = convertPDFToText(actualFolderPath)

                // Changing the Intent to the Add Summary page
                // Initializing a new intent to go to the next activity and also its loading screen
                val addSummaryPageIntent = Intent(this, AddSummaryPage:: class.java)
                val summaryLoadingScreenIntent = Intent(
                    this,
                    GeneratingSummaryLoadingScreen::class.java
                )

                // Pushing the data to the next activity
                addSummaryPageIntent.putExtra("Summary Content", extractedTextFromPDF)

                // Going to the Add Summary Page
                startActivity(addSummaryPageIntent)
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

    // Function to extract text from pdf
    fun convertPDFToText(src: String?): String {
        try {
            //create pdf reader
            val pr = PdfReader(src)

            //get the number of pages in the document
            val pNum = pr.numberOfPages

            // Initializing the text variable
            var finalText = ""

            //extract text from each page and write it to the output text file
            for (page in 1..pNum) {

                //text is the required String (initialized as "" )
                finalText = finalText + android.R.attr.text.toString() + PdfTextExtractor.getTextFromPage(pr, page)
            }

            return finalText

        } catch (e: Exception) {
            e.printStackTrace()
            return "PDF READ ERROR"
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

    // Function to convert URI to path
    fun convertUriToPath(selectedFileURI: Uri): String? {

        // Initializing the file
        val file = selectedFileURI.path?.let { File(it) } //create path from uri

        // Doing file string manipulations
        val split = file?.path?.split(":".toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray() //split the path.

        // Getting the file path
        val filePath = split?.get(1) //assign it to a string(your choice).

        // Returning the file path
        return filePath
    }

    // Function for the finder
    fun finder(view: View){

        // Initializing the text view
        val finderEditText: EditText = findViewById(R.id.finderEditText)

        // Getting the text from the textview
        val searchPrompt = finderEditText.text

        // Opening the Firebase Database
        val firebaseDatabaseSummaries = FirebaseDatabase.getInstance()
            .getReference("Summaries")

        // Loading the values
        firebaseDatabaseSummaries.get().addOnSuccessListener {it ->

            // Loading the data from the database
            val retrievedMap = it.value as Map<String, String>

            // Getting the list of keys
            val listOfKeys = retrievedMap.keys.toList()

            // Initializing a boolean if match found
            var promptFound = false

            // Initializing a counter for the values list
            var counterI = 0

            // Looping through the prompt to get a match
            listOfKeys.forEach { key ->

                // Looking for a match
                if (key.toString() == searchPrompt.toString()){
                    // If there is a match
                    promptFound = true

                    // Getting the content

                    // Getting the values associated with the root
                    val listOfValues = retrievedMap.values.toList()

                    // Setting it as strings
                    var listOfValuesString = listOfValues.joinToString()

                    // Removing the first square bracket from the list
                    listOfValuesString = listOfValuesString.substring(1)

                    // Removing the last square bracket
                    listOfValuesString = listOfValuesString.substring(0, listOfValuesString.length-1)


                    // Converting that string into a list
                    val listOfValuesArray = listOfValuesString.split("}, {")

                    // Getting the date
                    val lastEditedDate = listOfValuesArray[counterI].take(10)

                    // Getting the content
                    val summaryContent = listOfValuesArray[counterI].drop(11)


                    // Initializing a new intent to go to the next activity
                    val displaySummaryContent = Intent(this, DisplaySummaryContent:: class.java)

                    // Pushing the data to the next activity
                    displaySummaryContent.putExtra("Summary Title", key)
                    displaySummaryContent.putExtra("Edit Date", lastEditedDate)
                    displaySummaryContent.putExtra("Summary Content", summaryContent)

                    // Switching to the next activity
                    startActivity(displaySummaryContent)

                    // Setting the text to nothing once entered
                    finderEditText.setText("")

                }

                // Updating the counter
                counterI += 1


            }

            // If the prompt is false, toast an error message

            if(promptFound == false){
                // Toasting that the file does not exist
                Toast.makeText(this, "File does not exist in database", Toast.LENGTH_LONG).show()
            }



        }
    }


    // Function to get the list of content for revision
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getListForActiveRecall() {

        // Initializing a list of summary topics to be returned
        val listOfSummaryTopics = mutableListOf<String>()

        // Loading the cloud
        val firebaseDatabaseSummaries = FirebaseDatabase.getInstance()
            .getReference("Summaries")

        // Initializing a date formatter
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")

        // Initializing the calendar instances of the specific dates
        val tomorrowDate = Calendar.getInstance()
        val dateAfterOneWeek = Calendar.getInstance()
        val dateAfterOneMonth = Calendar.getInstance()

        // Adding the dates to get the specific times
        tomorrowDate.add(Calendar.DAY_OF_YEAR, -1)
        dateAfterOneWeek.add(Calendar.WEEK_OF_YEAR, -1)
        dateAfterOneMonth.add(Calendar.MONTH, -1)

        // Initializing the key target days into a list
        val targetDays = listOf<String>(dateFormat.format(tomorrowDate.time).toString(),
            dateFormat.format(dateAfterOneWeek.time).toString(),
            dateFormat.format(dateAfterOneMonth.time).toString())

        // Loading the values
        firebaseDatabaseSummaries.get().addOnSuccessListener {it ->

            // Initializing a layout
            val layoutResId: Int = android.R.layout.simple_list_item_1

            // Loading the data from the database
            val retrievedMap = it.value as Map<String, String>

            // Getting the content

            // Getting the values associated with the root
            val listOfValues = retrievedMap.values.toList()

            // Setting it as strings
            var listOfValuesString = listOfValues.joinToString()

            // Removing the first square bracket from the list
            listOfValuesString = listOfValuesString.substring(1)

            // Removing the last square bracket
            listOfValuesString = listOfValuesString.substring(0, listOfValuesString.length-1)

            // Converting that string into a list
            val listOfValuesArray = listOfValuesString.split("}, {")

            // Initializing a list of keys
            val listOfKeys = retrievedMap.keys.toList()

            // Initializing a counter to loop through the keys
            var keysCounter = 0

            // Looping through the list of values
            listOfValuesArray.forEach { value->

                // Getting the last edited date
                val lastEditedDate = value.take(10)

                // If the value is in the list
                if (lastEditedDate in targetDays){

                    // Add the summary
                    listOfSummaryTopics.add(listOfKeys[keysCounter])
                }
                // Updating the keys counter
                keysCounter += 1
            }

            // If the list is empty
            if (listOfSummaryTopics.isEmpty()){

                // Notify the user that no revisions are available
                listOfSummaryTopics.add("No revisions available")

            }

            // Initializing an array adapter
            val arraySummaryAdapter = ArrayAdapter(this, layoutResId, listOfSummaryTopics)

            // Initializing a list view to display this on
            val activeRecallListView: ListView = findViewById(R.id.activeRecallListView)

            // Setting the adapter to the ListView
            activeRecallListView.adapter = arraySummaryAdapter

            // What happens when an item is clicked

            activeRecallListView.setOnItemClickListener { parent, view, position, id ->

                // Initializing a new Intent
                val flashCardTranslationIntent = Intent(this,
                    FlashcardTranslation::class.java)

                // Passing the title string into the FlashCard Intent if it is not "No revisions available"
                if (listOfSummaryTopics[position] != "No revisions available"){

                    // Pushing the data to the next activity
                    flashCardTranslationIntent.putExtra("Flash Card Title",
                        listOfSummaryTopics[position])

                    // Switching to the next activity
                    startActivity(flashCardTranslationIntent)
                }else{
                    Toast.makeText(this, "Nothing to review today!", Toast.LENGTH_LONG)
                        .show() //DO NOT REMOVE
                }
            }
        }
    }

    // Function to get the recently added Summaries
    // Function to get the list of content for revision
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRecentlyAddedSummaries() {

        // Initializing a list of summary topics to be returned
        val listOfSummaryTopics = mutableListOf<String>()

        // Loading the cloud
        val firebaseDatabaseSummaries = FirebaseDatabase.getInstance()
            .getReference("Summaries")

        // Loading the values
        firebaseDatabaseSummaries.get().addOnSuccessListener {it ->

            // Initializing a layout
            val layoutResId: Int = android.R.layout.simple_list_item_1

            // Loading the data from the database
            val retrievedMap = it.value as Map<String, String>

            // Getting the content

            // Getting the values associated with the root
            val listOfValues = retrievedMap.values.toList()

            // Initializing a list for last-edited dates
            var listOfLastEditedDates = mutableMapOf<String, LocalDate>()
//
//            mapOfValues.forEach { value->
//                listOfLastEditedDates.add(value.take(10))
//            }
//
//            Log.i("24/7", listOfLastEditedDates.toString())

//            // Getting the list of last-edited dates
//            val listOfLastEditedDates = mapOfValues

            // Setting it as strings
            var listOfValuesString = listOfValues.joinToString()
//
            // Removing the first square bracket from the list
            listOfValuesString = listOfValuesString.substring(1)

            // Removing the last square bracket
            listOfValuesString = listOfValuesString.substring(0, listOfValuesString.length-1)


            // Converting that string into a list
            val listOfValuesArray = listOfValuesString.split("}, {")

            // Initializing a list of keys
            val listOfKeys = retrievedMap.keys.toList()

            // Getting the number of files
            val numOfFiles = listOfKeys.size


            // Initializing the view to display the size
            val storageSizeProgressBar: ProgressBar = findViewById(R.id.storageSizeProgressBar)
            val progressTextView: TextView = findViewById(R.id.progressTextView)

            // Setting the text for the text view
            progressTextView.text = numOfFiles.toString()+"/1000"

            // Setting the maximum allowed
            storageSizeProgressBar.max = 1000

            // Setting the Progress Bar
            storageSizeProgressBar.setProgress(numOfFiles, true)
            storageSizeProgressBar


            // Initializing a counter to loop through the keys
            var keysCounter = 0

            // Initializing a date-time formatter
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)

            // Adding the last edited dates to the necessary keys
            listOfValuesArray.forEach { value->

                // Adding the values into the map
                listOfLastEditedDates.put(listOfKeys[keysCounter], LocalDate.parse(value.take(10), formatter))

                // Incrementing the counter
                keysCounter += 1
            }

            // Sorting the list and reversing it
            listOfLastEditedDates = listOfLastEditedDates.toList().sortedBy { it.second }.toMap()
                .toList().reversed().toMap() as MutableMap<String, LocalDate>

            // Getting only the list of Keys to be Displayed
            var displayedListOfKeys = listOfLastEditedDates.keys.toList()




            Log.i("24/7", listOfLastEditedDates.toString())

            // Only displaying the  top 5 results
            displayedListOfKeys = displayedListOfKeys.take(numOfRecents)


            // Initializing an array adapter
            val arraySummaryAdapter = ArrayAdapter(this, layoutResId, displayedListOfKeys)

            // Initializing a list view to display this on
            val recentlyAddedListView: ListView = findViewById(R.id.recentlyAddedListView)

            // Setting the adapter to the ListView
            recentlyAddedListView.adapter = arraySummaryAdapter

            // What happens when an item is clicked

            recentlyAddedListView.setOnItemClickListener { parent, view, position, id ->

                // Initializing a new Intent
                val flashCardTranslationIntent = Intent(this,
                    FlashcardTranslation::class.java)

                // Passing the title string into the FlashCard Intent if it is not "No revisions available"
                if (displayedListOfKeys[position] != "No revisions available"){

                    // Pushing the data to the next activity
                    flashCardTranslationIntent.putExtra("Flash Card Title",
                        displayedListOfKeys[position])

                    // Switching to the next activity
                    startActivity(flashCardTranslationIntent)
                }else{
                    Toast.makeText(this, "Nothing to review today!", Toast.LENGTH_LONG)
                        .show() //DO NOT REMOVE
                }



        }
        }

    }

    // Function to determine the suffix of the date
    fun dateSuffix(todayDate: String): String {

        // If it is 1
        if (todayDate.takeLast(1) == "1"){
            return "st"
        }else if(todayDate.takeLast(1) == "2"){
            return "nd"
        } else if(todayDate.takeLast(1) == "3"){
            return "rd"
        }

        return "th"
    }



    companion object {
        val MEDIA_TYPE_AUDIO = "multipart/form-data".toMediaType()
    }










}