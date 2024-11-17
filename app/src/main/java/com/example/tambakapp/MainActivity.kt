package com.example.tambakapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tambakapp.customView.KualitasAirView
import com.example.tambakapp.customView.SisaPakanView
import com.example.tambakapp.customView.StatusSensorView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var phValueTextView: TextView
    private lateinit var tdsValueTextView: TextView
    private lateinit var turbidityValueTextView: TextView
    private lateinit var sisaPakanView: SisaPakanView
    private lateinit var relayButton: Button
    private lateinit var phStatusView: StatusSensorView
    private lateinit var tdsStatusView: StatusSensorView
    private lateinit var turbidityStatusView: StatusSensorView
    private lateinit var kualitasAirView: KualitasAirView
    private lateinit var dinamoStatusTextView: TextView
    private lateinit var servoStatusTextView: TextView
    private var relayState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        phValueTextView = findViewById(R.id.ph_value)
        tdsValueTextView = findViewById(R.id.tds_value)
        turbidityValueTextView = findViewById(R.id.turbidity_value)
        sisaPakanView = findViewById(R.id.volume_percentage)
        relayButton = findViewById(R.id.relay_control_button)
        phStatusView = findViewById(R.id.ph_status)
        tdsStatusView = findViewById(R.id.tds_status)
        turbidityStatusView = findViewById(R.id.turbidity_status)
        kualitasAirView = findViewById(R.id.kualitas_air)
        dinamoStatusTextView = findViewById(R.id.status_dinamo)
        servoStatusTextView = findViewById(R.id.status_servo)

        // Reference to Firebase paths
        val database = FirebaseDatabase.getInstance()
        val sensorRef = database.getReference("sensor")
        val fuzzyRef = database.getReference("fuzzy")
        val pakanOtomatisRef = database.getReference("pakanOtomatis")

        // Listen for sensor data changes
        sensorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ph = snapshot.child("ph").getValue(Int::class.java) ?: 0
                val tds = snapshot.child("tds").getValue(Int::class.java) ?: 0
                val turbidity = snapshot.child("turbidity").getValue(Int::class.java) ?: 0

                // Update the UI
                phValueTextView.text = ph.toString()
                tdsValueTextView.text = tds.toString()
                turbidityValueTextView.text = turbidity.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load sensor data", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        // Listen for fuzzy status data changes
        fuzzyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val phStatus = snapshot.child("status_pH").getValue(String::class.java) ?: "Unknown"
                val tdsStatus =
                    snapshot.child("status_tds").getValue(String::class.java) ?: "Unknown"
                val turbidityStatus =
                    snapshot.child("status_turbidity").getValue(String::class.java) ?: "Unknown"
                val kualitasAir =
                    snapshot.child("kualitas_air").getValue(String::class.java) ?: "Unknown"

                // Update the UI with status
                phStatusView.updateStatus(phStatus)
                tdsStatusView.updateStatus(tdsStatus)
                turbidityStatusView.updateStatus(turbidityStatus)
                kualitasAirView.updateStatusAir(kualitasAir)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load status data", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        // Listen for pakanOtomatis data changes
        pakanOtomatisRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sisaPakan = snapshot.child("volume").getValue(Float::class.java) ?: 0f
                val relayState = snapshot.child("relay").getValue(Int::class.java) ?: 0
                val servoState = snapshot.child("servo").getValue(Int::class.java) ?: 0

                // Update UI
                sisaPakanView.updateSisaPakan(sisaPakan)
                relayButton.text = if (relayState == 1) "Tutup Pakan" else "Buka Pakan"
                dinamoStatusTextView.text = if (relayState == 1) "Menyala" else "Mati"
                servoStatusTextView.text = if (servoState == 1) "Terbuka" else "Tertutup"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity, "Failed to load pakanOtomatis data", Toast.LENGTH_SHORT
                ).show()
            }
        })

        relayButton.setOnClickListener {
            relayState = !relayState

            pakanOtomatisRef.child("relay").setValue(if (relayState) 1 else 0)
                .addOnSuccessListener {
                    relayButton.text = if (relayState) "Tutup Pakan" else "Buka Pakan"
                    Toast.makeText(this, "Relay state updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update relay state", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
