package com.gardmeer.hellos

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    val listDatos = ArrayList<NuevaPalabra>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_HelloS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapterDatos = AdapterDatos(listDatos)
        val rvReciente: RecyclerView = findViewById(R.id.rvReciente)
        rvReciente.adapter = adapterDatos
        rvReciente.layoutManager = LinearLayoutManager(this)

        llenarReciente()

        adapterDatos.setOnItemClickListener(object:AdapterDatos.onItemClickListener {
            override fun onItemClick(view:View) {
                val palabra = listDatos[rvReciente.getChildAdapterPosition(view)].getPalabra()
                Toast.makeText(applicationContext,"Aprende la palabra $palabra!!",Toast.LENGTH_LONG).show()
                verVideo(palabra)
            }
        })
        }

    fun crearPalabra(view:View){
        val cWr = Intent(this,CrearActivity::class.java)
        startActivity(cWr)
    }

    fun verBiblioteca(view:View){
        val sLb = Intent(this,BibliotecaActivity::class.java)
        startActivity(sLb)
    }

    fun verVideo(palabra:String?){
        val sVp = Intent(this,PlayerActivity::class.java)
        sVp.putExtra("palabra",palabra)
        startActivity(sVp)
    }

    private fun llenarReciente() {
        val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
        val lista = prefL.getStringSet("reclista",sortedSetOf<String?>())

        lista!!.forEach {
            val pref = getSharedPreferences(it, Context.MODE_PRIVATE)
            val uriImagen = pref.getString("uriimagen","")!!.toUri()
            listDatos.add(NuevaPalabra(it,uriImagen))
        }
    }
}