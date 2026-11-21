package com.example.karflex.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.karflex.model.RentalItem
import com.example.karflex.model.User
import com.example.karflex.model.Vehicle
import java.time.LocalDate

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "karflex.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE Users (
                UserID INTEGER PRIMARY KEY AUTOINCREMENT,
                Username TEXT UNIQUE,
                Password TEXT,
                PhoneNumber TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE Vehicle (
                VehicleID INTEGER PRIMARY KEY,
                ModelName TEXT,
                Type TEXT,
                Brand TEXT,
                PricePerDay INTEGER,
                Transmission TEXT,
                Image TEXT,
                Description TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE Rentals (
                RentalID INTEGER PRIMARY KEY AUTOINCREMENT,
                VehicleID INTEGER,
                UserID INTEGER,
                Duration INTEGER,
                StartDate TEXT,
                Status TEXT,
                FOREIGN KEY(VehicleID) REFERENCES Vehicle(VehicleID),
                FOREIGN KEY(UserID) REFERENCES Users(UserID)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Rentals")
        db.execSQL("DROP TABLE IF EXISTS Vehicle")
        db.execSQL("DROP TABLE IF EXISTS Users")
        onCreate(db)
    }

    // --- Users ---

    fun isUsernameTaken(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT UserID FROM Users WHERE Username = ?", arrayOf(username))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun registerUser(username: String, password: String, phone: String): Long {
        val values = ContentValues().apply {
            put("Username", username)
            put("Password", password)
            put("PhoneNumber", phone)
        }
        return writableDatabase.insert("Users", null, values)
    }

    fun authenticateUser(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT UserID, Username, Password, PhoneNumber FROM Users WHERE Username = ? AND Password = ?",
            arrayOf(username, password)
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                userId = cursor.getInt(0),
                username = cursor.getString(1),
                password = cursor.getString(2),
                phoneNumber = cursor.getString(3)
            )
        }
        cursor.close()
        return user
    }

    // --- Vehicles ---

    fun upsertVehicle(vehicle: Vehicle) {
        val values = ContentValues().apply {
            put("VehicleID", vehicle.vehicleId)
            put("ModelName", vehicle.modelName)
            put("Type", vehicle.type)
            put("Brand", vehicle.brand)
            put("PricePerDay", vehicle.pricePerDay)
            put("Transmission", vehicle.transmission)
            put("Image", vehicle.image)
            put("Description", vehicle.description)
        }
        writableDatabase.insertWithOnConflict("Vehicle", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun isVehicleRented(vehicleId: Int): Boolean {
        updateExpiredRentals()
        val cursor = readableDatabase.rawQuery(
            "SELECT RentalID FROM Rentals WHERE VehicleID = ? AND Status = 'Active'",
            arrayOf(vehicleId.toString())
        )
        val rented = cursor.moveToFirst()
        cursor.close()
        return rented
    }

    fun getAllVehicles(): List<Vehicle> {
        updateExpiredRentals()
        val list = mutableListOf<Vehicle>()
        val query = """
            SELECT v.VehicleID, v.ModelName, v.Type, v.Brand, v.PricePerDay, v.Transmission, v.Image, v.Description,
                   EXISTS(SELECT 1 FROM Rentals r WHERE r.VehicleID = v.VehicleID AND r.Status = 'Active') AS IsRented
            FROM Vehicle v
            ORDER BY v.VehicleID ASC
        """.trimIndent()

        val cursor = readableDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {
            list.add(
                Vehicle(
                    vehicleId = cursor.getInt(0),
                    modelName = cursor.getString(1),
                    type = cursor.getString(2),
                    brand = cursor.getString(3),
                    pricePerDay = cursor.getInt(4),
                    transmission = cursor.getString(5),
                    image = cursor.getString(6),
                    description = cursor.getString(7),
                    isRented = cursor.getInt(8) == 1
                )
            )
        }
        cursor.close()
        return list
    }

    fun getVehicleById(vehicleId: Int): Vehicle? {
        updateExpiredRentals()
        val cursor = readableDatabase.rawQuery(
            """
            SELECT v.VehicleID, v.ModelName, v.Type, v.Brand, v.PricePerDay, v.Transmission, v.Image, v.Description,
                   EXISTS(SELECT 1 FROM Rentals r WHERE r.VehicleID = v.VehicleID AND r.Status = 'Active') AS IsRented
            FROM Vehicle v
            WHERE v.VehicleID = ?
            """.trimIndent(),
            arrayOf(vehicleId.toString())
        )
        var vehicle: Vehicle? = null
        if (cursor.moveToFirst()) {
            vehicle = Vehicle(
                vehicleId = cursor.getInt(0),
                modelName = cursor.getString(1),
                type = cursor.getString(2),
                brand = cursor.getString(3),
                pricePerDay = cursor.getInt(4),
                transmission = cursor.getString(5),
                image = cursor.getString(6),
                description = cursor.getString(7),
                isRented = cursor.getInt(8) == 1
            )
        }
        cursor.close()
        return vehicle
    }

    // --- Rentals ---

    fun updateExpiredRentals() {
        val db = writableDatabase
        val today = LocalDate.now()
        val cursor = db.rawQuery(
            "SELECT RentalID, StartDate, Duration FROM Rentals WHERE Status = 'Active'",
            null
        )
        val expiredIds = mutableListOf<Int>()
        while (cursor.moveToNext()) {
            val rentalId = cursor.getInt(0)
            val startDateStr = cursor.getString(1)
            val duration = cursor.getInt(2)
            try {
                val endDate = LocalDate.parse(startDateStr).plusDays(duration.toLong())
                if (today.isAfter(endDate)) {
                    expiredIds.add(rentalId)
                }
            } catch (_: Exception) {
            }
        }
        cursor.close()

        for (id in expiredIds) {
            val values = ContentValues().apply { put("Status", "Completed") }
            db.update("Rentals", values, "RentalID = ?", arrayOf(id.toString()))
        }
    }

    fun insertRental(vehicleId: Int, userId: Int, duration: Int): Long {
        val values = ContentValues().apply {
            put("VehicleID", vehicleId)
            put("UserID", userId)
            put("Duration", duration)
            put("StartDate", LocalDate.now().toString())
            put("Status", "Active")
        }
        return writableDatabase.insert("Rentals", null, values)
    }

    fun getActiveRentalsByUser(userId: Int): List<RentalItem> {
        updateExpiredRentals()
        val list = mutableListOf<RentalItem>()
        val cursor = readableDatabase.rawQuery(
            """
            SELECT r.RentalID, r.VehicleID, r.UserID, r.Duration, r.StartDate, r.Status,
                   v.ModelName, v.Brand, v.Image
            FROM Rentals r
            INNER JOIN Vehicle v ON r.VehicleID = v.VehicleID
            WHERE r.UserID = ? AND r.Status = 'Active'
            ORDER BY r.RentalID DESC
            """.trimIndent(),
            arrayOf(userId.toString())
        )
        while (cursor.moveToNext()) {
            list.add(
                RentalItem(
                    rentalId = cursor.getInt(0),
                    vehicleId = cursor.getInt(1),
                    userId = cursor.getInt(2),
                    duration = cursor.getInt(3),
                    startDate = cursor.getString(4),
                    status = cursor.getString(5),
                    modelName = cursor.getString(6),
                    brand = cursor.getString(7),
                    image = cursor.getString(8)
                )
            )
        }
        cursor.close()
        return list
    }

    fun getRentalHistoryByUser(userId: Int, queryFilter: String = ""): List<RentalItem> {
        updateExpiredRentals()
        val list = mutableListOf<RentalItem>()
        val cursor = readableDatabase.rawQuery(
            """
            SELECT r.RentalID, r.VehicleID, r.UserID, r.Duration, r.StartDate, r.Status,
                   v.ModelName, v.Brand, v.Image
            FROM Rentals r
            INNER JOIN Vehicle v ON r.VehicleID = v.VehicleID
            WHERE r.UserID = ? AND v.ModelName LIKE ?
            ORDER BY r.RentalID DESC
            """.trimIndent(),
            arrayOf(userId.toString(), "%$queryFilter%")
        )
        while (cursor.moveToNext()) {
            list.add(
                RentalItem(
                    rentalId = cursor.getInt(0),
                    vehicleId = cursor.getInt(1),
                    userId = cursor.getInt(2),
                    duration = cursor.getInt(3),
                    startDate = cursor.getString(4),
                    status = cursor.getString(5),
                    modelName = cursor.getString(6),
                    brand = cursor.getString(7),
                    image = cursor.getString(8)
                )
            )
        }
        cursor.close()
        return list
    }

    fun updateRentalDuration(rentalId: Int, newDuration: Int): Int {
        val values = ContentValues().apply {
            put("Duration", newDuration)
        }
        return writableDatabase.update("Rentals", values, "RentalID = ?", arrayOf(rentalId.toString()))
    }

    fun deleteRental(rentalId: Int): Int {
        return writableDatabase.delete("Rentals", "RentalID = ?", arrayOf(rentalId.toString()))
    }
}
