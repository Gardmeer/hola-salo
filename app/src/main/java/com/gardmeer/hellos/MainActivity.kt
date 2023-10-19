package com.gardmeer.hellos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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