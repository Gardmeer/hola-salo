package com.gardmeer.hellos

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

class PlayerActivity : AppCompatActivity((R.layout.activity_player)) {
    private lateinit var frPlayer : FrameLayout
    private lateinit var imvAprende : ImageView
    private lateinit var givYahoo : ImageView
    private lateinit var txtVideo : TextView
    private lateinit var sp : SoundPool
    private var parametroPalabra = ""
    private var sonido = 0
    private var noPic = false
    private var noVid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val palabra = intent.getStringExtra("palabra")
        if (palabra != null) {
            parametroPalabra = palabra
        }

        frPlayer=findViewById(R.id.frPlayer)
        imvAprende=findViewById(R.id.imvAprende)
        givYahoo=findViewById(R.id.givYahoo)
        txtVideo=findViewById(R.id.txtVideo)

        cargarVideo(palabra)
        sp = SoundPool(1,AudioManager.STREAM_MUSIC,1)
        sonido = sp.load(this,R.raw.yay,1)
    }

    fun reproducirVideo(view:View){
        if(!noVid) {
            val playerInstance = PlayerFragment.newInstance(parametroPalabra,"param")
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.frPlayer,playerInstance)
            transaction.addToBackStack(null)
            transaction.commit()
            sp.autoPause()
            if(noPic){imvAprende.isGone = true}
            else {imvAprende.isVisible=true}
            givYahoo.isVisible=false
        }
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
        txtVideo.text = cargarPalabra
    }

    override fun onStop() {
        super.onStop()
        sp.autoPause()
    }
}
