package com.example.fibertel.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.fibertel.R

class InicioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        val btnVerMas = view.findViewById<ImageButton>(R.id.icon_info_balance)

        btnVerMas.setOnClickListener {
            mostrarDialogoBalanceCC()
        }

        return view
    }

    private fun mostrarDialogoBalanceCC() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.info_balance_cc)

        val btnCerrar = dialog.findViewById<Button>(R.id.btnCerrar)
        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
