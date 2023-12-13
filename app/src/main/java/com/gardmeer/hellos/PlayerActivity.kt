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
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout

class PlayerActivity : AppCompatActivity((R.layout.activity_player)) {
    private lateinit var vvwAprende : VideoView
    private lateinit var imvAprende : ImageView
    private lateinit var imvAprendeMini : ImageView
    private lateinit var givYahoo : ImageView
    private lateinit var rlActividad: RelativeLayout
    private lateinit var scrPalabra: ScrollView
    private lateinit var rltVideo : RelativeLayout
    private lateinit var txtVideo : TextView
    private lateinit var dlMenu : DrawerLayout
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
        rlActividad=findViewById(R.id.rlActividad)
        scrPalabra=findViewById(R.id.scrPalabra)
        rltVideo=findViewById(R.id.rltVideo)
        txtVideo=findViewById(R.id.txtVideo)
        dlMenu=findViewById(R.id.dlMenu)

        cargarVideo(palabra)
        sp = SoundPool(1,AudioManager.STREAM_MUSIC,1)
        sonido = sp.load(this,R.raw.yay,1)
    }

    fun reproducirVideo(view:View){
        rlActividad.isVisible=false
        scrPalabra.isVisible=false
        rltVideo.isVisible=true
        givYahoo.isVisible =false
        imvAprende.isVisible =false
        sp.autoPause()
        vvwAprende.start()
    }

    fun regresarPalabra(view:View){
        rlActividad.isVisible=true
        scrPalabra.isVisible=true
        rltVideo.isVisible=false
        if (noPic){imvAprende.isVisible =false}
        else {imvAprende.isInvisible =false}

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

    fun modificarPalabra(view:View){
        val palabra = txtVideo.text.toString()
        val mPa = Intent(this,CrearActivity::class.java)
        mPa.putExtra("palabra",palabra)
        startActivity(mPa)
    }

    fun eliminarPalabra(view:View){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.delete_cancel) + " ${txtVideo.text}")
            .setPositiveButton(R.string.accept
            ) { _, _ ->
                val palabra = txtVideo.text.toString()
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

                Toast.makeText(applicationContext, resources.getString(R.string.word_deleted,palabra), Toast.LENGTH_LONG).show()

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
        val mediaController = MediaController(this)
        vvwAprende.setMediaController(mediaController)
    }

    fun verMenu(view:View){
        dlMenu.openDrawer(GravityCompat.START)
    }

    fun irInicio(view:View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }

    override fun onStop() {
        super.onStop()
        sp.autoPause()
    }
}
