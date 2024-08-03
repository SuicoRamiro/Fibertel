// src/main/java/com/example/fibertel/NotificationAdapter.kt
package com.example.fibertel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.R
import com.example.fibertel.model.Ticket

class NotificationAdapter(
    private val notifications: List<Ticket>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val ticket = notifications[position]
        holder.bind(ticket)
    }

    override fun getItemCount(): Int = notifications.size

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_notification_title)
        private val detailsTextView: TextView = itemView.findViewById(R.id.tv_notification_details)
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_notification_date)

        fun bind(ticket: Ticket) {
            titleTextView.text = "Ticket finalizado"
            detailsTextView.text = "${ticket.title} ha sido finalizado"
            dateTextView.text = ticket.finalizedAt ?: "No date"
        }
    }
}
