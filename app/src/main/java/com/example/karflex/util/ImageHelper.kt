package com.example.karflex.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.karflex.R
import java.text.NumberFormat
import java.util.Locale

object ImageHelper {

    fun loadVehicleImage(imageView: ImageView, imageUrl: String, modelName: String) {
        val fallback = when {
            modelName.contains("Avanza", ignoreCase = true) -> R.drawable.avanza
            modelName.contains("Brio", ignoreCase = true) -> R.drawable.brio
            modelName.contains("Yamaha", ignoreCase = true) || modelName.contains("NMAX", ignoreCase = true) -> R.drawable.yamaha
            modelName.contains("Honda", ignoreCase = true) || modelName.contains("Vario", ignoreCase = true) -> R.drawable.honda
            else -> R.drawable.ic_karflex_logo
        }

        Glide.with(imageView.context)
            .load(imageUrl)
            .placeholder(fallback)
            .error(fallback)
            .into(imageView)
    }

    fun formatIdr(price: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
        return "Rp ${formatter.format(price)} / day"
    }
}
