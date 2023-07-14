package com.example.notegenie

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class AddSummaryPage : AppCompatActivity() {

    // Initializing the global variables
    private val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_summary_page)

        // Initializing the layouts
        val summaryTextEditText: TextView = findViewById(R.id.summaryContentsView)
        val summaryFileNameEditText: EditText = findViewById(R.id.summaryFileName)
        val dateTextView: TextView = findViewById(R.id.modifiedDate)
        val saveButton: Button = findViewById(R.id.saveButton)
        val displaySummaryContentTextView: TextView = findViewById(R.id.summaryContentsView)

        // Setting the timeout for the client
        client.newBuilder().connectTimeout(60, TimeUnit.MINUTES)
        client.newBuilder().readTimeout(60, TimeUnit.MINUTES)

        // Initializing the content from the Home page
        val unSummarisedContent = intent.getStringExtra("Summary Content").toString()

        // Getting the current date
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())

        // Setting the text of the Date TextView to the date
        dateTextView.text = "Date: $currentDate"

        // Cleaning the text
        val re = Regex("[^A-Za-z0-9 ]")
        var cleanedTextSummary = re.replace(unSummarisedContent, "")
        cleanedTextSummary = cleanTextForJSON(cleanedTextSummary)


        Log.i("This is the unsummarised content", cleanedTextSummary)

        // Getting the title
        val titleOfSummary = summaryFileNameEditText.text

//        saveSummaryToFirebase("Circuit Analysis", currentDate, "This is very interesting")

        callChatGPT(this,"Turn this note into a summary: $cleanedTextSummary"){response ->
            runOnUiThread {
                Log.i("Response from GPT", response.toString())
            }
        }
    }

    // FUnction to call ChatGPT
    fun callChatGPT(onSummayPageContext: Context,question: String, param: (Any) -> Unit){

        // Getting the current date
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())

        // Initializing appropriate widgets
        val saveButton: Button = findViewById(R.id.saveButton)
        val summaryFileNameEditText: EditText = findViewById(R.id.summaryFileName)


        // Initializing the api key & link
        val apiKey = "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"
        val apiLink = "https://api.openai.com/v1/chat/completions"

        // Getting the parameters from OpenAI
        val requestBody = """{
  "model": "gpt-3.5-turbo",
  "messages": [{"role": "system", "content": "You are a helpful assistant."}, {"role": "user", "content": "'$question'"}]
}
"""

        // Getting a request from Openhttp
        val request = Request.Builder().url(apiLink)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("model", "gpt-3.5-turbo")
            .addHeader("messages", "{\"role\": \"user\", \"content\": \"Hello!\"}")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

//        // Initializing the api key & link
//        val apiKey = "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"
//        val apiLink = "https://api.openai.com/v1/completions"
//
//        // Getting the parameters from OpenAI
//        val requestBody = """{
//  "model": "text-davinci-003",
//  "prompt": "$question"
//}
//"""
//
//        // Getting a request from Openhttp
//        val request = Request.Builder().url(apiLink)
//            .addHeader("Content-Type", "application/json")
//            .addHeader("Authorization", "Bearer $apiKey")
//            .addHeader("model", "text-davinci-003")
//            .addHeader("prompt", question)
//            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
//            .build()


        // Getting the value from Chat GPT
        client.newCall(request).enqueue(object: Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("OPENAI call error", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val transcribedText = response.body?.string()

                // Removing the first set of strings
                var answerFromChatGPT = transcribedText.toString().split("\"content\": ")[1]

//                // Removing the last set of strings
                answerFromChatGPT = answerFromChatGPT.split("},")[0]

                // Removing the first character
                answerFromChatGPT = answerFromChatGPT.substring(1)
//
                // Removing the last character
                answerFromChatGPT = answerFromChatGPT.substring(0, answerFromChatGPT.length-1)
                answerFromChatGPT = answerFromChatGPT.substring(0, answerFromChatGPT.length-1)

                // Removing a few newances
                answerFromChatGPT = answerFromChatGPT.replace("\\n'\\n", " ")
                answerFromChatGPT = answerFromChatGPT.replace("Summary: ", "")

                // Initializing the text view
                val displaySummaryContentTextView: TextView = findViewById(R.id.summaryContentsView)

                Log.i("Answer from ChatGPT", answerFromChatGPT)

                // Posting the text onto the Text View
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    // Setting the text
                    displaySummaryContentTextView.text = answerFromChatGPT

                    // Setting an onclick listener for the Save Button
                    saveButton.setOnClickListener(View.OnClickListener { view ->

                        // Getting the title
                        val titleOfSummary = summaryFileNameEditText.text

                        // Checking if the summary is already in the database
                        // If the summary does not exist in database
                        FirebaseDatabase.getInstance().getReference().child("Summaries").get().addOnSuccessListener { it->

                            // Getting the data from the database
                            val dataFromDatabase  = it.value as Map<String, String>

                            // Getting the keys
                            val keysFromDatabase = dataFromDatabase.keys.toList()

                            // Checking if the summary title exists in database
                            if (keysFromDatabase.contains(titleOfSummary.toString())){

                                // Does not save

                            }else{

                                // Storing into database
                                FirebaseDatabase.getInstance().getReference()
                                    .child("Summaries")
                                    .child(titleOfSummary.toString()).child(currentDate).setValue(answerFromChatGPT);

                            }
                        }

                        // Going back to home page
                        finish()

                    })
                }



//                Log.i("Question: $question", answerFromChatGPT)
            }
        })

    }

    // Function to clean text to ensure proper JSON input
    fun cleanTextForJSON(inputText: String): String {

        var inputTextString = inputText

        // Removing the "
        inputTextString = inputTextString.replace("\"", "")

        // Removing the \
        inputTextString = inputTextString.replace("\\", "")

        // Removing the /
        inputTextString = inputTextString.replace("/", "")

        // Removing the :
        inputTextString = inputTextString.replace(":", "")

        // Removing the $
        inputTextString = inputTextString.replace("$", "")

        // Removing the new line character
        inputTextString = inputTextString.replace("\n", "")

        // Removing the ą
        inputTextString = inputTextString.replace("ą", "")

        // Returning the final text
        return inputTextString


    }

    // Function to save the summary into the firebase database
    fun saveSummaryToFirebase(summaryName: String, summaryDate: String,
                              summarisedSummaryContent: String){


        // Checking if the summary is already in the database
        // If the summary does not exist in database
        FirebaseDatabase.getInstance().getReference().child("Summaries").get().addOnSuccessListener { it->

            // Getting the data from the database
            val dataFromDatabase  = it.value as Map<String, String>

            // Getting the keys
            val keysFromDatabase = dataFromDatabase.keys.toList()

            // Checking if the summary title exists in database
            if (keysFromDatabase.intersect(listOf(summaryName).toSet()) != setOf<String>()){

                // If the element exists
                Toast.makeText(this, "This summary already exists! Choose another title",
                    Toast.LENGTH_LONG).show()

            }else{

                // If the element exists
                Toast.makeText(this, "ADDING!", Toast.LENGTH_LONG).show()

                // Storing into database
                FirebaseDatabase.getInstance().getReference()
                    .child("Summaries")
                    .child(summaryName).child(summaryDate).setValue(summarisedSummaryContent);

            }
        }
    }

    // Function to get the embeddings from OPENAI



}