package com.example.tambakapp

class FuzzyHelper {

    fun evaluatePH(ph: Float): String {
        return when {
            ph < 6.5 -> "Rendah"
            ph <= 8.5 -> "Normal"
            else -> "Tinggi"
        }
    }

    fun evaluateSuhuAir(suhuAir: Int): String {
        return when {
            suhuAir < 25 -> "Rendah"
            suhuAir <= 30 -> "Normal"
            else -> "Tinggi"
        }
    }

    fun evaluateTurbidity(turbidity: Int): String {
        return when {
            turbidity < 15 -> "Rendah"
            turbidity <= 30 -> "Normal"
            else -> "Tinggi"
        }
    }

    fun evaluateKualitasAir(phStatus: String, suhuAirStatus: String, turbidityStatus: String): String {
        return when {
            phStatus == "Rendah" && suhuAirStatus == "Rendah" && turbidityStatus == "Rendah" -> "Layak"
            phStatus == "Rendah" && suhuAirStatus == "Rendah" && turbidityStatus == "Normal" -> "Layak"
            phStatus == "Rendah" && suhuAirStatus == "Rendah" && turbidityStatus == "Tinggi" -> "Tidak Layak"
            phStatus == "Rendah" && suhuAirStatus == "Normal" && turbidityStatus == "Rendah" -> "Layak"
            phStatus == "Rendah" && suhuAirStatus == "Normal" && turbidityStatus == "Normal" -> "Layak"
            phStatus == "Rendah" && suhuAirStatus == "Normal" && turbidityStatus == "Tinggi" -> "Tidak Layak"
            phStatus == "Rendah" && suhuAirStatus == "Tinggi" -> "Tidak Layak"
            phStatus == "Normal" && suhuAirStatus == "Rendah" && turbidityStatus != "Tinggi" -> "Layak"
            phStatus == "Normal" && suhuAirStatus == "Rendah" && turbidityStatus == "Tinggi" -> "Tidak Layak"
            phStatus == "Normal" && suhuAirStatus == "Normal" && turbidityStatus != "Tinggi" -> "Layak"
            phStatus == "Normal" && suhuAirStatus == "Tinggi" -> "Tidak Layak"

            phStatus == "Tinggi" -> "Tidak Layak"
            else -> "Tidak Layak"
        }
    }
}
