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

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.Theme_HelloS)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var listDatos = ArrayList<NuevaPalabra>()
        val adapterDatos = AdapterDatos(listDatos)

        val rvReciente: RecyclerView = findViewById(R.id.rvReciente)
        rvReciente.adapter = adapterDatos
        rvReciente.layoutManager = LinearLayoutManager(this)

        listDatos.add(NuevaPalabra("Peppa",R.drawable.peppa))
        listDatos.add(NuevaPalabra("George",R.drawable.george))

        adapterDatos.setOnItemClickListener(object:AdapterDatos.onItemClickListener {
            override fun onItemClick(view:View) {
                val palabra = listDatos.get(rvReciente.getChildAdapterPosition(view)).getPalabra()
                val imagen = listDatos.get(rvReciente.getChildAdapterPosition(view)).getImagen()
                Toast.makeText(applicationContext,"Soy $palabra",Toast.LENGTH_LONG).show()
                verVideo(palabra, imagen)
            }
        })
        }

    fun crearPalabra(view:View){
        val cWr = Intent(this,CrearActivity::class.java)
        startActivity(cWr)
    }
    fun verBiblioteca(view: View){
        val sLb = Intent(this,BibliotecaActivity::class.java)
        startActivity(sLb)
    }

    fun verVideo(palabra:String?, imagen:Int){
        val sVp = Intent(this,PlayerActivity::class.java)
        sVp.putExtra("palabra",palabra)
        sVp.putExtra("imagen",imagen)
        startActivity(sVp)
    }
}