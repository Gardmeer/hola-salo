package com.gardmeer.hellos

import android.content.ActivityNotFoundException
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.forEach
import androidx.core.view.isGone
import androidx.core.view.isVisible
import java.util.TreeSet

class CrearActivity : AppCompatActivity(R.layout.activity_crear) {

    private lateinit var txtNombre : EditText
    private lateinit var txtCategoria: EditText
    private lateinit var txtBloqueado : TextView
    private lateinit var txtPalabra : TextView
    private lateinit var imvImagen: ImageView
    private lateinit var imvVideo: ImageView
    private lateinit var vvwVideo: VideoView
    private lateinit var spCategoria: Spinner
    private val listaCategorias = ArrayList<String>()
    private var posCheck = 0
    private var uriImagen: String? = ""
    private var uriVideo: String? = ""
    private var categoria :String? = ""
    private var permisosOk = false
    private var permisosRq = false
    private var buscando = false
    private val carpetaRaiz = "ArchivosApp/"
    private val rutaAlmacenamiento = carpetaRaiz+"HolaSalo/"
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private val codigos = arrayOf(10,20,123,321,124,324) // Camara, Escritura, Imagen, Video, CapImagen, CapVideo
    private val permisos = arrayOf("android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE","android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_IMAGES")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mPalabra = intent.getStringExtra("palabra")

        txtNombre=findViewById(R.id.txtNombre)
        txtBloqueado=findViewById(R.id.txtBloqueado)
        txtPalabra=findViewById(R.id.txtPalabra)
        txtCategoria=findViewById(R.id.txtCategoria)
        imvImagen=findViewById(R.id.imvImagen)
        imvVideo=findViewById(R.id.imvVideo)
        vvwVideo=findViewById(R.id.vvwVideo)
        spCategoria=findViewById(R.id.spCategoria)

        cargarCategorias()
        clickCategorias()

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
                        try{startActivityForResult(intent,codigos[4])}
                        catch (ex:ActivityNotFoundException){
                            Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
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
                        try{startActivityForResult(intent,codigos[5])}
                        catch (ex:ActivityNotFoundException){
                            Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
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
        val categorias =  (txtCategoria.text.toString().lowercase()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString() }

        if (palabra == ""){
            Toast.makeText(this,resources.getString(R.string.reminder_word),Toast.LENGTH_LONG).show()
        }
        else if(uriVideo == ""){
            Toast.makeText(this,resources.getString(R.string.reminder_video),Toast.LENGTH_LONG).show()
        }
        else if(posCheck == 2 &&  categorias== ""){
            Toast.makeText(this,resources.getString(R.string.reminder_category),Toast.LENGTH_LONG).show()
        }
        else if(uriImagen == "") {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.load))
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    cargarImagen(view)
                }
                .setNegativeButton(R.string.no
                ) { _, _ ->
                    uriImagen=R.drawable.nopicmini.toString()
                    imvImagen.setImageResource(R.drawable.nopicmini)
                    imvImagen.isVisible=true
                }
            builder.create()
            builder.show()

        } else{
            val pref = getSharedPreferences(palabra, Context.MODE_PRIVATE)
            val editor = pref.edit()
            editor.putString("urivideo", uriVideo)
            editor.putString("uriimagen", uriImagen)
            editor.putString("categorias", categorias)
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

            Toast.makeText(this, resources.getString(R.string.word_saved,palabra), Toast.LENGTH_LONG).show()
            finish()
        }
    }


    private fun crearImagen() {
        val uri: Uri = if (Build.VERSION.SDK_INT >= 29){
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
        val uri: Uri = if (Build.VERSION.SDK_INT >= 29){
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
        categoria = pref.getString("categorias","")

        try {val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uriImagen?.toUri())
        } catch (e:Exception){uriImagen=""}

        try {val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uriVideo?.toUri())
        } catch (e:Exception){uriVideo=""}

        if(uriImagen!=""){
            imvImagen.setImageURI(uriImagen!!.toUri())
            imvImagen.isVisible = true
        }
        if(uriVideo!=""){
            vvwVideo.setVideoURI(uriVideo!!.toUri())
            imvVideo.isVisible = true
        }
        if(categoria!=""){
            txtCategoria.setText(categoria)
            txtCategoria.isVisible = true
        }
        txtBloqueado.text = cargaPalabra
        txtNombre.setText(cargaPalabra)
        txtPalabra.setText(R.string.modify)
        txtBloqueado.isVisible = true
        txtNombre.isVisible = false

        for (i in 0..listaCategorias.size-1){
            if(spCategoria.getItemAtPosition(i)==categoria){
                spCategoria.setSelection(i)
            }
        }
    }

    private fun evaluarPermisos(){
        if (ContextCompat.checkSelfPermission(this, permisos[0]) == PackageManager.PERMISSION_GRANTED&&
            (((ContextCompat.checkSelfPermission(this, permisos[1]) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(this, permisos[2]) == PackageManager.PERMISSION_GRANTED)) ||
                    ((ContextCompat.checkSelfPermission(this, permisos[3]) == PackageManager.PERMISSION_GRANTED) &&
                            (ContextCompat.checkSelfPermission(this, permisos[4]) == PackageManager.PERMISSION_GRANTED)))){
            permisosOk = true
        }

        for (i in permisos.indices) {
            if (ContextCompat.checkSelfPermission(this, permisos[i]) == PackageManager.PERMISSION_DENIED){
                if(!permisosRq&&!permisosOk){
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(resources.getString(R.string.permissions))
                    builder.setMessage(resources.getString(R.string.suggestion))
                        .setPositiveButton(R.string.accept
                        ) { _, _ ->
                            ActivityCompat.requestPermissions(this, permisos ,codigos[0])
                        }
                    builder.create()
                    builder.show()
                    permisosRq=true
                }
            }
        }
        permisosRq=false
    }

    private fun cargarCategorias(){

        val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
        val lista = prefL.getStringSet("addlista",sortedSetOf<String?>())
        var categoriaTemp = mutableSetOf<String>()

        listaCategorias.add(resources.getString(R.string.category_option))
        listaCategorias.add(resources.getString(R.string.no_category))
        listaCategorias.add(resources.getString(R.string.new_category))

        lista!!.forEach {
            val pref = getSharedPreferences(it, Context.MODE_PRIVATE)
            val categoriaFill = pref.getString("categorias","")
            if (categoriaFill!=""){
                categoriaTemp.add(categoriaFill.toString())
            }
        }
        categoriaTemp.forEach{
            listaCategorias.add(it)
        }

        val adp= ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,listaCategorias)
        spCategoria.adapter=adp
    }
    private fun clickCategorias(){
        spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

                posCheck=position
                if (position>2){
                    txtCategoria.setText(listaCategorias[position])
                }
                else {
                    txtCategoria.setText("")

                    if (position==2){
                        txtCategoria.isVisible=true
                    } else {txtCategoria.isGone=true}
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

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