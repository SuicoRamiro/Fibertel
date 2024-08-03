package com.example.fibertel.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.fibertel.activities.MisTicketsActivity
import com.example.fibertel.R
import com.example.fibertel.activities.ReportarProblemaActivity

class SoporteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_soporte, container, false)

        view.findViewById<LinearLayout>(R.id.opcion_ReportarProblema).setOnClickListener {
            val intent = Intent(activity, ReportarProblemaActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.opcion_MisTickets).setOnClickListener {
            val intent = Intent(activity, MisTicketsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.opcion_PorCorreo).setOnClickListener {
            enviarCorreo()
        }

        return view
    }

    private fun enviarCorreo() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("proveedores@fibertel.com.pe"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta de usuario")
        intent.putExtra(Intent.EXTRA_TEXT, "Hola, necesito ayuda con...")

        val gmailIntent = Intent.createChooser(intent, "Enviar correo con")
        gmailIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(
            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, arrayOf("proveedores@fibertel.com.pe"))
        ))

        startActivity(gmailIntent)
    }
}
