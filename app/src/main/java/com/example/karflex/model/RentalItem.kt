package com.example.karflex.model

data class RentalItem(
    val rentalId: Int,
    val vehicleId: Int,
    val userId: Int,
    val duration: Int,
    val startDate: String,
    val status: String,
    val modelName: String,
    val brand: String,
    val image: String
)
