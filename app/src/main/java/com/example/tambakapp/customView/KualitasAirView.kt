package com.example.tambakapp.customView

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.tambakapp.R

class KualitasAirView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        // Default style for the TextView
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    /**
     * Update the status and change the color based on the value
     * @param status The sensor status: "Rendah", "Normal", or "Tinggi"
     */
    fun updateStatusAir(status: String) {
        text = status
        when (status) {
            "Layak" -> setTextColor(ContextCompat.getColor(context, R.color.green))
            "Tidak Layak" -> setTextColor(ContextCompat.getColor(context, R.color.yellow))
            else -> setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
}
