package com.example.karflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.model.Vehicle
import com.example.karflex.util.ImageHelper

class VehicleAdapter(
    private var vehicles: List<Vehicle>,
    private val onItemClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardVehicle)
        val ivVehicle: ImageView = view.findViewById(R.id.ivVehicle)
        val tvModelName: TextView = view.findViewById(R.id.tvModelName)
        val tvBrand: TextView = view.findViewById(R.id.tvBrand)
        val tvTransmission: TextView = view.findViewById(R.id.tvTransmission)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = vehicles[position]
        holder.tvModelName.text = vehicle.modelName
        holder.tvBrand.text = "Brand: ${vehicle.brand}"
        holder.tvTransmission.text = "Transmission: ${vehicle.transmission}"
        holder.tvPrice.text = ImageHelper.formatIdr(vehicle.pricePerDay)

        ImageHelper.loadVehicleImage(holder.ivVehicle, vehicle.image, vehicle.modelName)

        val bgColor = if (vehicle.isRented) R.color.rented_red else R.color.white
        holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, bgColor))

        holder.itemView.setOnClickListener { onItemClick(vehicle) }
    }

    override fun getItemCount(): Int = vehicles.size

    fun updateData(newList: List<Vehicle>) {
        vehicles = newList
        notifyDataSetChanged()
    }
}
