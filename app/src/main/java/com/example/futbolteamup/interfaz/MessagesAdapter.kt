package com.example.futbolteamup.interfaz

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MessagesAdapter(
    context: Context,
    private val messages: List<String>
) : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, messages) {

    // Este método se usa para inflar la vista personalizada de cada mensaje en la lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)

        // Obtener el mensaje en la posición actual
        val message = getItem(position)

        // Personalizar la vista
        val textView = view as TextView
        textView.text = message  // Setear el mensaje en el TextView

        // Crear un fondo difuminado para el mensaje
        val messageBackground = GradientDrawable()
        messageBackground.cornerRadius = 16f  // Redondear las esquinas del fondo
        messageBackground.setColor(Color.parseColor("#80000000"))  // Fondo negro con opacidad

        // Asignar el fondo al TextView (mensaje)
        textView.background = messageBackground

        return view
    }
}
