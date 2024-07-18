package com.example.fibertel.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.fibertel.CorreoActivity
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
            val intent = Intent(activity, CorreoActivity::class.java)
            startActivity(intent)
        }

        return view
    }


}