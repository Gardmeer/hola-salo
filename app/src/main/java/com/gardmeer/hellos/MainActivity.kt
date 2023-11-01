package com.gardmeer.hellos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listDatos = arrayOf("Video 1", "Video 2", "Video 3", "Video 4")
        val adapterDatos = AdapterDatos(listDatos)

        val rvReciente: RecyclerView = findViewById(R.id.rvReciente)
        rvReciente.adapter = adapterDatos

        rvReciente.layoutManager = LinearLayoutManager(this)
        //Toast.makeText(this,"Crea una nueva palabra",Toast.LENGTH_LONG).show()

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