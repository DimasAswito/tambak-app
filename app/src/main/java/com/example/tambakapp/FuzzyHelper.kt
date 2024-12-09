class FuzzyHelper {


    private fun membership(value: Float, start: Float, peak: Float, end: Float): Float {
        return when {
            value < start || value > end -> 0.0f
            value in start..peak -> (value - start) / (peak - start)
            value in peak..end -> (end - value) / (end - peak)
            else -> 0.0f
        }
    }

//  Fuzzifikasi
    fun pHRendah(x: Float): Float = membership(x, 0f, 6.5f, 6.5f)
    fun pHNormal(x: Float): Float = membership(x, 6.5f, 8.5f, 8.5f)
    fun pHTinggi(x: Float): Float = membership(x, 8.5f, 10f, 14f)

    fun suhuRendah(x: Float): Float = membership(x, 15f, 25f, 25f)
    fun suhuNormal(x: Float): Float = membership(x, 25f, 30f, 30f)
    fun suhuTinggi(x: Float): Float = membership(x, 30f, 40f, 40f)

    fun turbidityRendah(x: Float): Float = membership(x, 0f, 15f, 15f)
    fun turbidityNormal(x: Float): Float = membership(x, 15f, 30f, 30f)
    fun turbidityTinggi(x: Float): Float = membership(x, 30f, 50f, 50f)


    //pembentukan rule
    fun categorizepH(phValue: Float): String {
        val rendah = pHRendah(phValue)
        val normal = pHNormal(phValue)
        val tinggi = pHTinggi(phValue)

        return when {
            rendah > normal && rendah > tinggi -> "Rendah"
            normal > rendah && normal > tinggi -> "Normal"
            tinggi > rendah && tinggi > normal -> "Tinggi"
            else -> "Tidak Diketahui"
        }
    }

    fun categorizeSuhu(suhuValue: Float): String {
        val rendah = suhuRendah(suhuValue)
        val normal = suhuNormal(suhuValue)
        val tinggi = suhuTinggi(suhuValue)

        return when {
            rendah > normal && rendah > tinggi -> "Rendah"
            normal > rendah && normal > tinggi -> "Normal"
            tinggi > rendah && tinggi > normal -> "Tinggi"
            else -> "Tidak Diketahui"
        }
    }

    fun categorizeTurbidity(turbidityValue: Float): String {
        val rendah = turbidityRendah(turbidityValue)
        val normal = turbidityNormal(turbidityValue)
        val tinggi = turbidityTinggi(turbidityValue)

        return when {
            rendah > normal && rendah > tinggi -> "Rendah"
            normal > rendah && normal > tinggi -> "Normal"
            tinggi > rendah && tinggi > normal -> "Tinggi"
            else -> "Tidak Diketahui"
        }
    }

//implikasi
    fun evaluateKualitasAir(ph: Float, suhu: Float, turbidity: Float): String {
        val rendah = minOf(pHRendah(ph), suhuRendah(suhu), turbidityRendah(turbidity))
        val normal = minOf(pHNormal(ph), suhuNormal(suhu), turbidityNormal(turbidity))
        val tinggi = minOf(pHTinggi(ph), suhuTinggi(suhu), turbidityTinggi(turbidity))

//defuzzifikasi
        return when {
            rendah > normal && rendah > tinggi -> "Tidak Layak" //tidak layak (Rendah)
            normal > rendah && normal > tinggi -> "Layak"
            tinggi > rendah && tinggi > normal -> "Tidak Layak" //tidak layak (Tinggi)
            else -> "Tidak Layak"
        }
    }
}
