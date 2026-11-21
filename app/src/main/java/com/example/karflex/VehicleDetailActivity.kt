package com.example.karflex

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.karflex.db.DatabaseHelper
import com.example.karflex.util.ImageHelper
import com.example.karflex.util.SessionManager

class VehicleDetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private var vehicleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        db = DatabaseHelper(this)
        session = SessionManager(this)
        vehicleId = intent.getIntExtra("vehicle_id", -1)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val vehicle = db.getVehicleById(vehicleId)
        if (vehicle == null) {
            Toast.makeText(this, "Vehicle not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val ivVehicle = findViewById<ImageView>(R.id.ivVehicle)
        val tvModelName = findViewById<TextView>(R.id.tvModelName)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val tvBrand = findViewById<TextView>(R.id.tvBrand)
        val tvType = findViewById<TextView>(R.id.tvType)
        val tvTransmission = findViewById<TextView>(R.id.tvTransmission)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val tvRentedNotice = findViewById<TextView>(R.id.tvRentedNotice)
        val layoutRentForm = findViewById<LinearLayout>(R.id.layoutRentForm)
        val etDuration = findViewById<EditText>(R.id.etDuration)
        val btnRent = findViewById<Button>(R.id.btnRent)

        ImageHelper.loadVehicleImage(ivVehicle, vehicle.image, vehicle.modelName)
        tvModelName.text = vehicle.modelName
        tvPrice.text = ImageHelper.formatIdr(vehicle.pricePerDay)
        tvBrand.text = "Brand: ${vehicle.brand}"
        tvType.text = "Type: ${vehicle.type}"
        tvTransmission.text = "Transmission: ${vehicle.transmission}"
        tvDescription.text = vehicle.description

        if (vehicle.isRented) {
            layoutRentForm.visibility = View.GONE
            tvRentedNotice.visibility = View.VISIBLE
        } else {
            layoutRentForm.visibility = View.VISIBLE
            tvRentedNotice.visibility = View.GONE
        }

        btnRent.setOnClickListener {
            val durationText = etDuration.text.toString().trim()

            if (durationText.isEmpty()) {
                Toast.makeText(this, "Duration must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val duration = durationText.toIntOrNull()
            if (duration == null || duration <= 0) {
                Toast.makeText(this, "Duration must be a number and more than 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = db.insertRental(vehicle.vehicleId, session.getUserId(), duration)
            if (result != -1L) {
                Toast.makeText(this, "Vehicle rented successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to rent vehicle", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
