package com.example.fibertel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fibertel.R
import com.example.fibertel.model.Plan

class PlanAdapter(private var plans: MutableList<Plan>) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    inner class PlanViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val planName: TextView = view.findViewById(R.id.planName)
        val planDetailsDown: TextView = view.findViewById(R.id.planDetailsDown)
        val planDetailsUp: TextView = view.findViewById(R.id.planDetailsUp)
        val planPrice: TextView = view.findViewById(R.id.planPrice)
        val icDescarga: ImageView = view.findViewById(R.id.icDescarga)
        val icSubida: ImageView = view.findViewById(R.id.icSubida)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.planName.text = plan.name
        holder.planDetailsDown.text = "Descarga: ${plan.ceil_down_kbps / 1000} Mbps"
        holder.planDetailsUp.text = "Subida: ${plan.ceil_up_kbps / 1000} Mbps"
        holder.planPrice.text = "S/.${plan.price}"
    }

    override fun getItemCount(): Int = plans.size

    fun updatePlans(newPlans: List<Plan>) {
        plans.clear()
        plans.addAll(newPlans)
        notifyDataSetChanged()
    }
}
