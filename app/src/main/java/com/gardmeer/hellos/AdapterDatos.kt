package com.gardmeer.hellos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterDatos(private val listaDatos: ArrayList<NuevaPalabra>) :
    RecyclerView.Adapter<AdapterDatos.ViewHolderDatos>()  {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener:onItemClickListener){
        mListener=listener
    }

    class ViewHolderDatos(view: View, listener:onItemClickListener) : RecyclerView.ViewHolder(view) {
        val datoPalabra: TextView
        val datoImagen: ImageView

        init {
            datoPalabra = view.findViewById(R.id.txtDato)
            datoImagen = view.findViewById(R.id.imvMini)

            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatos {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)

        return ViewHolderDatos(view, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolderDatos, position: Int) {
        holder.datoPalabra.text = listaDatos[position].getPalabra()
        holder.datoImagen.setImageResource(listaDatos[position].getImagen())
    }

    override fun getItemCount() = listaDatos.size

}