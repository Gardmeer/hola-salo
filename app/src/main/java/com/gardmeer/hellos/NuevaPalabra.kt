package com.gardmeer.hellos

import android.net.Uri

class NuevaPalabra(_palabra: String?,_imagen: Uri?){
    private var palabra:String?=_palabra
    private var imagen:Uri?=_imagen

    fun getPalabra(): String?{
        return palabra
    }

    fun getImagen(): Uri?{
        return imagen
    }

}