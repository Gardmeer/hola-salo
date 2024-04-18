package com.gardmeer.hellos

import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.gardmeer.hellos.databinding.ActivityPlayerBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerActivity : AppCompatActivity((R.layout.activity_player)) {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var sp: SoundPool
    private var parametroPalabra = ""
    private var sonido = 0
    private var noPic = false
    private var noVid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val palabra = intent.getStringExtra("palabra")
        if (palabra != null) {
            parametroPalabra = palabra
        }

        cargarVideo(palabra)
        sp = SoundPool(1,AudioManager.STREAM_MUSIC,1)
        sonido = sp.load(this,R.raw.yay,1)

        binding.btnReproducir.setOnClickListener {
            reproducirVideo()
        }

        binding.btnYahoo.setOnClickListener {
            yahoo()
        }
    }

    private fun reproducirVideo(){
        if(!noVid) {
            val playerInstance = PlayerFragment.newInstance(parametroPalabra,"param")
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.frPlayer,playerInstance)
            transaction.replace(R.id.frPlayer,playerInstance)
            transaction.addToBackStack(null)
            transaction.commit()
            sp.autoPause()
            binding.llBotones.isGone=true
        }
    }

    private fun yahoo(){
        if(!noVid) {
            binding.givYahoo.isVisible = !binding.givYahoo.isVisible
            binding.givYahoo.setImageResource(R.drawable.saloyahoo)

            if (noPic) {binding.imvAprende.isVisible = !binding.imvAprende.isVisible}
            else {binding.imvAprende.isInvisible = !binding.imvAprende.isInvisible}
            if (binding.givYahoo.isVisible) {sp.play(sonido,1f,1f,1,0,1f)}
            else {sp.autoPause()}
        }
    }

    private fun cargarVideo(cargarPalabra: String?){
        var imagen : Uri? = null
        var video : Uri? = null
        lifecycleScope.launch {
            imagen = consultarDato(cargarPalabra+"uriImagen")?.toUri()
            video = consultarDato(cargarPalabra+"uriVideo")?.toUri()
        }

        try {val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,imagen)
        } catch (e:Exception){
            noPic=true
            imagen="".toUri()
        }
        if(imagen.toString()==R.drawable.nopicmini.toString()||noPic){
            binding.imvAprende.isGone = true
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
        binding.imvAprende.setImageURI(imagen)
        binding.txtVideo.text = cargarPalabra
    }

    private suspend fun consultarDato(nombre:String): String? {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(nombre)]
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if(noPic){binding.imvAprende.isGone=true}
        else{binding.imvAprende.isVisible=true}
        binding.givYahoo.isVisible=false
        binding.llBotones.isGone=false
    }
    override fun onStop() {
        super.onStop()
        sp.autoPause()
    }
}
