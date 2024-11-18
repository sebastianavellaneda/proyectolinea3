package com.example.futbolteamup.interfaz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolteamup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CrearPartidoActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_partido)

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Referencias a los campos del formulario
        val etUbicacion = findViewById<EditText>(R.id.etUbicacion)
        val etTipoGrama = findViewById<EditText>(R.id.etTipoGrama)
        val etJugadores = findViewById<EditText>(R.id.etJugadores)
        val etHora = findViewById<EditText>(R.id.etHora)
        val etEspecificaciones = findViewById<EditText>(R.id.etEspecificaciones)
        val btnGuardarPartido = findViewById<Button>(R.id.btnGuardarPartido)

        // Acción del botón
        btnGuardarPartido.setOnClickListener {
            val ubicacion = etUbicacion.text.toString().trim()
            val tipoGrama = etTipoGrama.text.toString().trim()
            val jugadores = etJugadores.text.toString().trim().toIntOrNull()
            val hora = etHora.text.toString().trim()
            val especificaciones = etEspecificaciones.text.toString().trim()

            // Validación
            if (ubicacion.isEmpty() || tipoGrama.isEmpty() || jugadores == null || hora.isEmpty()) {
                Toast.makeText(this, "Por favor llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear el partido
            val partido = hashMapOf(
                "ubicacion" to ubicacion,
                "tipoGrama" to tipoGrama,
                "jugadores" to jugadores,
                "hora" to hora,
                "especificaciones" to especificaciones,
                "creador" to auth.currentUser?.uid
            )

            db.collection("partidos").add(partido)
                .addOnSuccessListener {
                    Toast.makeText(this, "Partido creado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar el partido: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
