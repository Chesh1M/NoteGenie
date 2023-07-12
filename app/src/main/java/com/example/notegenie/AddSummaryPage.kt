package com.example.notegenie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class AddSummaryPage : AppCompatActivity() {

    // Initializing the global variables
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_summary_page)

        // Initializing the layouts
        val summaryTextEditText: TextView = findViewById(R.id.summaryContentsView)
        val dateTextView: TextView = findViewById(R.id.modifiedDate)

        // Initializing the content from the Home page
        val unSummarisedContent = intent.getStringExtra("Summary Content").toString()

        // Getting the current date
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())

        // Setting the text of the Date TextView to the date
        dateTextView.text = "Date: $currentDate"

        val cleanedTextSummary = cleanTextForJSON(unSummarisedContent)

        Log.i("This is the unsummarised content", cleanedTextSummary)

        callChatGPT("Can you summarise the following content?: $cleanedTextSummary and please explain each point in more detail?"){response ->
            runOnUiThread {
                Log.i("Response from GPT", response.toString())
            }
        }
    }

    // FUnction to call ChatGPT
    fun callChatGPT(question: String, param: (Any) -> Unit){

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

                // Initializing the text view
                val displaySummaryContentTextView: TextView = findViewById(R.id.summaryContentsView)

                Log.i("Answer from ChatGPT", answerFromChatGPT)

                // Posting the text onto the Text View
                val handler = Handler(Looper.getMainLooper())
                handler.post {
//                     Setting the text
                    displaySummaryContentTextView.text = answerFromChatGPT
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

        // Returning the final text
        return inputTextString


    }
}