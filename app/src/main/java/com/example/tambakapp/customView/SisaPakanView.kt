package com.example.tambakapp.customView

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.example.tambakapp.R

class SisaPakanView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        // Default style for the TextView
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
    fun updateSisaPakan(value: Float) {
        text = "${String.format("%.1f%%", value)}"
        when {
            value >= 60.0 -> setTextColor(ContextCompat.getColor(context, R.color.green))
            value >= 35.0 -> setTextColor(ContextCompat.getColor(context, R.color.yellow))
            else -> setTextColor(ContextCompat.getColor(context, R.color.red))
        }
    }
}
