package com.gardmeer.hellos

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TPalabraFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var imvPerCamara: ImageView? = null
    private var imvPerAlma: ImageView? = null
    private var vista: View? = null

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
        vista = inflater.inflate(R.layout.fragment_t_palabra, container, false)

        imvPerCamara=vista?.findViewById(R.id.imvPerCamara)
        imvPerAlma=vista?.findViewById(R.id.imvPerAlma)

        if (Build.VERSION.SDK_INT >= 29){
            imvPerCamara?.setImageResource(R.drawable.tcamaranew)
            imvPerAlma?.setImageResource(R.drawable.talmacenanew)
        }

        return vista
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TPalabraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}