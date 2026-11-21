package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.karflex.util.SessionManager

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = SessionManager(requireContext())

        val tvUsername = view.findViewById<TextView>(R.id.tvUsername)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val btnRentalHistory = view.findViewById<Button>(R.id.btnRentalHistory)
        val btnStoreLocation = view.findViewById<Button>(R.id.btnStoreLocation)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        tvUsername.text = session.getUsername()
        tvPhone.text = session.getPhone()

        btnRentalHistory.setOnClickListener {
            startActivity(Intent(requireContext(), RentalHistoryActivity::class.java))
        }

        btnStoreLocation.setOnClickListener {
            startActivity(Intent(requireContext(), LocationActivity::class.java))
        }

        btnLogout.setOnClickListener {
            session.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}
