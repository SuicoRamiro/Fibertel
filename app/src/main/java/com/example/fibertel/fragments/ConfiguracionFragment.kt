package com.example.fibertel.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.fibertel.InformacionPersonalActivity
import com.example.fibertel.R
import com.google.zxing.integration.android.IntentIntegrator

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


        view.findViewById<LinearLayout>(R.id.opcion_Escaner).setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setPrompt("Escanear código QR")
            integrator.setBeepEnabled(false)
            integrator.initiateScan()
        }

        view.findViewById<LinearLayout>(R.id.opcion_EliminarCuenta).setOnClickListener {

        }

        return view
    }
    // Manejar el resultado del escaneo de QR
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // OBetner contenido Q
                val scannedResult = result.contents
                // Abrir InformacionPersonalActivity y pasar el URL como extra
                val intent = Intent(activity, InformacionPersonalActivity::class.java)
                intent.putExtra("link_mobile_login", scannedResult)
                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
