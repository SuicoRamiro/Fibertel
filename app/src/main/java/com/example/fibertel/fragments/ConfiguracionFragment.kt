package com.example.fibertel.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.fibertel.activities.InformacionPersonalActivity
import com.example.fibertel.activities.LoginActivity
import com.example.fibertel.R
import com.example.fibertel.activities.ServiciosDisponibles

class ConfiguracionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_configuracion, container, false)

        // Configurar onClickListeners para cada opción
        view.findViewById<LinearLayout>(R.id.opcion_InformacionPersonal).setOnClickListener {
            val intent = Intent(activity, InformacionPersonalActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.opcion_ServiciosDisponibles).setOnClickListener {
            val intent = Intent(activity, ServiciosDisponibles::class.java)
            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.opcion_EliminarCuenta).setOnClickListener {
            showLogoutConfirmationDialog()
        }

        return view
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Está seguro que quiere cerrar sesión?")

        builder.setPositiveButton("Aceptar") { dialog, _ ->
            logout()
            dialog.dismiss()
        }

        builder.setNegativeButton("Rechazar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun logout() {
        val sharedPreferences = requireActivity().getSharedPreferences("FibertelPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
