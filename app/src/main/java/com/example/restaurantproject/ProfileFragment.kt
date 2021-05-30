package com.example.restaurantproject

import android.graphics.BitmapFactory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.restaurantproject.data.UserViewModel


class ProfileFragment : Fragment() {
    private lateinit var mUserViewModel: UserViewModel
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val image = view.findViewById<ImageView>(R.id.imageChoosed)
        val name = view.findViewById<TextView>(R.id.personName)
        val address = view.findViewById<TextView>(R.id.postalAdress)
        val number = view.findViewById<TextView>(R.id.phoneNumber)
        val email = view.findViewById<TextView>(R.id.emailAdress)
        val update=view.findViewById<Button>(R.id.update_Button)
        val register=view.findViewById<Button>(R.id.register_Button)

        mUserViewModel.readUserData().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                name.text = it.name.toString()
                address.text = it.address.toString()
                email.text = it.email.toString()
                number.text = it.phone.toString()
                if(it.image?.size != null)
                {
                    image.setImageBitmap(BitmapFactory.decodeByteArray(it.image, 0, it.image?.size!!))
                }
            }
        })
        update.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_updateFragment)
        }

        register.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_registerFragment)
        }
        view.findViewById<Button>(R.id.homepageButton).setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
        }

        return view

    }
}