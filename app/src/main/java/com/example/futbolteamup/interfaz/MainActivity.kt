package com.example.futbolteamup.interfaz


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolteamup.R
import com.example.futbolteamup.interfaz.CrearPartidoActivity

import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        val imgUsuario: ImageView = findViewById(R.id.imgUsuario) // Ícono del usuario
        val btnGestionarPartidos: ImageView = findViewById(R.id.btnGestionarPartidos)
        val btnChat: ImageView = findViewById(R.id.btnChat)
        val btnCrearPartido: ImageView = findViewById(R.id.btnCrearPartido)
        val btnBuscarPartido: ImageView = findViewById(R.id.btnBuscarPartido)
        val btnSalir: ImageView = findViewById(R.id.btnSalir)

        // Obtener el nombre del usuario actual desde Firebase
        val usuarioActual = auth.currentUser
        if (usuarioActual != null) {
            db.collection("usuarios").document(usuarioActual.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre") ?: "Usuario"
                        tvNombreUsuario.text = nombre
                    }
                }
        }

        // Configurar acciones de los botones
        imgUsuario.setOnClickListener {
            val intent = Intent(this, EditarUsuarioActivity::class.java)
            startActivity(intent)
        }

        btnGestionarPartidos.setOnClickListener {
            startActivity(Intent(this, GestionarPartidosActivity::class.java))
        }

        btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        btnCrearPartido.setOnClickListener {
            startActivity(Intent(this, CrearPartidoActivity::class.java))
        }

        btnBuscarPartido.setOnClickListener {
            startActivity(Intent(this, BuscarPartidoActivity::class.java))
        }

        btnSalir.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

