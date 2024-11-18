package com.example.futbolteamup.interfaz

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolteamup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GestionarPartidosActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var noPartidosText: TextView
    private lateinit var partidosListView: ListView
    private lateinit var crearPartidoButton: Button

    private val partidos = ArrayList<String>()  // Lista de nombres de los partidos
    private val partidoIds = ArrayList<String>()  // Lista de IDs de los partidos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_partido)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Referencias a los elementos del layout
        noPartidosText = findViewById(R.id.noPartidosText)
        partidosListView = findViewById(R.id.partidosListView)
        crearPartidoButton = findViewById(R.id.crearPartidoButton)

        // Configurar el botón para crear un nuevo partido
        crearPartidoButton.setOnClickListener {
            val intent = Intent(this, CrearPartidoActivity::class.java)
            startActivity(intent)
        }

        // Cargar los partidos creados por el administrador
        cargarPartidos()

        // Manejar la selección de un partido de la lista
        partidosListView.setOnItemClickListener { _, _, position, _ ->
            val partidoId = partidoIds[position]  // Obtener el ID del partido seleccionado
            val intent = Intent(this, GestionarPartidoDetailActivity::class.java)
            intent.putExtra("PARTIDO_ID", partidoId)  // Pasar el ID del partido
            startActivity(intent)
        }
    }

    private fun cargarPartidos() {
        val user = auth.currentUser
        if (user != null) {
            val partidosRef = database.getReference("partidos")
            partidosRef.orderByChild("creador").equalTo(user.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    partidos.clear()  // Limpiar lista de partidos
                    partidoIds.clear()  // Limpiar lista de IDs

                    if (snapshot.exists()) {
                        // Iterar sobre los partidos creados por el usuario
                        for (partidoSnapshot in snapshot.children) {
                            val partido = partidoSnapshot.child("ubicacion").value.toString()
                            partidos.add(partido)
                            partidoIds.add(partidoSnapshot.key ?: "")
                        }

                        // Mostrar los partidos en el ListView
                        val adapter = ArrayAdapter(this@GestionarPartidosActivity, android.R.layout.simple_list_item_1, partidos)
                        partidosListView.adapter = adapter

                        // Mostrar el ListView y ocultar el mensaje si hay partidos
                        partidosListView.visibility = ListView.VISIBLE
                        noPartidosText.visibility = TextView.GONE
                    } else {
                        // No hay partidos creados, mostrar mensaje
                        partidosListView.visibility = ListView.GONE
                        noPartidosText.visibility = TextView.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@GestionarPartidosActivity, "Error al cargar los partidos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No estás autenticado. Por favor, inicia sesión.", Toast.LENGTH_SHORT).show()
        }
    }
}
