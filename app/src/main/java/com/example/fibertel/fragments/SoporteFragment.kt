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
        // Inflate the layout for this fragment
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
        // Crear un Intent para enviar un correo
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // Define el tipo de mensaje para manejar solo aplicaciones de correo
            putExtra(Intent.EXTRA_EMAIL, arrayOf("proveedores@fibertel.com.pe"))
            putExtra(Intent.EXTRA_SUBJECT, "Consulta de usuario")
            putExtra(Intent.EXTRA_TEXT, "Hola, necesito ayuda con...")
        }

        // Crear un selector para elegir entre las aplicaciones de correo disponibles
        val chooser = Intent.createChooser(intent, "Enviar correo con")

        // Verificar que hay aplicaciones que pueden manejar el Intent
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(chooser)
        } else {
            // Manejo del caso en el que no hay aplicaciones de correo disponibles
            // Mostrar un mensaje de error o una alerta al usuario
            Toast.makeText(activity, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show()
        }
    }
}
