package com.example.futbolteamup.interfaz

import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolteamup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GestionarPartidoDetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var ubicacionEditText: EditText
    private lateinit var tipoGramaEditText: EditText
    private lateinit var cantidadJugadoresEditText: EditText
    private lateinit var horaEditText: EditText
    private lateinit var especificacionesEditText: EditText
    private lateinit var guardarCambiosButton: Button

    private var partidoId: String? = null  // ID del partido

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gestionarpartido_detail_activity)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Referencias a los campos de edición
        ubicacionEditText = findViewById(R.id.ubicacion)
        tipoGramaEditText = findViewById(R.id.tipoGrama)
        cantidadJugadoresEditText = findViewById(R.id.cantidadJugadores)
        horaEditText = findViewById(R.id.hora)
        especificacionesEditText = findViewById(R.id.especificaciones)
        guardarCambiosButton = findViewById(R.id.guardarCambiosButton)

        // Obtener el partidoId desde el Intent
        partidoId = intent.getStringExtra("PARTIDO_ID")

        // Cargar los datos del partido
        cargarPartido()

        // Configurar el botón de guardar cambios
        guardarCambiosButton.setOnClickListener {
            guardarCambios()
        }
    }

    // Cargar los detalles del partido seleccionado desde Firebase
    private fun cargarPartido() {
        val user = auth.currentUser
        if (user != null && partidoId != null) {
            val partidoRef = database.getReference("partidos").child(partidoId!!)

            partidoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Obtener los datos del partido
                        val ubicacion = snapshot.child("ubicacion").value.toString()
                        val tipoGrama = snapshot.child("tipoGrama").value.toString()
                        val cantidadJugadores = snapshot.child("jugadores").value.toString()
                        val hora = snapshot.child("hora").value.toString()
                        val especificaciones = snapshot.child("especificaciones").value.toString()

                        // Setear los valores en los campos de edición
                        ubicacionEditText.setText(ubicacion)
                        tipoGramaEditText.setText(tipoGrama)
                        cantidadJugadoresEditText.setText(cantidadJugadores)
                        horaEditText.setText(hora)
                        especificacionesEditText.setText(especificaciones)
                    } else {
                        Toast.makeText(this@GestionarPartidoDetailActivity, "Partido no encontrado", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@GestionarPartidoDetailActivity, "Error al cargar los datos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Guardar los cambios realizados en el partido
    private fun guardarCambios() {
        val nuevaUbicacion = ubicacionEditText.text.toString()
        val nuevoTipoGrama = tipoGramaEditText.text.toString()
        val nuevaCantidadJugadores = cantidadJugadoresEditText.text.toString()
        val nuevaHora = horaEditText.text.toString()
        val nuevasEspecificaciones = especificacionesEditText.text.toString()

        // Validar que todos los campos sean válidos
        if (nuevaUbicacion.isEmpty() || nuevoTipoGrama.isEmpty() || nuevaCantidadJugadores.isEmpty() ||
            nuevaHora.isEmpty() || nuevasEspecificaciones.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Guardar los cambios en Firebase
        partidoId?.let {
            val partidoRef = database.getReference("partidos").child(it)

            val partidoActualizado = mapOf(
                "ubicacion" to nuevaUbicacion,
                "tipoGrama" to nuevoTipoGrama,
                "jugadores" to nuevaCantidadJugadores.toInt(),
                "hora" to nuevaHora,
                "especificaciones" to nuevasEspecificaciones
            )

            partidoRef.updateChildren(partidoActualizado)
                .addOnSuccessListener {
                    Toast.makeText(this, "Partido actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()  // Regresar a la pantalla anterior
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error al actualizar: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
