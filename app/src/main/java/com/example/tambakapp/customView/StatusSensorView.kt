package com.example.tambakapp.customView

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.tambakapp.R

class StatusSensorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        // Default style for the TextView
        setPadding(16, 8, 16, 8)
        setTextColor(ContextCompat.getColor(context, R.color.black))
        textSize = 14f
    }

    /**
     * Update the status and change the color based on the value
     * @param status The sensor status: "Rendah", "Normal", or "Tinggi"
     */
    fun updateStatus(status: String) {
        text = status
        when (status) {
            "Rendah" -> setTextColor(ContextCompat.getColor(context, R.color.yellow))
            "Normal" -> setTextColor(ContextCompat.getColor(context, R.color.green))
            "Tinggi" -> setTextColor(ContextCompat.getColor(context, R.color.red))
            else -> setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }
}
