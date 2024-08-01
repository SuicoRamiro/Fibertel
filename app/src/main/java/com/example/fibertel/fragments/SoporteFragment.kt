package com.example.fibertel.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.example.fibertel.MisTicketsActivity
import com.example.fibertel.R
import com.example.fibertel.ReportarProblemaActivity

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
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // Define el tipo de mensaje para manejar solo aplicaciones de correo
            putExtra(Intent.EXTRA_EMAIL, arrayOf("proveedores@fibertel.com.pe"))
            putExtra(Intent.EXTRA_SUBJECT, "Consulta de usuario")
            putExtra(Intent.EXTRA_TEXT, "Hola, necesito ayuda con...")
        }

        val chooser = Intent.createChooser(intent, "Enviar correo con")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(activity, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show()
        }
    }
}
