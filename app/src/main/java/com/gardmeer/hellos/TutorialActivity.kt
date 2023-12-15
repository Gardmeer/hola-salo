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

        val TBFragmentInstance = TutorialBibliotecaFragment.newInstance("param","param")
        val TVFragmentInstance = TutorialVideoFragment.newInstance("param","param")
        val TCFragmentInstance = TutorialCrearFragment.newInstance("param","param")
        val fAdapter = AdapterFragment(supportFragmentManager,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        fAdapter.agregarFragment(TBFragmentInstance)
        fAdapter.agregarFragment(TVFragmentInstance)
        fAdapter.agregarFragment(TCFragmentInstance)

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
        puntosSlide=ArrayList(3)
        llPuntos.removeAllViews()

        for (i in 0..2){
            puntosSlide.add(TextView(this))
            puntosSlide[i].text = Html.fromHtml("&#8226")
            puntosSlide[i].textSize = 35f
            puntosSlide[i].setTextColor(resources.getColor(R.color.transparente))
            llPuntos.addView(puntosSlide[i])
        }
        if (puntosSlide.size>0){puntosSlide[pos].setTextColor(resources.getColor(R.color.white))}
    }
}