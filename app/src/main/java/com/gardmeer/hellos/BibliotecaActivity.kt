package com.gardmeer.hellos

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.gardmeer.hellos.databinding.ActivityBibliotecaBinding

class BibliotecaActivity : AppCompatActivity(R.layout.activity_biblioteca) {
    private lateinit var binding: ActivityBibliotecaBinding
    private val listaCategorias = ArrayList<String>()
    var posCheck = 0
    var modificarBool =  false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBibliotecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modificarBool = intent.getBooleanExtra("modificar",false)

        llenarBiblioteca("Todo")
        cargarCategorias()
        clickCategorias()

        if (modificarBool == true){
            binding.txtBiblioteca.setText(R.string.modify)
            binding.dlMenu.setBackgroundColor(resources.getColor(R.color.purple))
        }
    }

    fun modificarPalabra(palabra:String?){
        val mPa = Intent(this,CrearActivity::class.java)
        mPa.putExtra("palabra",palabra)
        startActivity(mPa)
        finish()
    }

    fun eliminarPalabra(palabra:String?){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.delete_cancel) +" "+palabra)
            .setPositiveButton(R.string.accept
            ) { _, _ ->
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
                finish()
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

    private fun cargarCategorias(){
        val prefL = getSharedPreferences("lista", Context.MODE_PRIVATE)
        val lista = prefL.getStringSet("addlista",sortedSetOf<String?>())
        val categoriaTemp = mutableSetOf<String>()

        listaCategorias.add(resources.getString(R.string.all_category))
        listaCategorias.add(resources.getString(R.string.no_category))
        
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
        binding.spCategoria.adapter=adp
    }
    private fun clickCategorias(){
        binding.spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                posCheck=position

                if (position==0){
                    llenarBiblioteca("Todo")
                }
                else if (position==1){
                    llenarBiblioteca("")
                } else {
                    llenarBiblioteca(listaCategorias[position])
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }
    }
    private fun llenarBiblioteca(categoriaLib:String) {
        val listDatos = ArrayList<NuevaPalabra>()
        val adapterDatos = AdapterDatos(listDatos)
        binding.rvBiblioteca.adapter = adapterDatos
        binding.rvBiblioteca.layoutManager = LinearLayoutManager(this)

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
            val categoria = pref.getString("categorias","")

            try {val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uriImagen?.toUri())
            } catch (e:Exception){noPic=true}

            if (categoriaLib=="Todo"||categoriaLib==categoria){
                if(uriImagen==R.drawable.nopicmini.toString()||noPic) {
                    listDatos.add(NuevaPalabra(it,uriNoPic))
                    noPic=false
                } else {
                    listDatos.add(NuevaPalabra(it,uriImagen?.toUri()))
                }
            }
        }

        adapterDatos.setOnItemClickListener(object:AdapterDatos.OnItemClickListener {
            override fun onItemClick(view: View) {
                val palabra = listDatos[binding.rvBiblioteca.getChildAdapterPosition(view)].getPalabra()

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
}