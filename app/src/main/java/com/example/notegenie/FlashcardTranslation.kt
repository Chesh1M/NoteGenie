package com.example.notegenie

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Timer
import kotlin.concurrent.schedule

class FlashcardTranslation : AppCompatActivity() {
    // okhttp for API calls
    private val client = OkHttpClient()

    // Initializing Global Variables
    var CURRENT_LANGUAGE = "En"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_translation)

        // Initializing the view
        val currentLanguageTextView: TextView = findViewById(R.id.currentLanguageTextView)

        // Initializing the pop-up menu

        // Firstly initializing the widget
        val languagesMenuView: ImageView = findViewById(R.id.changeLanguageImageView)
        val summaryContentsView: TextView = findViewById(R.id.flashCardText)

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
                englishChineseTranslator.translate(summaryContentsView.text.toString())
                    .addOnSuccessListener { translatedText ->
                        summaryContentsView.text = translatedText.toString()
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
                englishTamilTranslator.translate(summaryContentsView.text.toString())
                    .addOnSuccessListener { translatedText ->
                        summaryContentsView.text = translatedText.toString()
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
        languagesMenuView.setOnClickListener{
            changeLanguagePopupMenu.show()
        }
    }

    // Display Question
    fun displayQn(view: View){
        // Initializing the view
        val summaryContentsTextView: TextView = findViewById(R.id.flashCardText)

        // Dummy summary text
        val summary = "Let f be a function, and c and L real numbers. Suppose that as x gets closer to c from either side (but never reaches c), the y-values f (x) get closer to the same number L. Then we will write limx→c f (x) = L, read “the limit as x approaches c of f (x) is L.” We may also shorten this to “as x → c then f (x) → L. Let f be a function and suppose f (a) exists. Then the limit lim h→0 f (a + h) − f (a) h , (1) if it exists, is called the derivative of f at a, and will be denoted by f 0 (a). The function is then said to be differentiable at x = a. Figure 1: The graph of a function f (x), two points on this function—(a, f (a)) (in black) and (a+h, f (a+h)) (in blue)—and the secant line (dashed) passing through those two points. (I explain what the red line is below.) Notice that the quantity (a +h)− a in the denominator of (2) is the change in the x-value of f from x = a to x = a + h; let’s call that change ∆x (read “change in x”), so that ∆x = (a + h) − a. That change in input produces a change in output ( y-values) of f (a + h) − f (a). Let’s call that change ∆y, so that ∆y = f (a + h) − f (a). Then (2) becomes f (a + h) − f (a) h = f (a + h) − f (a) (a + h) − a = ∆y ∆x . The quantity all the way on the right is the slope of the line connecting the points (a, f (a)) and (a + h, f (a + h)) in Figure 1 (the dashed line in the figure). That line is called the secant line. Returning to Definition 2, the limit in (1) then becomes lim ∆x→0 ∆y ∆x (since ∆x = (a + h) − a = h, so that h → 0 is equivalent to ∆x → 0). Thus, if the derivative exists, we have f 0 (a) = lim ∆x→0 ∆y ∆x , (3) which tells us that the derivative is the limit of the slopes of the secant lines through (a, f (a)) and (a +h, f (a +h)) as the right-endpoint used to calculate those slopes gets closer to the left-endpoint (i.e., ∆x → 0). The resulting line—the red line in Figure 1—is called the tangent line, so named because if you zoom in to the point (a, f (a)) in Figure 1, the line is tangent to the graph of f (x) at the point (a, f (a))."

        generateQuestion(summary) {response ->
            runOnUiThread {
                summaryContentsTextView.text = response
            }
        }
    } // End of Display Question

    // Generate Question
    fun generateQuestion(summary:String, callback:(String) ->  Unit){
        val apiKey="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"
        val url="https://api.openai.com/v1/engines/text-davinci-003/completions"
        // call gpt api to generate question based on summary passed in
        val requestBody="""
            {
            "prompt": "Please come up with a different test question based on the notes provided here: $summary",
            "max_tokens": 500,
            "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else {
                    Log.v("data", "empty")
                }
                val jsonObject=JSONObject(body)
                val jsonArray:JSONArray=jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)

                /* TODO:
                *   Return the textResult as a var instead (flashcardQn)
                *   so that the viewAswer function can use it to generate the answer */
            }
        })

    } // End of Generate Question

    // Reveal Answer
    fun viewAnswer(view: View){

        // Initializing the view
        val summaryContentsTextView: TextView = findViewById(R.id.flashCardText)
        val summaryContentCard: LinearLayout = findViewById(R.id.questionCard)


        // dummy flashcard question
        val flashcardQn = "Hi how are you?"

        // Changing text from qn to ans
        generateAnswer(flashcardQn) {response ->
            runOnUiThread {
                summaryContentsTextView.text = response
                summaryContentCard.setBackgroundColor(Color.parseColor("#006DEC"))
            }
        }
    } // End of Reveal Answer

    // Generate Answer
    fun generateAnswer(flashcardQn:String, callback:(String) ->  Unit){
        val apiKey="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"
        val url="https://api.openai.com/v1/engines/text-davinci-003/completions"
        // call gpt api to generate answer based on question passed in
        val requestBody="""
            {
            "prompt": "$flashcardQn",
            "max_tokens": 500,
            "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body=response.body?.string()
                if (body != null) {
                    Log.v("data",body)
                }
                else {
                    Log.v("data", "empty")
                }
                val jsonObject=JSONObject(body)
                val jsonArray:JSONArray=jsonObject.getJSONArray("choices")
                val textResult=jsonArray.getJSONObject(0).getString("text")
                callback(textResult)
            }
        })
    } // End of Generate Answer

}