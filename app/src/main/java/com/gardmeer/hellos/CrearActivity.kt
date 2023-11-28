package com.gardmeer.hellos

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import java.io.File

class CrearActivity : AppCompatActivity() {

    var txtNombre: EditText?=null
    var txtBloqueado : TextView?=null
    var txtPalabra : TextView?=null
    var imvImagen: ImageView?=null
    var imvVideo: ImageView?=null
    var vvwVideo: VideoView?=null
    var uriImagen: String? = ""
    var uriVideo: String? = ""
    var buscando: Boolean = false
    val carpetaRaiz = "ArchivosApp/"
    val rutaAlmacenamiento = carpetaRaiz+"HolaSalo"
    var imageUri: Uri? = null
    var videoUri: Uri? = null
    private var codigoCamara = 10
    private var imagenRC = 123
    private var videoRC = 321
    private var imagenRCapture = 124
    private var videoRCapture = 324

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear)

        val mPalabra = intent.getStringExtra("palabra")

        txtNombre=findViewById(R.id.txtNombre)
        txtBloqueado=findViewById(R.id.txtBloqueado)
        txtPalabra=findViewById(R.id.txtPalabra)
        imvImagen=findViewById(R.id.imvImagen)
        imvVideo=findViewById(R.id.imvVideo)
        vvwVideo=findViewById(R.id.vvwVideo)

        if(mPalabra != null){modificarPalabra(mPalabra)}
    }

    fun cargarImagen(view:View){
        val lista = resources.getStringArray(R.array.picture_option)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.load_picture))
        builder.setItems(lista){ id, posicion ->
            when(posicion){
                0 -> {
                    if (ContextCompat.checkSelfPermission(this,
                            "android.permission.CAMERA") == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(this, arrayOf("android.permission.CAMERA") ,codigoCamara)
                    } else {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        val direccionImagen = crearImagen()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,direccionImagen)
                        if (intent.resolveActivity(packageManager) != null){
                            startActivityForResult(intent,imagenRCapture)
                        }
                    }
                }
                1 -> {
                    val buscar = Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/imghp?hl=es&ogbl"))
                    startActivity(buscar)
                    buscando=true
                }
                2 -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type = "image/*"
                    startActivityForResult(intent,imagenRC)
                }
            }
        }

        builder.create()
        builder.show()
    }

    fun cargarVideo (view:View){
        val lista = resources.getStringArray(R.array.video_option)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.load_video))
        builder.setItems(lista){ id, posicion ->
            when(posicion){
                0 -> {
                    if (ContextCompat.checkSelfPermission(this,
                            "android.permission.CAMERA") == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(this, arrayOf("android.permission.CAMERA") ,codigoCamara)
                    } else {
                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        val direccionVideo = crearVideo()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,direccionVideo)
                        if (intent.resolveActivity(packageManager) != null){
                            startActivityForResult(intent,videoRCapture)
                        }
                    }
                }
                1 -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type = "video/*"
                    startActivityForResult(intent,videoRC)
                }
            }
        }

        builder.create()
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            when(requestCode){
                imagenRC -> {
                    imageUri = data?.data
                    contentResolver.takePersistableUriPermission(imageUri!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    imvImagen?.setImageURI(imageUri)
                    imvImagen?.isVisible=true
                    uriImagen = imageUri.toString()
                }
                videoRC -> {
                    videoUri = data?.data
                    contentResolver.takePersistableUriPermission(videoUri!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    vvwVideo?.setVideoURI(videoUri)
                    imvVideo?.isVisible=true
                    uriVideo = videoUri.toString()
                }
                imagenRCapture -> {
                    imvImagen?.setImageURI(imageUri)
                    imvImagen?.isVisible=true
                    uriImagen = imageUri.toString()
                }
                videoRCapture -> {
                    vvwVideo?.setVideoURI(videoUri)
                    imvVideo?.isVisible=true
                    uriVideo = videoUri.toString()
                }
            }
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
                var buPalabra = ""
                if(reciente.size == 2){
                    if(reciente.elementAt(0) == palabra){
                        buPalabra = reciente.elementAt(1)
                    } else {buPalabra = reciente.elementAt(0)}
                } else {
                    buPalabra = reciente.elementAt(0)
                }
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

    fun modificarPalabra(cargaPalabra: String){
        val pref = getSharedPreferences(cargaPalabra, Context.MODE_PRIVATE)
        uriImagen = pref.getString("uriimagen","")
        uriVideo = pref.getString("urivideo","")

        imvImagen?.setImageURI(uriImagen!!.toUri())
        vvwVideo?.setVideoURI(uriVideo!!.toUri())
        txtBloqueado?.text = cargaPalabra
        txtNombre?.setText(cargaPalabra)
        txtPalabra?.setText(R.string.modify)
        txtBloqueado?.isVisible = true
        txtNombre?.isVisible = false
        imvImagen?.isVisible = true
        imvVideo?.isVisible = true
    }

    private fun crearImagen(): Uri? {
        var uri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val nombreImagen = (System.currentTimeMillis()/1000).toString()+".jpg"
        ContentValues().put(MediaStore.Images.Media.DISPLAY_NAME, nombreImagen)
        ContentValues().put(MediaStore.Images.Media.RELATIVE_PATH, rutaAlmacenamiento)
        val uriFinal = contentResolver.insert(uri, ContentValues())
        imageUri = uriFinal
        return uriFinal
    }

    private fun crearVideo(): Uri? {
        var uri: Uri? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val nombreVideo = (System.currentTimeMillis()/1000).toString()+".mp4"
        ContentValues().put(MediaStore.Video.Media.DISPLAY_NAME, nombreVideo)
        ContentValues().put(MediaStore.Video.Media.RELATIVE_PATH, rutaAlmacenamiento)
        val uriFinal = contentResolver.insert(uri, ContentValues())
        videoUri = uriFinal
        return uriFinal
    }

    fun irInicio(view:View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }

    override fun onRestart() {
        super.onRestart()

        if (buscando==true){
            buscando=false
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent,imagenRC)
        }
    }
}