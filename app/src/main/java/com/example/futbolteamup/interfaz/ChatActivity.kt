package com.example.futbolteamup.interfaz

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolteamup.R
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesList: ListView
    private lateinit var messagesAdapter: ArrayAdapter<String>
    private val messages = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance()
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        messagesList = findViewById(R.id.messagesList)

        // Configurar el adaptador para la lista
        messagesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messages)
        messagesList.adapter = messagesAdapter

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageInput.text.clear()
            } else {
                Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show()
            }
        }

        // Leer mensajes en tiempo real
        val messagesRef = database.getReference("messages")
        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(String::class.java)
                if (message != null) {
                    messages.add(message)
                    messagesAdapter.notifyDataSetChanged()
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
        val messagesRef = database.getReference("messages")
        val messageId = messagesRef.push().key
        if (messageId != null) {
            messagesRef.child(messageId).setValue(message).addOnSuccessListener {
                Toast.makeText(this, "Mensaje enviado", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { error ->
                Toast.makeText(this, "Error al enviar mensaje: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
