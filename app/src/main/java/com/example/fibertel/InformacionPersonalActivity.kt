package com.example.fibertel

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        // Inicializa los TextViews
        tvNombre = findViewById(R.id.tv_NombreInformacion)
        tvEmail = findViewById(R.id.tv_EmailInformacion)
        tvTelefono = findViewById(R.id.tv_TelefonoInformacion)
        tvDireccion = findViewById(R.id.tv_DireccionInformacion)
        tvIdentificacion = findViewById(R.id.tv_IdentificacionInformacion)

        // Recupera el usuario almacenado en UserManager
        val userManager = UserManager(this)
        val user = userManager.currentUser

        // Verifica si hay un usuario y actualiza los TextViews
        user?.let {
            tvNombre.text = it.name
            tvEmail.text = it.email
            tvTelefono.text = it.phone
            tvDireccion.text = it.address
            tvIdentificacion.text = it.nationalIdentificationNumber
        }

        // Configura el botón de retroceso
        val btnRetroceder = findViewById<ImageButton>(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish()  // Cierra la actividad y regresa a la anterior
        }
    }
}
