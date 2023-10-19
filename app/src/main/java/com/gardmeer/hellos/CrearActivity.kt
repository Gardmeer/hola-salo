package com.gardmeer.hellos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.core.view.isVisible

class CrearActivity : AppCompatActivity() {

    var imvImagen: ImageView?=null
    var vvwVideo: VideoView?=null
    //var txtUriV: TextView?=null
    //var txtUriI: TextView?=null
    private var imagenRC : Int = 123
    private var videoRC : Int = 321

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear)

        imvImagen=findViewById(R.id.imvImagen)
        vvwVideo=findViewById(R.id.vvwVideo)
        //txtUriI=findViewById(R.id.txtUriI)
        //txtUriV=findViewById(R.id.txtUriV)
    }

    fun buscarImagen(view:View){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //intent.type = "image/*"
        startActivityForResult(intent,imagenRC)
    }

    fun buscarVideo (view:View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent,videoRC)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagenRC && resultCode == RESULT_OK) {
            val imagenUri = data?.data
            //var uriI = ""
            imvImagen?.setImageURI(imagenUri)
            imvImagen?.isVisible=true
            //uriI = imagenUri.toString()
            //txtUriI?.setText(uriI)

        }
        else if (requestCode == videoRC && resultCode == RESULT_OK) {
            val videoUri = data?.data
            //var uriV = ""
            vvwVideo?.setVideoURI(videoUri)
            vvwVideo?.isVisible=true
            //uriV = videoUri.toString()
            //txtUriV?.setText(uriV)
        }
    }

        fun irInicio(view: View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }
}