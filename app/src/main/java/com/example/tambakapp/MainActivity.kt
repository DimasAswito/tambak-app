package com.example.tambakapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var phValueTextView: TextView
    private lateinit var tdsValueTextView: TextView
    private lateinit var turbidityValueTextView: TextView
    private lateinit var volumePercentageTextView: TextView
    private lateinit var relayButton: Button
    private lateinit var phStatusTextView: TextView
    private lateinit var tdsStatusTextView: TextView
    private lateinit var turbidityStatusTextView: TextView
    private lateinit var kualitasAirTextView: TextView
    private var relayState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        phValueTextView = findViewById(R.id.ph_value)
        tdsValueTextView = findViewById(R.id.tds_value)
        turbidityValueTextView = findViewById(R.id.turbidity_value)
        volumePercentageTextView = findViewById(R.id.volume_percentage)
        relayButton = findViewById(R.id.relay_control_button)
        phStatusTextView = findViewById(R.id.ph_status)
        tdsStatusTextView = findViewById(R.id.tds_status)
        turbidityStatusTextView = findViewById(R.id.turbidity_status)
        kualitasAirTextView = findViewById(R.id.kualitas_air)

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
                val tdsStatus = snapshot.child("status_tds").getValue(String::class.java) ?: "Unknown"
                val turbidityStatus = snapshot.child("status_turbidity").getValue(String::class.java) ?: "Unknown"
                val kualitasAir = snapshot.child("kualitas_air").getValue(String::class.java) ?: "Unknown"

                // Update the UI with status
                phStatusTextView.text = phStatus
                tdsStatusTextView.text = tdsStatus
                turbidityStatusTextView.text = turbidityStatus
                kualitasAirTextView.text = "Kualitas Air : $kualitasAir"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load status data", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        // Listen for pakanOtomatis data changes
        pakanOtomatisRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("DefaultLocale")
            override fun onDataChange(snapshot: DataSnapshot) {
                val volume = snapshot.child("volume").getValue(Float::class.java) ?: 0f
                relayState = snapshot.child("relay").getValue(Int::class.java) == 1

                // Update the UI
                volumePercentageTextView.text = String.format("Sisa Pakan: %.1f%%", volume)
                relayButton.text = if (relayState) "Nyalakan Relay" else "Matikan Relay"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity, "Failed to load pakanOtomatis data", Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Relay button click listener
        relayButton.setOnClickListener {
            // Toggle relay state
            relayState = !relayState

            // Update Firebase with new relay state
            pakanOtomatisRef.child("relay").setValue(if (relayState) 1 else 0)
                .addOnSuccessListener {
                    relayButton.text = if (relayState) "Nyalakan Relay" else "Matikan Relay"
                    Toast.makeText(this, "Relay state updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to update relay state", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
