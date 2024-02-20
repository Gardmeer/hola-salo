package com.gardmeer.hellos

import android.content.ActivityNotFoundException
import android.content.ContentResolver
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
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    val listDatos = ArrayList<NuevaPalabra>()
    private lateinit var dlMenu : DrawerLayout
    private lateinit var nvNavegacion : NavigationView
    private val carpetaRaiz = "ArchivosApp/"
    private val rutaAlmacenamiento = carpetaRaiz+"HolaSalo/"
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var permisosOk = false
    private var permisosRq = false
    private val codigos = arrayOf(10,20,123,321,124,324) // Camara, Escritura, Imagen, Video, CapImagen, CapVideo
    private val permisos = arrayOf("android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_EXTERNAL_STORAGE","android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_IMAGES")
    override fun onCreate(savedInstanceState: Bundle?) {
        cargarTutorial()
        setTheme(R.style.Theme_HelloS)
        Thread.sleep(500)
        super.onCreate(savedInstanceState)

        dlMenu=findViewById(R.id.dlMenu)
        nvNavegacion=findViewById(R.id.nvNavegacion)

        nvNavegacion.itemIconTintList = null

        val adapterDatos = AdapterDatos(listDatos)
        val rvReciente: RecyclerView = findViewById(R.id.rvReciente)
        rvReciente.adapter = adapterDatos
        rvReciente.layoutManager = LinearLayoutManager(this)

        llenarReciente()

        adapterDatos.setOnItemClickListener(object:AdapterDatos.OnItemClickListener {
            override fun onItemClick(view:View) {
                val palabra = listDatos[rvReciente.getChildAdapterPosition(view)].getPalabra()
                verVideo(palabra)
            }
        })

        nvNavegacion.setNavigationItemSelectedListener {
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
                    val buscar = Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/imghp"))
                    startActivity(buscar)
                }
                R.id.mnSalir -> {
                    dlMenu.closeDrawer(GravityCompat.START)}
            }
            true
        }
    }

    fun verMenu(view:View){
        dlMenu.openDrawer(GravityCompat.START)
    }

    fun verTutorial(view: View){
        val vTu = Intent(this,TutorialActivity::class.java)
        startActivity(vTu)
    }

    fun crearPalabra(view:View){
        val cWr = Intent(this,CrearActivity::class.java)
        startActivity(cWr)
    }

    fun verBiblioteca(view:View){
        val sLb = Intent(this,BibliotecaActivity::class.java)
        startActivity(sLb)
    }

    fun modificarBorrar(view:View){
        val mDe = Intent(this,BibliotecaActivity::class.java)
        mDe.putExtra("modificar",true)
        startActivity(mDe)
    }

    fun verVideo(palabra:String?){
        val sVp = Intent(this,PlayerActivity::class.java)
        sVp.putExtra("palabra",palabra)
        startActivity(sVp)
    }

    private fun cargarTutorial(){
        val prefB = getSharedPreferences("bandera", Context.MODE_PRIVATE)
        val editor = prefB.edit()
        val valorB = prefB.getString("valorBandera", "0")?.toInt()

        if(valorB == 0){
            val vTu = Intent(this,TutorialActivity::class.java)
            startActivity(vTu)
            editor.putString("valorBandera", "1")
            editor.apply()
        }
    }

    private fun llenarReciente() {
        val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
        val lista = prefL.getStringSet("reclista",sortedSetOf<String?>())
        var noPic = false
        val resourceId = R.drawable.nopicmini
        val uriNoPic = Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(resourceId))
            .appendPath(resources.getResourceTypeName(resourceId))
            .appendPath(resources.getResourceEntryName(resourceId))
            .build()

        lista!!.forEach {
            val pref = getSharedPreferences(it, Context.MODE_PRIVATE)
            val uriImagen = pref.getString("uriimagen","")

            try {val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uriImagen?.toUri())
            } catch (e:Exception){noPic=true}

            if(uriImagen==R.drawable.nopicmini.toString()||noPic) {
                listDatos.add(NuevaPalabra(it,uriNoPic))
                noPic=false
            } else {listDatos.add(NuevaPalabra(it,uriImagen?.toUri()))}
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
}