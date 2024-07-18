package com.example.fibertel

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ReportarProblemaActivity : AppCompatActivity() {

    private lateinit var tituloProblema: EditText
    private lateinit var descripcionProblema: EditText
    private var tituloProblemaTexto: String = ""
    private var descripcionProblemaTexto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reportar_problema)

        val spinnerHint: TextView = findViewById(R.id.spinner_hint)
        val spinnerProblema: Spinner = findViewById(R.id.spinner_problema)

        // Crear un ArrayAdapter usando un array de strings y un layout por defecto para el spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.problema_array, // Array definido en recursos
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especificar el layout a usar cuando se despliegan las opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplicar el adaptador al spinner
            spinnerProblema.adapter = adapter
        }

        spinnerHint.setOnClickListener {
            spinnerHint.visibility = View.GONE
            spinnerProblema.visibility = View.VISIBLE
            spinnerProblema.performClick()
        }

        spinnerProblema.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != 0) {
                    spinnerHint.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Inicializar los EditTexts
        tituloProblema = findViewById(R.id.tituloProblema)
        descripcionProblema = findViewById(R.id.descripcionProblema)

        // Inicializar el botón de retroceso
        val btnRetroceder: ImageButton = findViewById(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish() // Cierra la actividad y vuelve al fragmento anterior
        }

        // Inicializar el botón de enviar
        val btnEnviar: Button = findViewById(R.id.botonEnviarReporte)
        btnEnviar.setOnClickListener {
            // Obtener el texto del EditText y almacenarlo en variables de instancia
            tituloProblemaTexto = tituloProblema.text.toString()
            descripcionProblemaTexto = descripcionProblema.text.toString()
            val categoriaProblema = spinnerProblema.selectedItem.toString()

            // Guardar el ticket en SharedPreferences
            val sharedPreferences = getSharedPreferences("Tickets", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val ticketsSet = sharedPreferences.getStringSet("ticketsSet", mutableSetOf())?.toMutableSet()
            val ticket = "$tituloProblemaTexto|$categoriaProblema|$descripcionProblemaTexto"
            ticketsSet?.add(ticket)
            editor.putStringSet("ticketsSet", ticketsSet)
            editor.apply()

            // Mostrar un mensaje de confirmación (puedes cambiar esto según sea necesario)
            Toast.makeText(this, "Problema reportado con éxito", Toast.LENGTH_SHORT).show()

            // Finalizar la actividad después de enviar los datos
            finish()
        }
    }
}
