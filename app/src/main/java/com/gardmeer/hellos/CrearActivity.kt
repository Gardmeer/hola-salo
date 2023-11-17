package com.gardmeer.hellos

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.core.view.isVisible

class CrearActivity : AppCompatActivity() {

    var txtNombre: EditText?=null
    var txtBloqueado : TextView?=null
    var txtPalabra : TextView?=null
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
        txtBloqueado=findViewById(R.id.txtBloqueado)
        txtPalabra=findViewById(R.id.txtPalabra)
        imvImagen=findViewById(R.id.imvImagen)
        imvVideo=findViewById(R.id.imvVideo)
        vvwVideo=findViewById(R.id.vvwVideo)
    }

    fun buscarImagen(view:View){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent,imagenRC)
    }

    fun buscarVideo (view:View){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "video/*"
        startActivityForResult(intent,videoRC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imagenRC && resultCode == RESULT_OK) {
            val imagenUri = data?.data
            getContentResolver().takePersistableUriPermission(imagenUri!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            imvImagen?.setImageURI(imagenUri)
            imvImagen?.isVisible=true
            uriImagen = imagenUri.toString()
        }
        else if (requestCode == videoRC && resultCode == RESULT_OK) {
            val videoUri = data?.data
            getContentResolver().takePersistableUriPermission(videoUri!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            vvwVideo?.setVideoURI(videoUri)
            imvVideo?.isVisible=true
            uriVideo = videoUri.toString()
        }
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

            val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
            val editarL = prefL.edit()
            val lista = prefL.getStringSet("addlista",sortedSetOf<String?>())
            val reciente = prefL.getStringSet("reclista",sortedSetOf<String?>())
            val buLista = lista

            editarL.remove("addlista")
            editarL.remove("reclista")
            editarL.apply()

            buLista!!.add(palabra)
            editarL.putStringSet("addlista",buLista)

            var buReciente = sortedSetOf<String?>()

            if (reciente!!.size >0){
                val buPalabra = reciente.elementAt(0)
                buReciente = sortedSetOf(palabra,buPalabra)
            } else {
                buReciente = sortedSetOf(palabra)
            }

            editarL.putStringSet("reclista",buReciente)
            editarL.apply()

            Toast.makeText(this, "Palabra $palabra guardada!", Toast.LENGTH_LONG).show()
            val iBl = Intent(this,BibliotecaActivity::class.java)
            startActivity(iBl)
        }
    }

        fun irInicio(view:View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }
}