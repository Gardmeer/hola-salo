package com.gardmeer.hellos

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri

class PlayerActivity : AppCompatActivity() {
    private lateinit var vvwAprende : VideoView
    private lateinit var imvAprende : ImageView
    private var txtVideo : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val palabra = intent.getStringExtra("palabra")

        vvwAprende=findViewById(R.id.vvwAprende)
        imvAprende=findViewById(R.id.imvAprende)
        txtVideo=findViewById(R.id.txtVideo)

        cargarVideo(palabra)
    }

    fun reproducirVideo(view:View){
        vvwAprende.start()
    }

    fun yahoo(view:View){
        Toast.makeText(applicationContext,"Felicitaciones!!", Toast.LENGTH_LONG).show()
    }

    fun modificarPalabra(view:View){
        val palabra = txtVideo?.text.toString()
        val mPa = Intent(this,CrearActivity::class.java)
        mPa.putExtra("palabra",palabra)
        startActivity(mPa)
    }

    fun eliminarPalabra(view:View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.delete_cancel) + " ${txtVideo?.text.toString()}")
            .setPositiveButton(R.string.accept
            ) { _, _ ->
                val palabra = txtVideo?.text.toString()
                val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
                val editarL = prefL.edit()
                val lista = prefL.getStringSet("addlista", sortedSetOf<String?>())
                val reciente = prefL.getStringSet("reclista", sortedSetOf<String?>())
                val buLista = lista
                val buReciente = reciente

                editarL.remove("addlista")
                editarL.remove("reclista")
                editarL.apply()

                buLista!!.remove(palabra)
                buReciente!!.remove(palabra)
                editarL.putStringSet("addlista", buLista)
                editarL.putStringSet("reclista", buReciente)
                editarL.apply()

                Toast.makeText(applicationContext, "$palabra eliminada!!", Toast.LENGTH_LONG).show()

                val iIn = Intent(this, MainActivity::class.java)
                startActivity(iIn)
            }
            .setNegativeButton(R.string.cancel
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    private fun cargarVideo(cargarPalabra: String?){
        val pref = getSharedPreferences(cargarPalabra, Context.MODE_PRIVATE)
        val imagen = pref.getString("uriimagen","")!!.toUri()
        val video = pref.getString("urivideo","")!!.toUri()

        imvAprende.setImageURI(imagen)
        txtVideo?.text = cargarPalabra
        vvwAprende.setVideoURI(video)
        val mediaController = MediaController(this)
        vvwAprende.setMediaController(mediaController)
    }

    fun irInicio(view:View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }
}