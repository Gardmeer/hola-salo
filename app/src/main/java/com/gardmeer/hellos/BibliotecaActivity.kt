package com.gardmeer.hellos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BibliotecaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biblioteca)

        val listDatos = ArrayList<NuevaPalabra>()
        val adapterDatos = AdapterDatos(listDatos)

        val rvBiblioteca: RecyclerView = findViewById(R.id.rvBiblioteca)
        rvBiblioteca.adapter = adapterDatos
        rvBiblioteca.layoutManager = LinearLayoutManager(this)

        listDatos.add(NuevaPalabra("Peppa",R.drawable.peppa))
        listDatos.add(NuevaPalabra("George",R.drawable.george))
        listDatos.add(NuevaPalabra("Mummy",R.drawable.mummy))
        listDatos.add(NuevaPalabra("Daddy",R.drawable.daddy))
        listDatos.add(NuevaPalabra("Grandpa",R.drawable.grandpa))

        adapterDatos.setOnItemClickListener(object:AdapterDatos.onItemClickListener {
            override fun onItemClick(view: View) {
                val palabra = listDatos.get(rvBiblioteca.getChildAdapterPosition(view)).getPalabra()
                val imagen = listDatos.get(rvBiblioteca.getChildAdapterPosition(view)).getImagen()
                Toast.makeText(applicationContext,"Soy $palabra",Toast.LENGTH_LONG).show()
                verVideo(palabra, imagen)
            }
        })
    }

    fun verVideo(palabra:String?, imagen:Int){
        val sVp = Intent(this,PlayerActivity::class.java)
        sVp.putExtra("palabra",palabra)
        sVp.putExtra("imagen",imagen)
        startActivity(sVp)
    }
    fun irInicio(view: View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }
}