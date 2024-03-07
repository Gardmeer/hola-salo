package com.gardmeer.hellos

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.core.view.isGone

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PlayerFragment : Fragment() {

    private lateinit var vvwAprende : VideoView
    private lateinit var imvAprendeMini : ImageView
    private var vista: View? = null
    private var param1: String? = null
    private var param2: String? = null

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

        vvwAprende=vista!!.findViewById(R.id.vvwAprende)
        imvAprendeMini=vista!!.findViewById(R.id.imvAprendeMini)

        val pref = this.activity?.getSharedPreferences(param1, Context.MODE_PRIVATE)
        val imagen = pref?.getString("uriimagen","")!!.toUri()
        val video = pref.getString("urivideo","")!!.toUri()

        try {imvAprendeMini.setImageURI(imagen)
        } catch (e:Exception){
            imvAprendeMini.setImageResource(R.drawable.nopicmini)
            imvAprendeMini.isGone=true
        }
        vvwAprende.setVideoURI(video)
        val mc = MediaController(this.activity)
        vvwAprende.setMediaController(mc)
        vvwAprende.setBackgroundColor(resources.getColor(R.color.celeste))
        vvwAprende.seekTo(0)
        vvwAprende.setOnPreparedListener {it.isLooping=true
            vvwAprende.setBackgroundColor(Color.TRANSPARENT)}
        vvwAprende.start()
        return vista
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