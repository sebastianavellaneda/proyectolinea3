package com.example.futbolteamup.interfaz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.widget.EditText
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolteamup.R
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesList: ListView
    private lateinit var messagesAdapter: MessagesAdapter  // Usar el adaptador personalizado
    private val messages = ArrayList<String>()

    private var chatType = "general"  // Tipo de chat: "general" o "partido"
    private var partidoId: String? = null  // ID del partido seleccionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        messagesList = findViewById(R.id.messagesList)

        // Usar el adaptador personalizado MessagesAdapter
        messagesAdapter = MessagesAdapter(this, messages)
        messagesList.adapter = messagesAdapter

        // Cargar los mensajes del chat general
        loadMessages()

        // Enviar un mensaje
        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageInput.text.clear()
            } else {
                Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadMessages() {
        val messagesRef: DatabaseReference = database.getReference("messages/general")

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(String::class.java)
                if (message != null) {
                    messages.add(message)
                    messagesAdapter.notifyDataSetChanged()  // Notificar al adaptador que hay nuevos mensajes
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Error al cargar mensajes: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage(message: String) {
        val messagesRef: DatabaseReference = database.getReference("messages/general")

        val messageId = messagesRef.push().key
        if (messageId != null) {
            val messageData = mapOf(
                "message" to message,
                "sender" to "usuario1",  // Nombre o ID del usuario autenticado
                "timestamp" to System.currentTimeMillis().toString()
            )

            messagesRef.child(messageId).setValue(messageData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error al enviar mensaje: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
