package com.example.fibertel

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MisTicketsActivity : AppCompatActivity() {
    private lateinit var ticketsAdapter: TicketAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mis_tickets)

        // Inicializar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTickets)
        recyclerView.layoutManager = LinearLayoutManager(this)
        ticketsAdapter = TicketAdapter(loadTickets(), ::onTicketResolved)
        recyclerView.adapter = ticketsAdapter

        // Inicializar el botón de retroceso
        val btnRetroceder: ImageButton = findViewById(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish() // Cierra la actividad y vuelve al fragmento anterior
        }
    }

    private fun loadTickets(): MutableList<Ticket> {
        val sharedPreferences = getSharedPreferences("Tickets", Context.MODE_PRIVATE)
        val ticketsSet = sharedPreferences.getStringSet("ticketsSet", setOf()) ?: emptySet()
        return ticketsSet.map { ticketString ->
            val parts = ticketString.split("|")
            Ticket(parts[0], parts[1], parts[2])
        }.toMutableList()
    }

    private fun saveTickets(tickets: List<Ticket>) {
        val sharedPreferences = getSharedPreferences("Tickets", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val ticketsSet = tickets.map { "${it.title}|${it.category}|${it.description}" }.toSet()
        editor.putStringSet("ticketsSet", ticketsSet)
        editor.apply()
    }

    private fun onTicketResolved(ticket: Ticket) {
        // Eliminar el ticket
        ticketsAdapter.removeTicket(ticket)
        saveTickets(ticketsAdapter.getTickets())

        // Mostrar mensaje de éxito
        Toast.makeText(this, "Ticket resuelto y eliminado", Toast.LENGTH_SHORT).show()
    }
}

