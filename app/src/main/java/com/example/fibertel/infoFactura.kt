package com.example.fibertel

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class infoFactura : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_factura)

        val tvFacturaBalance = findViewById<TextView>(R.id.tvFacturaBalance)
        val tvFacturaNumber = findViewById<TextView>(R.id.tvFacturaNumber)
        val tvFechaEmitida = findViewById<TextView>(R.id.tvFechaEmitida)
        val tvFechaVencimiento1 = findViewById<TextView>(R.id.tvFechaVencimiento1)
        val tvFechaVencimiento2 = findViewById<TextView>(R.id.tvFechaVencimiento2)

        // Recupera los datos del Intent
        val balance = intent.getStringExtra("balance") ?: "Monto no disponible"
        val issuedAt = intent.getStringExtra("issued_at") ?: "Fecha no disponible"
        val firstDueDate = intent.getStringExtra("first_due_date") ?: "Fecha no disponible"
        val secondDueDate = intent.getStringExtra("second_due_date") ?: "Fecha no disponible"
        val invoiceNumber = intent.getStringExtra("invoice_number") ?: "Número no disponible"

        // Define los formatos de entrada y salida
        val inputFormatDateTime = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" // Para fechas con tiempo
        val inputFormatDate = "yyyy-MM-dd" // Para fechas sin tiempo
        val outputFormat = "dd/MM/yyyy" // Formato deseado

        // Formatea las fechas
        val formattedIssuedAt = if (issuedAt.contains("T")) {
            formatDate(issuedAt, inputFormatDateTime, outputFormat)
        } else {
            formatDate(issuedAt, inputFormatDate, outputFormat)
        }

        val formattedFirstDueDate = formatDate(firstDueDate, inputFormatDate, outputFormat)
        val formattedSecondDueDate = formatDate(secondDueDate, inputFormatDate, outputFormat)

        tvFacturaBalance.text = "Monto a pagar: $balance"
        tvFacturaNumber.text = "Factura # $invoiceNumber"
        tvFechaEmitida.text = "Emitida el : $formattedIssuedAt"
        tvFechaVencimiento1.text = "Primera Fecha de Vencimiento: $formattedFirstDueDate"
        tvFechaVencimiento2.text = "Segunda Fecha de Vencimiento: $formattedSecondDueDate"

        // Configura el botón de retroceso
        val btnRetroceder = findViewById<ImageButton>(R.id.btn_retroceder)
        btnRetroceder.setOnClickListener {
            finish()  // Cierra la actividad y regresa a la anterior
        }
    }

    private fun formatDate(inputDate: String, inputFormat: String, outputFormat: String): String {
        return try {
            val date = SimpleDateFormat(inputFormat, Locale.getDefault()).parse(inputDate)
            val outputFormatter = SimpleDateFormat(outputFormat, Locale.getDefault())
            date?.let { outputFormatter.format(it) } ?: "Fecha no válida"
        } catch (e: ParseException) {
            "Fecha no válida"
        }
    }

}
