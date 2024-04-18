package com.gardmeer.hellos

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
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gardmeer.hellos.databinding.ActivityBibliotecaBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BibliotecaActivity : AppCompatActivity(R.layout.activity_biblioteca) {
    private lateinit var binding: ActivityBibliotecaBinding
    private val listaCategorias = ArrayList<String>()
    var modificarBool =  false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBibliotecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modificarBool = intent.getBooleanExtra("modificar",false)

        cargarCategorias()
        clickCategorias()
        llenarBiblioteca("Todo")

        if (modificarBool){
            binding.txtBiblioteca.setText(R.string.modify)
            binding.dlMenu.setBackgroundColor(ContextCompat.getColor(applicationContext,R.color.purple))
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
                lifecycleScope.launch {
                    val modLista = consultarLista("lista")?.toMutableSet() ?: mutableSetOf()
                    val modReciente = consultarLista("reclista")?.toMutableSet() ?: mutableSetOf()
                    modLista.remove(palabra!!)
                    modReciente.remove(palabra)
                    dataStore.edit { preferences ->
                        preferences[stringSetPreferencesKey("lista")] = modLista
                        preferences[stringSetPreferencesKey("reclista")] = modReciente
                    }
                }
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
        val categoriaTemp = mutableSetOf<String>()
        lifecycleScope.launch {
            consultarLista("lista").orEmpty().forEach {
                val categoriaFill = consultarDato(it+"categorias")

                if (categoriaFill!=""){
                    categoriaTemp.add(categoriaFill.toString())
                }
            }
        }
        listaCategorias.add(resources.getString(R.string.all_category))
        listaCategorias.add(resources.getString(R.string.no_category))

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
                when (position) {
                    0 -> {
                        llenarBiblioteca("Todo")
                    }
                    1 -> {
                        llenarBiblioteca("")
                    }
                    else -> {
                        llenarBiblioteca(listaCategorias[position])
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }
        }
    }

    private fun llenarBiblioteca(categoriaLib:String){
        lifecycleScope.launch {
            var uriImagen:String?
            var categoria:String?
            val listDatos = ArrayList<NuevaPalabra>()

            consultarLista("lista").orEmpty().forEach {
                lifecycleScope.launch {
                    uriImagen = consultarDato(it +"uriImagen")
                    categoria = consultarDato(it +"categorias")
                    elementoLista(it,uriImagen,categoria,categoriaLib,listDatos)
                }
            }
        }
    }
    private fun elementoLista(item:String?,uriImagen:String?,categoria:String?,categoriaLib:String,
                              listaDatos:ArrayList<NuevaPalabra>){
        var noPic = false
        val adapterDatos = AdapterDatos(listaDatos)
        val uriNoPic = Uri.parse("android.resource://com.gardmeer.hellos/"+R.drawable.nopicmini)

        binding.rvBiblioteca.adapter = adapterDatos
        binding.rvBiblioteca.layoutManager = LinearLayoutManager(this)

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,uriImagen?.toUri())
        } catch (e:Exception){
            noPic=true
        }

        if (categoriaLib=="Todo"||categoriaLib==categoria){
            if(uriImagen=="R.drawable.nopicmini"||noPic) {
                listaDatos.add(NuevaPalabra(item, uriNoPic))
            } else {
                listaDatos.add(NuevaPalabra(item, uriImagen?.toUri()))
            }
        }
        adapterDatos.setOnItemClickListener(object:AdapterDatos.OnItemClickListener {
            override fun onItemClick(view:View) {
                val palabra =
                    listaDatos[binding.rvBiblioteca.getChildAdapterPosition(view)].getPalabra()

                if (modificarBool){
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

    private suspend fun consultarLista(nombre:String): Set<String>? {
        val preferences = dataStore.data.first()
        return preferences[stringSetPreferencesKey(nombre)]
    }

    private suspend fun consultarDato(nombre:String): String? {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(nombre)]
    }
}