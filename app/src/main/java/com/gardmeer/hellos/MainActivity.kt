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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gardmeer.hellos.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "USER_PREFERENCES_NAME")
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding
    var listDatos = ArrayList<NuevaPalabra>()
    private var uriImagen: String? = null
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var permisosOk = false
    private var permisosRq = false
    private val rutaAlmacenamiento = "ArchivosApp/HolaSalo/"
    private val codigos = arrayOf(10,20,123,321,124,324) // Camara, Escritura, Imagen, Video, CapImagen, CapVideo
    private val permisos = arrayOf("android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE","android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_IMAGES")
    override fun onCreate(savedInstanceState: Bundle?) {
        cargarTutorial()
        setTheme(R.style.Theme_HelloS)
        Thread.sleep(500)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nvNavegacion.itemIconTintList = null

        lifecycleScope.launch {
            consultarLista("reclista")?.forEach {
                uriImagen = consultarDato(it +"uriImagen")
                elementoLista(it,uriImagen)
            }
        }

        binding.nvNavegacion.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mnVideo -> {
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
                R.id.mnImagen -> {
                    evaluarPermisos()

                    if(permisosOk){
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        crearImagen()
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
                        try{startActivityForResult(intent,codigos[4])}
                        catch (ex: ActivityNotFoundException){
                            Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                R.id.mnWeb -> {
                    startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/imghp")))
                }
                R.id.mnSalir -> {
                    binding.dlMenu.closeDrawer(GravityCompat.START)}
            }
            true
        }

        binding.btnMenu.setOnClickListener {
            binding.dlMenu.openDrawer(GravityCompat.START)
        }

        binding.btnAyuda.setOnClickListener {
            startActivity(Intent(this,TutorialActivity::class.java))
        }

        binding.btnPalabra.setOnClickListener {
            startActivity(Intent(this,CrearActivity::class.java))
        }

        binding.btnBiblioteca.setOnClickListener {
            startActivity(Intent(this,BibliotecaActivity::class.java))
        }

        binding.btnModificar.setOnClickListener {
            val mDe = Intent(this,BibliotecaActivity::class.java)
            mDe.putExtra("modificar",true)
            startActivity(mDe)
        }
    }

    fun verVideo(palabra:String?){
        val sVp = Intent(this,PlayerActivity::class.java)
        sVp.putExtra("palabra",palabra)
        startActivity(sVp)
    }

    private fun cargarTutorial(){
        lifecycleScope.launch {
            val valorB = consultarDato("bandera").orEmpty()
            if(valorB != "1") {
                startActivity(Intent(this@MainActivity, TutorialActivity::class.java))
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("bandera")] = "1"
                }
            }
        }
    }

    private fun elementoLista(item:String?,uriImagen:String?){
        var noPic = false
        val adapterDatos = AdapterDatos(listDatos)
        val uriNoPic = Uri.parse("android.resource://com.gardmeer.hellos/"+R.drawable.nopicmini)

        binding.rvReciente.adapter = adapterDatos
        binding.rvReciente.layoutManager = LinearLayoutManager(this)

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uriImagen?.toUri())
        } catch (e:Exception){
            noPic=true
        }

        if(uriImagen=="R.drawable.nopicmini"||noPic) {
            listDatos.add(NuevaPalabra(item,uriNoPic))
        } else {
            listDatos.add(NuevaPalabra(item,uriImagen?.toUri()))
        }

        adapterDatos.setOnItemClickListener(object:AdapterDatos.OnItemClickListener {
            override fun onItemClick(view:View) {
                val palabra = listDatos[binding.rvReciente.getChildAdapterPosition(view)].getPalabra()
                verVideo(palabra)
            }
        })
    }

    private suspend fun consultarLista(nombre:String): Set<String>? {
        val preferences = dataStore.data.first()
        return preferences[stringSetPreferencesKey(nombre)]
    }

    private suspend fun consultarDato(nombre:String): String? {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(nombre)]
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

    private fun crearImagen() {
        val uri: Uri = if (Build.VERSION.SDK_INT >= 29){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val nombreImagen = (System.currentTimeMillis()/1000).toString()+".jpg"
        ContentValues().put(MediaStore.MediaColumns.DISPLAY_NAME, nombreImagen)
        ContentValues().put(MediaStore.MediaColumns.RELATIVE_PATH,rutaAlmacenamiento)
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

    override fun onRestart() {
        super.onRestart()
        listDatos.clear()
        lifecycleScope.launch {
            consultarLista("reclista")?.forEach {
                uriImagen = consultarDato(it +"uriImagen")
                elementoLista(it,uriImagen)
            }
        }
    }
}