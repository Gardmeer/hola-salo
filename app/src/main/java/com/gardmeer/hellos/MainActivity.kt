package com.gardmeer.hellos

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    val listDatos = ArrayList<NuevaPalabra>()
    private lateinit var dlMenu : DrawerLayout
    private lateinit var nvNavegacion : NavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_HelloS)
        super.onCreate(savedInstanceState)

        dlMenu=findViewById(R.id.dlMenu)
        nvNavegacion=findViewById(R.id.nvNavegacion)

        nvNavegacion.itemIconTintList = null

        val adapterDatos = AdapterDatos(listDatos)
        val rvReciente: RecyclerView = findViewById(R.id.rvReciente)
        rvReciente.adapter = adapterDatos
        rvReciente.layoutManager = LinearLayoutManager(this)

        llenarReciente()

        adapterDatos.setOnItemClickListener(object:AdapterDatos.onItemClickListener {
            override fun onItemClick(view:View) {
                val palabra = listDatos[rvReciente.getChildAdapterPosition(view)].getPalabra()
                verVideo(palabra)
            }
        })

        nvNavegacion.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mnCrear -> {
                    val cWr = Intent(this,CrearActivity::class.java)
                    startActivity(cWr)
                }
                R.id.mnBiblioteca -> {
                    val sLb = Intent(this,BibliotecaActivity::class.java)
                    startActivity(sLb)
                }
                R.id.mnTutorial -> {

                }
                R.id.mnSalir -> {
                    finish()
                }
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

    fun verVideo(palabra:String?){
        val sVp = Intent(this,PlayerActivity::class.java)
        sVp.putExtra("palabra",palabra)
        startActivity(sVp)
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
}