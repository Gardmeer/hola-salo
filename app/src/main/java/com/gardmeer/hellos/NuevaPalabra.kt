package com.gardmeer.hellos

public class NuevaPalabra(_palabra: String?,_imagen: Int){
    private var palabra:String?=_palabra
    private var imagen:Int=_imagen

    public fun getPalabra(): String?{
        return palabra
    }

    public fun getImagen(): Int {
        return imagen
    }

}