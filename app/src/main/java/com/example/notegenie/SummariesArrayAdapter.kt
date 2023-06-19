package com.example.notegenie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SummariesArrayAdapter(private val context: Activity, private val arrayList: ArrayList<SummaryTopic>):
    ArrayAdapter<SummaryTopic>(context, R.layout.activity_summary_card, arrayList){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Inflating the view (IDK what this means)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.activity_summary_card, null)

        // Setting the data into the listview array

        // Initializing the views
        val summaryTopicView: TextView = view.findViewById(R.id.summaryFileName)
        val summaryLastEditedView: TextView = view.findViewById(R.id.modifiedDate)

        // Actually setting them based on positions
        summaryTopicView.text = arrayList[position].summaryTitle
        summaryLastEditedView.text = arrayList[position].summaryDate




        return view
    }

}