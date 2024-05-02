package com.gardmeer.hellos

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
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
import java.io.File

class PlayerActivity : AppCompatActivity((R.layout.activity_player)) {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var soundPlayer: SoundPool
    private lateinit var palabra: String
    private var sonido = 0
    private var noPic = false
    private var noVid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        palabra = intent.getStringExtra("palabra").orEmpty()

        lifecycleScope.launch {
            val imagen = consultarDato(palabra+"uriImagen")
            val video = consultarDato(palabra+"uriVideo")
            cargarVideo(palabra,video,imagen)
        }
        soundPlayer = SoundPool.Builder().build()
        sonido = soundPlayer.load(this,R.raw.yay,1)

        binding.btnReproducir.setOnClickListener {
            reproducirVideo()
        }
        binding.btnYahoo.setOnClickListener {
            yahoo()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (!binding.frPlayer.isGone){
                    if (noPic){
                        binding.imvAprende.isGone=true
                    } else{
                        binding.imvAprende.isVisible=true
                    }
                    binding.givYahoo.isVisible=false
                    binding.llBotones.isGone=false
                    binding.frPlayer.isGone=true
                } else {
                    finish()
                }
            }
        })
    }

    private fun reproducirVideo(){
        if(!noVid) {
            val playerInstance = PlayerFragment.newInstance(palabra,"param")
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.frPlayer,playerInstance)
            transaction.replace(R.id.frPlayer,playerInstance)
            transaction.addToBackStack(null)
            transaction.commit()
            soundPlayer.autoPause()
            binding.frPlayer.isGone=false
            binding.llBotones.isGone=true
        }
    }

    private fun yahoo(){
        if(!noVid) {
            binding.givYahoo.isVisible = !binding.givYahoo.isVisible
            binding.givYahoo.setImageResource(R.drawable.saloyahoo)
            if (noPic) {
                binding.imvAprende.isVisible = !binding.imvAprende.isVisible
            } else {
                binding.imvAprende.isInvisible = !binding.imvAprende.isInvisible
            }
            if (binding.givYahoo.isVisible) {
                soundPlayer.play(sonido,1f,1f,1,0,1f)
            } else {
                soundPlayer.autoPause()
            }
        }
    }

    private fun cargarVideo(cargarPalabra: String?, cargarVideo: String?, cargarImagen: String?){
        val imgFile = File(cargarImagen!!)
        if(!imgFile.exists()){
            noPic=true
        }
        if(cargarImagen==R.drawable.nopicmini.toString()||noPic){
            binding.imvAprende.isGone = true
        }
        val vidFile = File(cargarVideo!!)
        if(!vidFile.exists()){
            noVid=true
            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.not_found))
            builder.setMessage(resources.getString(R.string.change_video))
            builder.create()
            builder.show()
        }
        binding.imvAprende.setImageURI(cargarImagen.toUri())
        binding.txtVideo.text = cargarPalabra
    }

    private suspend fun consultarDato(nombre:String): String? {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(nombre)]
    }

    override fun onStop() {
        super.onStop()
        soundPlayer.autoPause()
    }
}