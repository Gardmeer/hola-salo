package com.gardmeer.hellos

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import java.util.TreeSet

class CrearActivity : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtBloqueado : TextView
    private lateinit var txtPalabra : TextView
    private lateinit var imvImagen: ImageView
    private lateinit var imvVideo: ImageView
    private lateinit var vvwVideo: VideoView
    private var uriImagen: String? = ""
    private var uriVideo: String? = ""
    private var permisosOk = false
    private var buscando = false
    private val carpetaRaiz = "ArchivosApp/"
    private val rutaAlmacenamiento = carpetaRaiz+"HolaSalo/"
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private val codigos = arrayOf(10,20,123,321,124,324) // Camara, Escritura, Imagen, Video, CapImagen, CapVideo
    private val permisos = arrayOf("android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_IMAGES","android.permission.READ_MEDIA_AUDIO")

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
        builder.setItems(lista){ _, posicion ->
            when(posicion){
                0 -> {
                    evaluarPermisos()

                    if(permisosOk){
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        crearImagen()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
                        if (intent.resolveActivity(packageManager) != null){
                            startActivityForResult(intent,codigos[4])
                        }
                    }
                }
               1 -> {
                   val palabra =  (txtNombre.text.toString())
                   val buscar = Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q=$palabra&tbm=isch"))
                   startActivity(buscar)
                   buscando=true
                }
                2 -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type = "image/*"
                    startActivityForResult(intent,codigos[2])
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
        builder.setItems(lista){ _, posicion ->
            when(posicion){
                0 -> {
                    evaluarPermisos()

                    if(permisosOk){
                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        crearVideo()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,videoUri)
                        if (intent.resolveActivity(packageManager) != null){
                            startActivityForResult(intent,codigos[5])
                        }
                    }
                }
                1 -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type = "video/*"
                    startActivityForResult(intent,codigos[3])
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
                codigos[2] -> {
                    imageUri = data?.data
                    contentResolver.takePersistableUriPermission(imageUri!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    imvImagen.setImageURI(imageUri)
                    imvImagen.isVisible=true
                    uriImagen = imageUri.toString()
                }
                codigos[3] -> {
                    videoUri = data?.data
                    contentResolver.takePersistableUriPermission(videoUri!!,Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    vvwVideo.setVideoURI(videoUri)
                    imvVideo.isVisible=true
                    uriVideo = videoUri.toString()
                }
                codigos[4] -> {
                    imvImagen.setImageURI(imageUri)
                    imvImagen.isVisible=true
                    uriImagen = imageUri.toString()
                }
                codigos[5] -> {
                    vvwVideo.setVideoURI(videoUri)
                    imvVideo.isVisible=true
                    uriVideo = videoUri.toString()
                }
            }
        }
    }

    fun guardarPalabra(view:View){
        val palabra =  (txtNombre.text.toString().lowercase()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString() }

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

            val buReciente: TreeSet<String> = if (reciente!!.size >0){
                val buPalabra: String = if(reciente.size == 2){
                    if(reciente.elementAt(0) == palabra){
                        reciente.elementAt(1)
                    } else {
                        reciente.elementAt(0)
                    }
                } else {
                    reciente.elementAt(0)
                }
                sortedSetOf(palabra,buPalabra)
            } else {
                sortedSetOf(palabra)
            }

            editarL.putStringSet("reclista",buReciente)
            editarL.apply()

            Toast.makeText(this, "Palabra $palabra guardada!", Toast.LENGTH_LONG).show()
            val iBl = Intent(this,BibliotecaActivity::class.java)
            startActivity(iBl)
        }
    }


    private fun crearImagen() {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val nombreImagen = (System.currentTimeMillis()/1000).toString()+".jpg"
        ContentValues().put(MediaStore.Images.Media.DISPLAY_NAME, nombreImagen)
        ContentValues().put(MediaStore.Images.Media.RELATIVE_PATH, rutaAlmacenamiento)
        imageUri = contentResolver.insert(uri, ContentValues())
    }

    private fun crearVideo(){
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val nombreVideo = (System.currentTimeMillis()/1000).toString()+".mp4"
        ContentValues().put(MediaStore.Video.Media.DISPLAY_NAME, nombreVideo)
        ContentValues().put(MediaStore.Video.Media.RELATIVE_PATH, rutaAlmacenamiento)
        videoUri = contentResolver.insert(uri, ContentValues())
    }

    private fun modificarPalabra(cargaPalabra: String){
        val pref = getSharedPreferences(cargaPalabra, Context.MODE_PRIVATE)
        uriImagen = pref.getString("uriimagen","")
        uriVideo = pref.getString("urivideo","")

        imvImagen.setImageURI(uriImagen!!.toUri())
        vvwVideo.setVideoURI(uriVideo!!.toUri())
        txtBloqueado.text = cargaPalabra
        txtNombre.setText(cargaPalabra)
        txtPalabra.setText(R.string.modify)
        txtBloqueado.isVisible = true
        txtNombre.isVisible = false
        imvImagen.isVisible = true
        imvVideo.isVisible = true
    }

    fun irInicio(view:View){
        val iIn = Intent(this,MainActivity::class.java)
        startActivity(iIn)
    }

    private fun evaluarPermisos(){
        for (i in permisos.indices) {
            if (ContextCompat.checkSelfPermission(this, permisos[i]) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, permisos ,codigos[i])
            }
        }
        if ((shouldShowRequestPermissionRationale(permisos[0]))||
            (shouldShowRequestPermissionRationale(permisos[1]))){
            val builder = AlertDialog.Builder(this)
            builder.setTitle(resources.getString(R.string.permissions))
            builder.setMessage(resources.getString(R.string.suggestion))
                .setPositiveButton(R.string.accept
                ) { _, _ ->
                    ActivityCompat.requestPermissions(this, permisos ,codigos[0])
                }
        }
        if ((ContextCompat.checkSelfPermission(this, permisos[0]) == PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this, permisos[1]) == PackageManager.PERMISSION_GRANTED)){
            permisosOk = true
        }
    }

    override fun onRestart() {
        super.onRestart()

        if (buscando){
            buscando=false
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent,codigos[2])
        }
    }
}