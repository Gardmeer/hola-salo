package com.gardmeer.hellos

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.gardmeer.hellos.ui.main.SectionsPagerAdapter
import com.gardmeer.hellos.databinding.ActivityTutorialBinding

class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var TBFragmentInstance = TutorialBibliotecaFragment.newInstance("param","param")
        var fAdapter = AdapterFragment(supportFragmentManager,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        fAdapter.agregarFragment(TBFragmentInstance)

        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = fAdapter

    }
}