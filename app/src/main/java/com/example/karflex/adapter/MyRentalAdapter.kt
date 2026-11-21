package com.example.karflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.R
import com.example.karflex.model.RentalItem
import com.example.karflex.util.ImageHelper

class MyRentalAdapter(
    private var rentals: List<RentalItem>,
    private val onUpdateClick: (RentalItem) -> Unit,
    private val onCancelClick: (RentalItem) -> Unit
) : RecyclerView.Adapter<MyRentalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivVehicle: ImageView = view.findViewById(R.id.ivVehicle)
        val tvModelName: TextView = view.findViewById(R.id.tvModelName)
        val tvBrand: TextView = view.findViewById(R.id.tvBrand)
        val tvStartDate: TextView = view.findViewById(R.id.tvStartDate)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val btnUpdate: Button = view.findViewById(R.id.btnUpdate)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_rental, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = rentals[position]
        holder.tvModelName.text = item.modelName
        holder.tvBrand.text = item.brand
        holder.tvStartDate.text = "Start Date: ${item.startDate}"
        holder.tvDuration.text = "Duration: ${item.duration} day(s)"

        ImageHelper.loadVehicleImage(holder.ivVehicle, item.image, item.modelName)

        holder.btnUpdate.setOnClickListener { onUpdateClick(item) }
        holder.btnCancel.setOnClickListener { onCancelClick(item) }
    }

    override fun getItemCount(): Int = rentals.size

    fun updateData(newList: List<RentalItem>) {
        rentals = newList
        notifyDataSetChanged()
    }
}
