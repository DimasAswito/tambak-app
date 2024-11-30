package com.example.tambakapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tambakapp.customView.KualitasAirView
import com.example.tambakapp.customView.SisaPakanView
import com.example.tambakapp.customView.StatusSensorView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var fuzzyHelper: FuzzyHelper
    private lateinit var pakanButton: Button
    private var isPakanOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fuzzyHelper = FuzzyHelper()

        val database = FirebaseDatabase.getInstance()
        val sensorRef = database.getReference("sensor")
        val pakanOtomatisRef = database.getReference("pakanOtomatis")

        val phValueTextView: TextView = findViewById(R.id.ph_value)
        val suhuAirValueTextView: TextView = findViewById(R.id.suhuAir_value)
        val turbidityValueTextView: TextView = findViewById(R.id.turbidity_value)
        val phStatusView: StatusSensorView = findViewById(R.id.ph_status)
        val suhuAirStatusView: StatusSensorView = findViewById(R.id.suhuAir_status)
        val turbidityStatusView: StatusSensorView = findViewById(R.id.turbidity_status)
        val kualitasAirView: KualitasAirView = findViewById(R.id.kualitas_air)
        val volumePercentageView: SisaPakanView = findViewById(R.id.volume_percentage)
        val dinamoStatusTextView: TextView = findViewById(R.id.status_dinamo)
        val servoStatusTextView: TextView = findViewById(R.id.status_servo)

        // Listen for sensor data changes
        sensorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ph = snapshot.child("ph").getValue(Float::class.java) ?: 0f
                val suhuAir = snapshot.child("temp").getValue(Float::class.java)?.toInt() ?: 0
                val turbidity = snapshot.child("turbidity").getValue(Float::class.java)?.toInt() ?: 0

                phValueTextView.text = String.format("%.1f", ph)
                suhuAirValueTextView.text = "$suhuAir°"
                turbidityValueTextView.text = turbidity.toString()

                val phStatus = fuzzyHelper.evaluatePH(ph)
                val suhuAirStatus = fuzzyHelper.evaluateSuhuAir(suhuAir)
                val turbidityStatus = fuzzyHelper.evaluateTurbidity(turbidity)

                phStatusView.updateStatus(phStatus)
                suhuAirStatusView.updateStatus(suhuAirStatus)
                turbidityStatusView.updateStatus(turbidityStatus)

                val kualitasAir = fuzzyHelper.evaluateKualitasAir(phStatus, suhuAirStatus, turbidityStatus)
                kualitasAirView.updateStatusAir(kualitasAir)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load sensor data", Toast.LENGTH_SHORT).show()
            }
        })

        // Listen for relay and servo states to update button label
        pakanOtomatisRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val volume = snapshot.child("volume").getValue(Float::class.java) ?: 0f
                volumePercentageView.updateSisaPakan(volume)
                val relayStatus = snapshot.child("relay").getValue(Int::class.java) ?: 0
                dinamoStatusTextView.text = if (relayStatus == 1) "Menyala" else "Mati"

                val servoStatus = snapshot.child("servo").getValue(Int::class.java) ?: 0
                servoStatusTextView.text = if (servoStatus == 1) "Terbuka" else "Tertutup"

                isPakanOpen = relayStatus == 1 && servoStatus == 1
                updatePakanButton()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load pakan state", Toast.LENGTH_SHORT).show()
            }
        })

        // Toggle relay and servo states on button click
        pakanButton = findViewById(R.id.pakan_button)
        pakanButton.setOnClickListener {
            val newState = if (isPakanOpen) 0 else 1
            pakanOtomatisRef.child("relay").setValue(newState)
            pakanOtomatisRef.child("servo").setValue(newState)
                .addOnSuccessListener {
                    isPakanOpen = !isPakanOpen
                    updatePakanButton()
                    Toast.makeText(this, if (isPakanOpen) "Buka Pakan" else "Tutup Pakan", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update pakan state", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updatePakanButton() {
        pakanButton.text = if (isPakanOpen) "Tutup Pakan" else "Buka Pakan"
    }
}
