package com.example.futbolteamup.interfaz

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.futbolteamup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarUsuarioActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

    private lateinit var fullNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var positionEditText: EditText
    private lateinit var editIcon: ImageView
    private lateinit var saveButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usuario_activity)

        // Inicializamos Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Obtenemos el UID del usuario autenticado
        userId = auth.currentUser?.uid ?: return

        // Vinculamos las vistas
        fullNameEditText = findViewById(R.id.full_name)
        phoneNumberEditText = findViewById(R.id.phone_number)
        positionEditText = findViewById(R.id.position)
        editIcon = findViewById(R.id.edit_icon)
        saveButton = findViewById(R.id.save_button)

        // Cargamos los datos del usuario
        loadUserData()

        // Habilitar edición al presionar el ícono de lápiz
        editIcon.setOnClickListener {
            enableEditing()
        }

        // Guardar cambios en los datos del usuario
        saveButton.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        // Consultamos los datos del usuario desde Firestore
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Si el documento existe, cargamos los datos en los campos correspondientes
                    fullNameEditText.setText(document.getString("nombre"))
                    phoneNumberEditText.setText(document.getString("telefono"))
                    positionEditText.setText(document.getString("posicion"))
                } else {
                    // Si no existe el documento, mostramos un mensaje de error
                    Toast.makeText(this, "No se encontraron los datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // En caso de error, mostramos el error
                Log.e("Firestore", "Error al cargar los datos del usuario", e)
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun enableEditing() {
        // Habilitamos la edición de los campos
        fullNameEditText.isEnabled = true
        phoneNumberEditText.isEnabled = true
        positionEditText.isEnabled = true
    }

    private fun saveUserData() {
        // Obtenemos los valores modificados por el usuario
        val updatedName = fullNameEditText.text.toString()
        val updatedPhone = phoneNumberEditText.text.toString()
        val updatedPosition = positionEditText.text.toString()

        // Validamos que los campos no estén vacíos
        if (updatedName.isBlank() || updatedPhone.isBlank() || updatedPosition.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Creamos un mapa con los datos actualizados
        val userUpdates = hashMapOf(
            "nombre" to updatedName,
            "telefono" to updatedPhone,
            "posicion" to updatedPosition
        )

        // Actualizamos los datos en Firestore
        db.collection("usuarios").document(userId).update(userUpdates as Map<String, Any>)
            .addOnSuccessListener {
                // Si la actualización fue exitosa, mostramos un mensaje y deshabilitamos los campos
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                fullNameEditText.isEnabled = false
                phoneNumberEditText.isEnabled = false
                positionEditText.isEnabled = false
            }
            .addOnFailureListener { e ->
                // En caso de error, mostramos un mensaje
                Toast.makeText(this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show()
            }
    }
}