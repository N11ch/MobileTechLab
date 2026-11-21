package com.example.karflex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.karflex.util.SessionManager

class OtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val userId = intent.getIntExtra("user_id", -1)
        val username = intent.getStringExtra("username") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val expectedOtp = intent.getStringExtra("otp_code") ?: ""

        val tvOtpInfo = findViewById<TextView>(R.id.tvOtpInfo)
        val etOtp = findViewById<EditText>(R.id.etOtp)
        val btnVerifyOtp = findViewById<Button>(R.id.btnVerifyOtp)

        tvOtpInfo.text = "Enter the 4-digit OTP code sent via SMS to $phone"

        btnVerifyOtp.setOnClickListener {
            val inputOtp = etOtp.text.toString().trim()

            if (inputOtp.isEmpty()) {
                Toast.makeText(this, "OTP code must be filled", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputOtp != expectedOtp) {
                Toast.makeText(this, "Invalid OTP code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SessionManager(this).saveUser(userId, username, phone)
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(homeIntent)
            finish()
        }
    }
}
