package com.gardmeer.hellos

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.core.view.isVisible

class CrearActivity : AppCompatActivity() {

    var txtNombre: EditText?=null
    var imvImagen: ImageView?=null
    var imvVideo: ImageView?=null
    var vvwVideo: VideoView?=null
    var uriImagen = ""
    var uriVideo = ""
    private var imagenRC : Int = 123
    private var videoRC : Int = 321

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear)

        txtNombre=findViewById(R.id.txtNombre)
        imvImagen=findViewById(R.id.imvImagen)
        imvVideo=findViewById(R.id.imvVideo)
        vvwVideo=findViewById(R.id.vvwVideo)
    }

    fun buscarImagen(view:View){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,imagenRC)
    }

    fun buscarVideo (view:View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent,videoRC)
    }

    fun guardarPalabra(view:View){
        val palabra =  (txtNombre?.text.toString().lowercase()).replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

        if (uriImagen == ""){
            Toast.makeText(this,"Selecciona una Imagen",Toast.LENGTH_LONG).show()
        }
        else if(uriVideo == ""){
                Toast.makeText(this,"Selecciona un Video",Toast.LENGTH_LONG).show()
        }
        else if(palabra == "") {
                    Toast.makeText(this, "Escribe la Palabra que aprenderás", Toast.LENGTH_LONG).show()
        }
        else{
            val pref = getSharedPreferences(palabra, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("urivideo", uriVideo)
            editor.putString("uriimagen", uriImagen)
            editor.apply()
            Toast.makeText(this, "Nueva Palabra Guardada!", Toast.LENGTH_LONG).show()
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagenRC && resultCode == RESULT_OK) {
            val imagenUri = data?.data
            imvImagen?.setImageURI(imagenUri)
            imvImagen?.isVisible=true
            uriImagen = imagenUri.toString()
        }
        else if (requestCode == videoRC && resultCode == RESULT_OK) {
            val videoUri = data?.data
            vvwVideo?.setVideoURI(videoUri)
            imvVideo?.isVisible=true
            uriVideo = videoUri.toString()
        }
    }

        fun irInicio(view: View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }
}