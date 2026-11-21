package com.example.karflex.model

data class Vehicle(
    val vehicleId: Int,
    val modelName: String,
    val type: String,
    val brand: String,
    val pricePerDay: Int,
    val transmission: String,
    val image: String,
    val description: String,
    val isRented: Boolean = false
)
