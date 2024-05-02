package com.gardmeer.hellos

import android.net.Uri

class NuevaPalabra(tpalabra: String?,timagen: Uri?){
    private var palabra:String?=tpalabra
    private var imagen:Uri?=timagen

    fun getPalabra(): String?{
        return palabra
    }

    fun getImagen(): Uri?{
        return imagen
    }

}