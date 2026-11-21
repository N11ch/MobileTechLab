package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.karflex.adapter.VehicleAdapter
import com.example.karflex.db.DatabaseHelper
import com.example.karflex.model.Vehicle

class BrowseFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: VehicleAdapter

    companion object {
        private const val KARFLEX_JSON_URL =
            "https://gist.githubusercontent.com/axelkrnwn/01c506ac60735f553af951e550634b59/raw/08489b2ff76e1356a6df38d09948716628024195/karflex.json"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        val rvVehicles = view.findViewById<RecyclerView>(R.id.rvVehicles)
        rvVehicles.layoutManager = LinearLayoutManager(requireContext())

        adapter = VehicleAdapter(emptyList()) { vehicle ->
            val intent = Intent(requireContext(), VehicleDetailActivity::class.java)
            intent.putExtra("vehicle_id", vehicle.vehicleId)
            startActivity(intent)
        }
        rvVehicles.adapter = adapter

        loadVehiclesFromDb()
        fetchVehiclesFromJson()
    }

    override fun onResume() {
        super.onResume()
        loadVehiclesFromDb()
    }

    private fun loadVehiclesFromDb() {
        adapter.updateData(db.getAllVehicles())
    }

    private fun fetchVehiclesFromJson() {
        val queue = Volley.newRequestQueue(requireContext())
        val request = JsonArrayRequest(
            Request.Method.GET,
            KARFLEX_JSON_URL,
            null,
            { response ->
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val vehicle = Vehicle(
                        vehicleId = obj.getInt("id"),
                        modelName = obj.getString("model_name"),
                        type = obj.getString("type"),
                        brand = obj.getString("brand"),
                        pricePerDay = obj.getInt("price_per_day"),
                        transmission = obj.getString("transmission"),
                        image = obj.getString("image_url"),
                        description = obj.getString("description")
                    )
                    db.upsertVehicle(vehicle)
                }
                if (isAdded) {
                    loadVehiclesFromDb()
                }
            },
            { error ->
                if (isAdded) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch vehicles: ${error.message ?: "Network error"}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        queue.add(request)
    }
}
