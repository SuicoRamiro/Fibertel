package com.example.fibertel.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fibertel.R
import com.example.fibertel.model.UserManager

class InformacionPersonalActivity : AppCompatActivity() {
    private lateinit var tvNombre: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var tvIdentificacion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion_personal)

        tvNombre = findViewById(R.id.tv_NombreInformacion)
        tvEmail = findViewById(R.id.tv_EmailInformacion)
        tvTelefono = findViewById(R.id.tv_TelefonoInformacion)
        tvDireccion = findViewById(R.id.tv_DireccionInformacion)
        tvIdentificacion = findViewById(R.id.tv_IdentificacionInformacion)

        val userManager = UserManager(this)
        val user = userManager.currentUser

        user?.let {
            tvNombre.text = it.name
            tvEmail.text = it.email
            tvTelefono.text = it.phone
            tvDireccion.text = it.address
            tvIdentificacion.text = it.nationalIdentificationNumber
        }

        val btnRetroceder = findViewById<ImageButton>(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish()
        }
    }
}
