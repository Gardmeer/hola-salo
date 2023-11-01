package com.gardmeer.hellos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterDatos(private val listaDatos: Array<String>) :
    RecyclerView.Adapter<AdapterDatos.ViewHolderDatos>()  {
    class ViewHolderDatos(view: View) : RecyclerView.ViewHolder(view) {
        val dato: TextView

        init {
            dato = view.findViewById(R.id.txtDato)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)

        return ViewHolderDatos(view)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.dato.text = listaDatos[position]
    }

    override fun getItemCount() = listaDatos.size

}