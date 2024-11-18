package com.example.futbolteamup.interfaz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolteamup.R
import com.google.firebase.firestore.FirebaseFirestore

class BuscarPartidoActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: PartidoAdapter
    private lateinit var rvResultados: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_partido)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        val btnBuscar = findViewById<Button>(R.id.btnBuscar)
        rvResultados = findViewById(R.id.rvResultados)

        // Configurar el RecyclerView
        rvResultados.layoutManager = LinearLayoutManager(this)
        adapter = PartidoAdapter(mutableListOf())
        rvResultados.adapter = adapter

        // Acción del botón Buscar
        btnBuscar.setOnClickListener {
            val criterio = etBuscar.text.toString().trim()
            if (criterio.isEmpty()) {
                Toast.makeText(this, "Ingresa un criterio de búsqueda", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            buscarPartidos(criterio)
        }
    }

    private fun buscarPartidos(criterio: String) {
        // Consulta a Firestore
        db.collection("partidos")
            .whereEqualTo("ubicacion", criterio)
            .get()
            .addOnSuccessListener { documents ->
                val resultados = mutableListOf<Partido>()
                for (document in documents) {
                    val partido = document.toObject(Partido::class.java)
                    resultados.add(partido)
                }
                if (resultados.isEmpty()) {
                    Toast.makeText(this, "No se encontraron partidos", Toast.LENGTH_SHORT).show()
                }
                adapter.updateData(resultados)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al buscar partidos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
