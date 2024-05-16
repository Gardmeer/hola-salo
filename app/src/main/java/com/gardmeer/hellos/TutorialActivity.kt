package com.gardmeer.hellos

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager2.widget.ViewPager2
import com.gardmeer.hellos.databinding.ActivityTutorialBinding

class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private lateinit var llPuntos : LinearLayout
    private  var puntosSlide = ArrayList<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        llPuntos=findViewById(R.id.llPuntos)

        val fAdapter = AdapterFragment(supportFragmentManager,lifecycle)
        fAdapter.agregarFragment(TPresentacionFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TInicioFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TCrearFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TVideoFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TImagenFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TPalabraFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TBibliotecaFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TModificarFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TPlayerMainFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TPlayerVideoFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TLateralFragment.newInstance("param","param"))
        fAdapter.agregarFragment(TFinalFragment.newInstance("param","param"))

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = fAdapter
        agregarIndPuntos(0)

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                agregarIndPuntos(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun agregarIndPuntos(pos: Int) {
        puntosSlide=ArrayList(12)
        llPuntos.removeAllViews()

        for (i in 0..11){
            puntosSlide.add(TextView(this))
            puntosSlide[i].text = HtmlCompat.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY)
            puntosSlide[i].textSize = 35f
            puntosSlide[i].setTextColor(ContextCompat.getColor(this, R.color.transparente))
            llPuntos.addView(puntosSlide[i])
        }
        if (puntosSlide.size>0){
            puntosSlide[pos].setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }
}