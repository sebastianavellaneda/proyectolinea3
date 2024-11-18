package com.example.futbolteamup.interfaz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.futbolteamup.R

class PartidoAdapter(private var partidos: MutableList<Partido>) :
    RecyclerView.Adapter<PartidoAdapter.PartidoViewHolder>() {

    class PartidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUbicacion: TextView = itemView.findViewById(R.id.tvUbicacion)
        val tvTipoGrama: TextView = itemView.findViewById(R.id.tvTipoGrama)
        val tvJugadores: TextView = itemView.findViewById(R.id.tvJugadores)
        val tvHora: TextView = itemView.findViewById(R.id.tvHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_partido, parent, false)
        return PartidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PartidoViewHolder, position: Int) {
        val partido = partidos[position]
        holder.tvUbicacion.text = "Ubicación: ${partido.ubicacion}"
        holder.tvTipoGrama.text = "Grama: ${partido.tipoGrama}"
        holder.tvJugadores.text = "Jugadores: ${partido.jugadores}"
        holder.tvHora.text = "Hora: ${partido.hora}"
    }

    override fun getItemCount() = partidos.size

    fun updateData(newPartidos: MutableList<Partido>) {
        partidos = newPartidos
        notifyDataSetChanged()
    }
}
