package com.gardmeer.hellos

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterFragment(fm: FragmentManager, lifecycle:  Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private var listaFragment = ArrayList<Fragment>()

    fun agregarFragment(fragment:Fragment){
        listaFragment.add(fragment)
    }

    override fun getItemCount(): Int {
        return listaFragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return listaFragment[position]
    }
}