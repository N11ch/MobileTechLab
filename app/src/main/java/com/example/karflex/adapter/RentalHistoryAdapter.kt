package com.example.karflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.model.RentalItem
import com.example.karflex.util.ImageHelper

class RentalHistoryAdapter(
    private var rentals: List<RentalItem>
) : RecyclerView.Adapter<RentalHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivVehicle: ImageView = view.findViewById(R.id.ivVehicle)
        val tvModelName: TextView = view.findViewById(R.id.tvModelName)
        val tvBrand: TextView = view.findViewById(R.id.tvBrand)
        val tvStartDate: TextView = view.findViewById(R.id.tvStartDate)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rental_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = rentals[position]
        holder.tvModelName.text = item.modelName
        holder.tvBrand.text = item.brand
        holder.tvStartDate.text = "Start Date: ${item.startDate}"
        holder.tvDuration.text = "Duration: ${item.duration} day(s)"
        holder.tvStatus.text = item.status

        val statusColor = if (item.status.equals("Active", ignoreCase = true)) {
            R.color.status_active
        } else {
            R.color.status_completed
        }
        holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, statusColor))

        ImageHelper.loadVehicleImage(holder.ivVehicle, item.image, item.modelName)
    }

    override fun getItemCount(): Int = rentals.size

    fun updateData(newList: List<RentalItem>) {
        rentals = newList
        notifyDataSetChanged()
    }
}
