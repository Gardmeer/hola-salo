package com.gardmeer.hellos

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.gardmeer.hellos.databinding.ActivityCrearBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.TreeSet

class CrearActivity : AppCompatActivity(R.layout.activity_crear) {
    private lateinit var binding: ActivityCrearBinding
    private val listaCategorias = ArrayList<String>()
    private var positionCheck = 0
    private var imagenUri: Uri? = null
    private var uriImagen: String? = ""
    private var uriVideo: String? = ""
    private var permisosOk = false
    private var permisosRq = false
    private var buscandoWeb = false
    private val rutaAlmacenamiento = "ArchivosApp/HolaSalo/"
    private val permisos = arrayOf("android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE","android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_IMAGES")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategorias()
        clickCategorias()

        val mPalabra = intent.getStringExtra("palabra")
        if(mPalabra != null){
            modificarPalabra(mPalabra)
        }
        binding.btnVideo.setOnClickListener {
            cargarVideo()
        }
        binding.btnImagen.setOnClickListener {
            cargarImagen()
        }
        binding.btnGuardar.setOnClickListener {
            guardarPalabra()
        }
    }

    private fun cargarImagen(){
        val listaOpcI = resources.getStringArray(R.array.picture_option)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.load_picture))
        builder.setItems(listaOpcI){ _, posicion ->
            when(posicion){
                0 -> {
                    evaluarPermisos()
                    if(permisosOk){
                        imagenUri = crearImagen()
                        try{
                            capturarImagen.launch(imagenUri)
                            uriImagen = obtenerPath(this,imagenUri!!)
                        } catch (ex:ActivityNotFoundException){
                            Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                1 -> {
                    val palabra =  (binding.txtNombre.text.toString())
                    val buscarWeb = Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q=$palabra&tbm=isch"))
                    startActivity(buscarWeb)
                    buscandoWeb=true
                }
                2 -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type="image/*"
                    galeriaImagen.launch(intent)
                }
            }
        }
        builder.create()
        builder.show()
    }

    private fun cargarVideo (){
        val listaOpcV = resources.getStringArray(R.array.video_option)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.load_video))
        builder.setItems(listaOpcV){ _, posicion ->
            when(posicion){
                0 -> {
                    evaluarPermisos()
                    if(permisosOk){
                        val videoUri = crearVideo()
                        try{
                            capturarVideo.launch(videoUri)
                            uriVideo = obtenerPath(this,videoUri!!)
                            binding.vvwVideo.setVideoURI(videoUri)
                        } catch (ex:ActivityNotFoundException){
                            Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                1 -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type="video/*"
                    galeriaVideo.launch(intent)
                }
            }
        }
        builder.create()
        builder.show()
    }

    private val capturarVideo = registerForActivityResult(ActivityResultContracts.CaptureVideo()){
        if (it){
            binding.imvVideo.isVisible=true
        }
    }

    private val capturarImagen = registerForActivityResult(ActivityResultContracts.TakePicture()){
        if (it){
            binding.imvImagen.setImageURI(imagenUri)
            binding.imvImagen.isVisible=true
        }
    }

    private val galeriaImagen = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        if(result.resultCode == RESULT_OK){
            val uri = result.data?.data
            uriImagen = obtenerPath(this, uri!!)
            binding.imvImagen.setImageURI(uri)
            binding.imvImagen.isVisible=true
        }
    }

    private val galeriaVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == RESULT_OK){
            val uri = result.data?.data
            uriVideo = obtenerPath(this, uri!!)
            binding.imvVideo.isVisible=true
        }
    }

    private fun guardarPalabra(){
        val palabra =  (binding.txtNombre.text.toString().lowercase()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString() }
        val categorias =  (binding.txtCategoria.text.toString().lowercase()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString() }
        if (palabra == ""){
            Toast.makeText(this,resources.getString(R.string.reminder_word),Toast.LENGTH_LONG).show()
        }
        else if(!binding.imvVideo.isVisible){
            Toast.makeText(this,resources.getString(R.string.reminder_video),Toast.LENGTH_LONG).show()
        }
        else if(positionCheck == 2 &&  categorias== ""){
            Toast.makeText(this,resources.getString(R.string.reminder_category),Toast.LENGTH_LONG).show()
        }
        else if(!binding.imvImagen.isVisible) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.load))
                .setPositiveButton(R.string.yes
                ) { _, _ ->
                    cargarImagen()
                }
                .setNegativeButton(R.string.no
                ) { _, _ ->
                    uriImagen="R.drawable.nopicmini"
                    binding.imvImagen.setImageResource(R.drawable.nopicmini)
                    binding.imvImagen.isVisible=true
                }
            builder.create()
            builder.show()
        } else {
            lifecycleScope.launch {
                val lista = consultarLista("lista")?.toMutableSet() ?: mutableSetOf()
                val reciente = consultarLista("reclista")?.toMutableSet() ?: mutableSetOf()
                lista.add(palabra)
                val buReciente: TreeSet<String> = if (reciente.size >0){
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
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(palabra)] = palabra
                    preferences[stringPreferencesKey(palabra+"uriVideo")] = uriVideo!!
                    preferences[stringPreferencesKey(palabra+"uriImagen")] = uriImagen!!
                    preferences[stringPreferencesKey(palabra+"categorias")] = categorias
                    preferences[stringSetPreferencesKey("lista")] = lista
                    preferences[stringSetPreferencesKey("reclista")] = buReciente
                }
            }
            Toast.makeText(this, resources.getString(R.string.word_saved,palabra), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun crearImagen(): Uri? {
        val filo = this.getExternalFilesDirs(rutaAlmacenamiento)
        filo[0].mkdirs()

        val uri: Uri = if (Build.VERSION.SDK_INT >= 29){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val nombreImagen = (System.currentTimeMillis()/1000).toString()+".jpg"
        ContentValues().put(MediaStore.Images.Media.DISPLAY_NAME, nombreImagen)
        ContentValues().put(MediaStore.Images.Media.RELATIVE_PATH, rutaAlmacenamiento)
        return contentResolver.insert(uri, ContentValues())
    }

    private fun crearVideo(): Uri?{
        val uri: Uri = if (Build.VERSION.SDK_INT >= 29){
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val nombreVideo = (System.currentTimeMillis()/1000).toString()+".mp4"
        ContentValues().put(MediaStore.Video.Media.DISPLAY_NAME, nombreVideo)
        ContentValues().put(MediaStore.Video.Media.RELATIVE_PATH, rutaAlmacenamiento)
        return contentResolver.insert(uri, ContentValues())
    }

    private fun obtenerPath(context: Context, uri: Uri): String? {
        val path: String?
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri,arrayOf(MediaStore.Images.Media.DATA),null,null,null)
            cursor?.moveToFirst()
            path = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
                ?.let { cursor.getString(it)}
        } finally {
            cursor?.close()
        }
        return path
    }

    private fun modificarPalabra(cargaPalabra: String){
        var categoria :String?
        lifecycleScope.launch {
            uriImagen = consultarDato(cargaPalabra+"uriImagen")
            uriVideo = consultarDato(cargaPalabra+"uriVideo")
            categoria = consultarDato(cargaPalabra+"categorias")

            val imgFile = File(uriImagen!!)
            if(!imgFile.exists()){
                uriImagen=""
            }
            val vidFile = File(uriVideo!!)
            if(!vidFile.exists()){
                uriVideo=""
            }
            if(uriImagen!=""){
                binding.imvImagen.setImageURI(uriImagen!!.toUri())
                binding.imvImagen.isVisible = true
            }
            if(uriVideo!=""){
                binding.vvwVideo.setVideoURI(uriVideo!!.toUri())
                binding.imvVideo.isVisible = true
            }
            if(categoria!=""){
                binding.txtCategoria.setText(categoria)
                binding.txtCategoria.isVisible = true
            }
            binding.txtBloqueado.text = cargaPalabra
            binding.txtNombre.setText(cargaPalabra)
            binding.txtPalabra.setText(R.string.modify)
            binding.txtBloqueado.isVisible = true
            binding.txtNombre.isVisible = false

            for (i in 0..<listaCategorias.size){
                if(binding.spCategoria.getItemAtPosition(i)==categoria){
                    binding.spCategoria.setSelection(i)
                }
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
                            ActivityCompat.requestPermissions(this, permisos ,123)
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
        val categoriaTemp = mutableSetOf<String>()
        lifecycleScope.launch {
                consultarLista("lista").orEmpty().forEach {
                val categoriaFill = consultarDato(it+"categorias")
                if (categoriaFill!=""){
                    categoriaTemp.add(categoriaFill.toString())
                }
            }
            listaCategorias.add(resources.getString(R.string.category_option))
            listaCategorias.add(resources.getString(R.string.no_category))
            listaCategorias.add(resources.getString(R.string.new_category))
            categoriaTemp.forEach{
                listaCategorias.add(it)
            }
            val adp= ArrayAdapter(this@CrearActivity,android.R.layout.simple_spinner_dropdown_item,listaCategorias)
            binding.spCategoria.adapter=adp
        }
    }

    private fun clickCategorias(){
        binding.spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                positionCheck=position
                if (position>2){
                    binding.txtCategoria.setText(listaCategorias[position])
                } else {
                    binding.txtCategoria.setText("")
                    if (position==2){
                        binding.txtCategoria.isVisible=true
                        binding.txtCategoria.requestFocus()
                    } else {
                        binding.txtCategoria.isGone=true
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
        }
    }

    private suspend fun consultarLista(nombre:String): Set<String>? {
        val preferences = dataStore.data.first()
        return preferences[stringSetPreferencesKey(nombre)]
    }

    private suspend fun consultarDato(nombre:String): String? {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(nombre)]
    }

    override fun onRestart() {
        super.onRestart()
        if (buscandoWeb){
            buscandoWeb=false
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            galeriaImagen.launch(intent)
        }
    }
}