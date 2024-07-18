package com.example.fibertel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(
    private val tickets: MutableList<Ticket>,
    private val onResolveClick: (Ticket) -> Unit
) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }

    override fun getItemCount(): Int = tickets.size

    fun removeTicket(ticket: Ticket) {
        val position = tickets.indexOf(ticket)
        if (position != -1) {
            tickets.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getTickets(): List<Ticket> = tickets

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.ticketTitle)
        private val categoryTextView: TextView = itemView.findViewById(R.id.ticketCategory)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.ticketDescription)
        private val resolveButton: Button = itemView.findViewById(R.id.btnResuelto)

        fun bind(ticket: Ticket) {
            titleTextView.text = ticket.title
            categoryTextView.text = ticket.category
            descriptionTextView.text = ticket.description

            // Implementar la lógica de botón "Resuelto"
            resolveButton.setOnClickListener {
                onResolveClick(ticket)
            }
        }
    }
}
