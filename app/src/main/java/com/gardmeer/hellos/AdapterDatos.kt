package com.gardmeer.hellos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterDatos(private val listaDatos: ArrayList<NuevaPalabra>) :
    RecyclerView.Adapter<AdapterDatos.ViewHolderDatos>()  {

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener{
        fun onItemClick(view:View)
    }

    fun setOnItemClickListener(listener:OnItemClickListener){
        mListener=listener
    }

    class ViewHolderDatos(view: View, listener:OnItemClickListener) : RecyclerView.ViewHolder(view) {
        val datoPalabra: TextView
        val datoImagen: ImageView

        init {
            datoPalabra = view.findViewById(R.id.txtDato)
            datoImagen = view.findViewById(R.id.imvMini)

            itemView.setOnClickListener{
                listener.onItemClick(view)
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
        holder.datoImagen.setImageURI(listaDatos[position].getImagen())
    }

    override fun getItemCount() = listaDatos.size

}