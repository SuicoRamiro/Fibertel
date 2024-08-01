import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.R
import com.example.fibertel.infoFactura
import com.example.fibertel.model.Factura

class FacturaAdapter(private val facturas: List<Factura>) : RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_factura, parent, false)
        return FacturaViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
        val factura = facturas[position]
        holder.bind(factura)
    }

    override fun getItemCount(): Int {
        return facturas.size
    }

    class FacturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAmountBalance: TextView = itemView.findViewById(R.id.tvAmountBalance)
        private val tvVencimiento: TextView = itemView.findViewById(R.id.tvVencimiento)
        private val tvNumeroFactura: TextView = itemView.findViewById(R.id.tvNumeroFactura)

        fun bind(factura: Factura) {
            tvNumeroFactura.text = "Factura N° ${factura.invoice_number}"
            tvVencimiento.text = "Vencimiento: ${factura.first_due_date}"

            val balance = "$${factura.balance}"
            val amount = "$${factura.amount}"
            val combinedText = "$balance/$amount"

            val spannable = SpannableString(combinedText)

            val balanceColor: Int = when (factura.state) {
                "paid" -> Color.parseColor("#2BC9B0")
                "pending" -> Color.RED
                else -> Color.BLACK
            }

            spannable.setSpan(
                ForegroundColorSpan(balanceColor),
                0,
                balance.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            tvAmountBalance.text = spannable

            val borderDrawable: Int = when (factura.state) {
                "paid" -> R.drawable.rounded_corners_paid
                "pending" -> R.drawable.rounded_corners_pending
                else -> R.drawable.rounded_corners
            }

            itemView.background = itemView.context.getDrawable(borderDrawable)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, infoFactura::class.java).apply {
                    putExtra("balance", factura.balance)
                    putExtra("issued_at", factura.issued_at)
                    putExtra("first_due_date", factura.first_due_date)
                    putExtra("second_due_date", factura.second_due_date)
                    putExtra("invoice_number", factura.invoice_number)
                    putExtra("id", factura.id)
                }
                context.startActivity(intent)
            }
        }
    }

}
