package com.example.restaurantproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton

class MainFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val prof_but = view.findViewById<ImageButton>(R.id.profile_Button)
        prof_but.setOnClickListener{
            val newFragment = ProfileFragment()
            val fragmentManager = fragmentManager
            requireFragmentManager().beginTransaction()
                .replace(R.id.navigationfragment, newFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }



}