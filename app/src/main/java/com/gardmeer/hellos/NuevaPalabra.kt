package com.gardmeer.hellos

import android.net.Uri

public class NuevaPalabra(_palabra: String?,_imagen: Uri?){
    private var palabra:String?=_palabra
    private var imagen:Uri?=_imagen

    public fun getPalabra(): String?{
        return palabra
    }

    public fun getImagen(): Uri?{
        return imagen
    }

}