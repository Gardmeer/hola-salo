package com.gardmeer.hellos

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

class PlayerActivity : AppCompatActivity((R.layout.activity_player)) {
    private lateinit var vvwAprende : VideoView
    private lateinit var imvAprende : ImageView
    private lateinit var imvAprendeMini : ImageView
    private lateinit var givYahoo : ImageView
    private lateinit var scrPalabra: ScrollView
    private lateinit var rltVideo : RelativeLayout
    private lateinit var txtVideo : TextView
    private lateinit var sp : SoundPool
    private var sonido = 0
    private var noPic = false
    private var noVid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val palabra = intent.getStringExtra("palabra")

        vvwAprende=findViewById(R.id.vvwAprende)
        imvAprende=findViewById(R.id.imvAprende)
        imvAprendeMini=findViewById(R.id.imvAprendeMini)
        givYahoo=findViewById(R.id.givYahoo)
        scrPalabra=findViewById(R.id.scrPalabra)
        rltVideo=findViewById(R.id.rltVideo)
        txtVideo=findViewById(R.id.txtVideo)

        cargarVideo(palabra)
        sp = SoundPool(1,AudioManager.STREAM_MUSIC,1)
        sonido = sp.load(this,R.raw.yay,1)
    }

    fun reproducirVideo(view:View){
        cambiarVista()
        givYahoo.isVisible =false
        imvAprende.isVisible =false
        sp.autoPause()
        vvwAprende.seekTo(0)
        vvwAprende.start()
    }

    fun regresarPalabra(view:View){
        cambiarVista()
        if (noPic){imvAprende.isVisible =false}
        else {imvAprende.isInvisible =false}
        vvwAprende.pause()
    }

    fun yahoo(view:View){
        if(!noVid) {
            givYahoo.isVisible = !givYahoo.isVisible
            givYahoo.setImageResource(R.drawable.saloyahoo)

            if (noPic) {imvAprende.isVisible = !imvAprende.isVisible}
            else {imvAprende.isInvisible = !imvAprende.isInvisible}
            if (givYahoo.isVisible) {sp.play(sonido,1f,1f,1,0,1f)}
            else {sp.autoPause()}
        }
    }

    private fun cargarVideo(cargarPalabra: String?){
        val pref = getSharedPreferences(cargarPalabra, Context.MODE_PRIVATE)
        var imagen = pref.getString("uriimagen","")!!.toUri()
        var video = pref.getString("urivideo","")!!.toUri()

        try {val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,imagen)
        } catch (e:Exception){
            noPic=true
            imagen="".toUri()
        }
        if(imagen.toString()==R.drawable.nopicmini.toString()||noPic){
            imvAprende.isGone = true
            imvAprendeMini.isGone = true
        }

        try {val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,video)
        } catch (e:Exception){
            noVid=true
            video="".toUri()
            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.not_found))
            builder.setMessage(resources.getString(R.string.change_video))
            builder.create()
            builder.show()
        }
        imvAprende.setImageURI(imagen)
        imvAprendeMini.setImageURI(imagen)
        txtVideo.text = cargarPalabra
        vvwAprende.setVideoURI(video)
        val mc = MediaController(this)
        vvwAprende.setMediaController(mc)
        vvwAprende.setOnPreparedListener {it.isLooping=true}
    }

    private fun cambiarVista(){
        scrPalabra.isVisible=!scrPalabra.isVisible
        rltVideo.isVisible=!rltVideo.isVisible
    }

    override fun onStop() {
        super.onStop()
        sp.autoPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        cambiarVista()
        if (noPic){imvAprende.isVisible =false}
        else {imvAprende.isInvisible =false}
        vvwAprende.pause()
        recreate()
    }
}
