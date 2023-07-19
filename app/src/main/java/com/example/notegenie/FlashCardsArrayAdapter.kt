package com.example.notegenie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FlashCardsArrayAdapter(private val context: Activity, private val arrayList: ArrayList<FlashCardData>):
    ArrayAdapter<FlashCardData>(context, R.layout.activity_flash_cards_card, arrayList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Inflating the view (IDK what this means)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.activity_flash_cards_card, null)

        // Setting the data into the listview array

        // Initializing the views
        val flashCardTopicView: TextView = view.findViewById(R.id.flashCardFileName)

        // Actually setting them based on positions
        flashCardTopicView.text = arrayList[position].flashCardTitle



        return view
    }

}