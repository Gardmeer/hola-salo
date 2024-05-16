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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gardmeer.hellos.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

val Context.dataStore by preferencesDataStore(name = "USER_PREFERENCES_NAME")
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var binding: ActivityMainBinding
    var listDatos = ArrayList<NuevaPalabra>()
    private var uriImagen: String? = null
    private var permisosOk = false
    private var permisosRq = false
    private val rutaAlmacenamiento = "ArchivosApp/HolaSalo/"
    private val permisos = arrayOf("android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE","android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_IMAGES")
    override fun onCreate(savedInstanceState: Bundle?) {
        cargarTutorial()
        setTheme(R.style.Theme_HelloS)
        Thread.sleep(500)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nvNavegacion.isGone=false
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
                        val videoUri = crearVideo()
                        try{
                            capturarVideo.launch(videoUri)
                        } catch (ex:ActivityNotFoundException){
                            Toast.makeText(this,"Not Found",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                R.id.mnImagen -> {
                    evaluarPermisos()
                    if(permisosOk){
                        val imagenUri = crearImagen()
                        try{
                            capturarImagen.launch(imagenUri)
                        } catch (ex:ActivityNotFoundException){
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

        val imgFile = File(uriImagen!!)
        if(!imgFile.exists()){
            noPic=true
        }
        if(uriImagen=="R.drawable.nopicmini"||noPic) {
            listDatos.add(NuevaPalabra(item,uriNoPic))
        } else {
            listDatos.add(NuevaPalabra(item, uriImagen.toUri()))
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

    private fun crearVideo(): Uri? {
        val uri: Uri = if (Build.VERSION.SDK_INT >= 29) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val nombreVideo = (System.currentTimeMillis() / 1000).toString() + ".mp4"
        ContentValues().put(MediaStore.Video.Media.DISPLAY_NAME, nombreVideo)
        ContentValues().put(MediaStore.Video.Media.RELATIVE_PATH, rutaAlmacenamiento)
        return contentResolver.insert(uri, ContentValues())
    }

    private fun crearImagen(): Uri? {
        val uri: Uri = if (Build.VERSION.SDK_INT >= 29){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val nombreImagen = (System.currentTimeMillis()/1000).toString()+".jpg"
        ContentValues().put(MediaStore.MediaColumns.DISPLAY_NAME, nombreImagen)
        ContentValues().put(MediaStore.MediaColumns.RELATIVE_PATH,rutaAlmacenamiento)
        return contentResolver.insert(uri, ContentValues())
    }

    private val capturarVideo = registerForActivityResult(ActivityResultContracts.CaptureVideo()){
    }

    private val capturarImagen = registerForActivityResult(ActivityResultContracts.TakePicture()){
    }

    override fun onRestart() {
        super.onRestart()
        listDatos.clear()
        lifecycleScope.launch {
            val reciente = consultarLista("reclista").orEmpty()
            if (reciente.isEmpty()){
                binding.rvReciente.isVisible=false
            } else {
                reciente.forEach {
                    uriImagen = consultarDato(it + "uriImagen")
                    elementoLista(it, uriImagen)
                }
                binding.rvReciente.isVisible=true
            }
        }
    }
}