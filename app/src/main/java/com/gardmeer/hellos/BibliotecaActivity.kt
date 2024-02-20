package com.gardmeer.hellos

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BibliotecaActivity : AppCompatActivity(R.layout.activity_biblioteca) {
    private lateinit var dlMenu: RelativeLayout
    private lateinit var txtBiblioteca: TextView
    val listDatos = ArrayList<NuevaPalabra>()
    var modificarBool =  false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        modificarBool = intent.getBooleanExtra("modificar",false)

        dlMenu=findViewById(R.id.dlMenu)
        txtBiblioteca=findViewById(R.id.txtBiblioteca)

        if (modificarBool == true){
            txtBiblioteca.setText(R.string.modify)
            dlMenu.setBackgroundColor(resources.getColor(R.color.some_blue))
        }

        val adapterDatos = AdapterDatos(listDatos)
        val rvBiblioteca: RecyclerView = findViewById(R.id.rvBiblioteca)
        rvBiblioteca.adapter = adapterDatos
        rvBiblioteca.layoutManager = LinearLayoutManager(this)

        llenarBiblioteca()

        adapterDatos.setOnItemClickListener(object:AdapterDatos.OnItemClickListener {
            override fun onItemClick(view: View) {
                val palabra = listDatos[rvBiblioteca.getChildAdapterPosition(view)].getPalabra()

                if (modificarBool == true){
                    val lista = resources.getStringArray(R.array.modify_option)
                    val builder = AlertDialog.Builder(this@BibliotecaActivity)
                    builder.setTitle(resources.getString(R.string.modify_word))
                    builder.setItems(lista){ _, posicion ->
                        when(posicion){
                            0 -> {
                                modificarPalabra(palabra)
                            }
                            1 -> {
                                eliminarPalabra(palabra)
                            }
                        }}
                    builder.create()
                    builder.show()
                }
                else {verVideo(palabra)}
            }
        })
    }

    fun modificarPalabra(palabra:String?){
        val mPa = Intent(this,CrearActivity::class.java)
        mPa.putExtra("palabra",palabra)
        startActivity(mPa)
    }

    fun eliminarPalabra(palabra:String?){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.delete_cancel) +" "+palabra)
            .setPositiveButton(R.string.accept
            ) { _, _ ->
                //val borrarPalabra = palabra
                val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
                val editarL = prefL.edit()
                val lista = prefL.getStringSet("addlista", sortedSetOf<String?>())
                val reciente = prefL.getStringSet("reclista", sortedSetOf<String?>())
                val buLista = lista
                val buReciente = reciente

                editarL.remove("addlista")
                editarL.remove("reclista")
                editarL.apply()

                buLista!!.remove(palabra)
                buReciente!!.remove(palabra)
                editarL.putStringSet("addlista", buLista)
                editarL.putStringSet("reclista", buReciente)
                editarL.apply()

                Toast.makeText(applicationContext, resources.getString(R.string.word_deleted,palabra), Toast.LENGTH_LONG).show()

                val iIn = Intent(this, MainActivity::class.java)
                startActivity(iIn)
            }
            .setNegativeButton(R.string.cancel
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }

    fun verVideo(palabra:String?){
        val sVp = Intent(this,PlayerActivity::class.java)
        sVp.putExtra("palabra",palabra)
        startActivity(sVp)
    }

    private fun llenarBiblioteca() {
        val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
        val lista = prefL.getStringSet("addlista",sortedSetOf<String?>())
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