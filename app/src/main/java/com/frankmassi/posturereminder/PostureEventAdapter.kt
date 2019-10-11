package com.frankmassi.posturereminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frankmassi.posturereminder.database.PostureEvent

class PostureEventAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<PostureEventAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var events = emptyList<PostureEvent>() // Cached copy of words

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = events[position]
        holder.wordItemView.text = "${current.displayCreatedAt()}: ${current.wasGood}"
        val color = if (current.wasGood) {
            inflater.context.getColor(R.color.goodPosture)
        } else {
            inflater.context.getColor(R.color.badPosture)
        }
        holder.wordItemView.setBackgroundColor(color)
    }

    internal fun setEvents(events: List<PostureEvent>) {
        this.events = events
        notifyDataSetChanged()
    }

    override fun getItemCount() = events.size
}
