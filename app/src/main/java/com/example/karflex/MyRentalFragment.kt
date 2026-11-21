package com.example.karflex

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.karflex.adapter.MyRentalAdapter
import com.example.karflex.db.DatabaseHelper
import com.example.karflex.model.RentalItem
import com.example.karflex.util.SessionManager

class MyRentalFragment : Fragment() {

    private lateinit var db: DatabaseHelper
    private lateinit var session: SessionManager
    private lateinit var adapter: MyRentalAdapter
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_rental, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())
        session = SessionManager(requireContext())

        tvEmpty = view.findViewById(R.id.tvEmptyRentals)
        val rvMyRentals = view.findViewById<RecyclerView>(R.id.rvMyRentals)
        rvMyRentals.layoutManager = LinearLayoutManager(requireContext())

        adapter = MyRentalAdapter(
            rentals = emptyList(),
            onUpdateClick = { showUpdateDialog(it) },
            onCancelClick = { showCancelDialog(it) }
        )
        rvMyRentals.adapter = adapter

        loadActiveRentals()
    }

    override fun onResume() {
        super.onResume()
        loadActiveRentals()
    }

    private fun loadActiveRentals() {
        val list = db.getActiveRentalsByUser(session.getUserId())
        adapter.updateData(list)
        tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showUpdateDialog(item: RentalItem) {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Enter new duration (days)"
            setText(item.duration.toString())
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Update Rental Duration")
            .setMessage("Update duration for ${item.modelName}")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val text = input.text.toString().trim()
                val newDuration = text.toIntOrNull()

                if (newDuration == null || newDuration <= 0) {
                    Toast.makeText(
                        requireContext(),
                        "Duration must be a number and more than 0",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                db.updateRentalDuration(item.rentalId, newDuration)
                Toast.makeText(requireContext(), "Rental duration updated", Toast.LENGTH_SHORT).show()
                loadActiveRentals()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCancelDialog(item: RentalItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Rental")
            .setMessage("Are you sure you want to cancel your rental for ${item.modelName}?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                db.deleteRental(item.rentalId)
                Toast.makeText(requireContext(), "Rental cancelled", Toast.LENGTH_SHORT).show()
                loadActiveRentals()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
