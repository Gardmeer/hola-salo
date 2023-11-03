package com.gardmeer.hellos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
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
            override fun onItemClick(position: Int) {
                Toast.makeText(this@MainActivity,"Soy $position",Toast.LENGTH_LONG).show()
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
}