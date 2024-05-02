package com.gardmeer.hellos

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AdapterFragment(fm: FragmentManager, behavior: Int) :
    FragmentPagerAdapter(fm, behavior) {

    private var listaFragment = ArrayList<Fragment>()

    override fun getCount(): Int {
        return listaFragment.size
    }

    override fun getItem(position: Int): Fragment {
        return listaFragment[position]
    }

    fun agregarFragment(fragment:Fragment){
        listaFragment.add(fragment)
    }
}