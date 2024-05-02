package com.gardmeer.hellos

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PlayerFragment : Fragment() {

    private lateinit var vvwAprende: VideoView
    private lateinit var imvAprendeMini: ImageView
    private var vista: View? = null
    private var param1: String? = null
    private var param2: String? = null
    private var imagen : Uri? = null
    private var video : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vista = inflater.inflate(R.layout.fragment_player, container, false)

        val mc = MediaController(this.activity)
        vvwAprende=vista!!.findViewById(R.id.vvwAprende)
        imvAprendeMini=vista!!.findViewById(R.id.imvAprendeMini)

        lifecycleScope.launch {
            imagen = consultarDato(param1+"uriImagen")?.toUri()
            video = consultarDato(param1+"uriVideo")?.toUri()
            try {
                imvAprendeMini.setImageURI(imagen)
            } catch (e:Exception){
                imvAprendeMini.setImageResource(R.drawable.nopicmini)
                imvAprendeMini.isGone=true
            }
            vvwAprende.setVideoURI(video)
            vvwAprende.setMediaController(mc)
            vvwAprende.setBackgroundColor(ContextCompat.getColor(requireActivity().applicationContext,R.color.celeste))
            vvwAprende.seekTo(0)
            vvwAprende.setOnPreparedListener {it.isLooping=true
                vvwAprende.setBackgroundColor(Color.TRANSPARENT)}
            vvwAprende.start()
        }

        return vista

    }
    private suspend fun consultarDato(nombre:String): String? {
        val preferences = this.activity?.dataStore?.data?.first()
        return preferences?.get(stringPreferencesKey(nombre))
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}