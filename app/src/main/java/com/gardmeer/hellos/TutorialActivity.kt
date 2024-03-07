package com.gardmeer.hellos

import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
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

        val tPInstance = TPresentacionFragment.newInstance("param","param")
        val tIInstance = TInicioFragment.newInstance("param","param")
        val tCInstance = TCrearFragment.newInstance("param","param")
        val tVInstance = TVideoFragment.newInstance("param","param")
        val tImInstance = TImagenFragment.newInstance("param","param")
        val tPaInstance = TPalabraFragment.newInstance("param","param")
        val tBInstance = TBibliotecaFragment.newInstance("param","param")
        val tMInstance = TModificarFragment.newInstance("param","param")
        val tPmInstance = TPlayerMainFragment.newInstance("param","param")
        val tPvInstance = TPlayerVideoFragment.newInstance("param","param")
        val tLInstance = TLateralFragment.newInstance("param","param")
        val tFInstance = TFinalFragment.newInstance("param","param")
        val fAdapter = AdapterFragment(supportFragmentManager,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        fAdapter.agregarFragment(tPInstance)
        fAdapter.agregarFragment(tIInstance)
        fAdapter.agregarFragment(tCInstance)
        fAdapter.agregarFragment(tVInstance)
        fAdapter.agregarFragment(tImInstance)
        fAdapter.agregarFragment(tPaInstance)
        fAdapter.agregarFragment(tBInstance)
        fAdapter.agregarFragment(tMInstance)
        fAdapter.agregarFragment(tPmInstance)
        fAdapter.agregarFragment(tPvInstance)
        fAdapter.agregarFragment(tLInstance)
        fAdapter.agregarFragment(tFInstance)

        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = fAdapter
        agregarIndPuntos(0)

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {agregarIndPuntos(position)}

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun agregarIndPuntos(pos: Int) {
        puntosSlide=ArrayList(12)
        llPuntos.removeAllViews()

        for (i in 0..11){
            puntosSlide.add(TextView(this))
            puntosSlide[i].text = Html.fromHtml("&#8226")
            puntosSlide[i].textSize = 35f
            puntosSlide[i].setTextColor(resources.getColor(R.color.transparente))
            llPuntos.addView(puntosSlide[i])
        }
        if (puntosSlide.size>0){puntosSlide[pos].setTextColor(resources.getColor(R.color.white))}
    }
}