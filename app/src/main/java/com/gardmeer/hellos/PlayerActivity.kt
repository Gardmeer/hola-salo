package com.gardmeer.hellos

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView

class PlayerActivity : AppCompatActivity() {

    private lateinit var vvwAprende : VideoView
    private var ourRequestCode : Int = 123
    lateinit var imvAprende : ImageView
    var txtVideo : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val palabra = intent.getStringExtra("palabra")
        val imagen = intent.getIntExtra("imagen", 0)

        vvwAprende=findViewById(R.id.vvwAprende)
        imvAprende=findViewById(R.id.imvAprende)
        txtVideo=findViewById(R.id.txtVideo)

        imvAprende.setImageResource(imagen)
        txtVideo?.setText(palabra)

        val fuenteVideo = "android.resource://$packageName/${R.raw.soy_peppa}"
        val uriReproductor = Uri.parse(fuenteVideo)

        vvwAprende.setVideoURI(uriReproductor)
        val mediaController = MediaController(this)
        vvwAprende.setMediaController(mediaController)
    }

    fun buscarVideo(view:View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent,ourRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ourRequestCode && resultCode == RESULT_OK){
            val videoUri = data?.data
            vvwAprende.setVideoURI(videoUri)
        }
    }

    fun reproducirVideo(view:View){
        vvwAprende.start()
    }

    fun irInicio(view: View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }
}