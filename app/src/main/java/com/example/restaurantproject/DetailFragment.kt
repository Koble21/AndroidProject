package com.example.restaurantproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.restaurantproject.ViewModel.MainViewModel
import com.example.restaurantproject.ViewModel.MainViewModelFactory
import com.example.restaurantproject.Repository.Repository

class DetailFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var restaurantName : TextView
    private lateinit var restaurantAddress : TextView
    private lateinit var restaurantPhone : TextView
    private lateinit var restaurantPrice : TextView
    private lateinit var phoneButton : ImageView
    private lateinit var restaurantImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel =
                ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapButton : ImageButton = view.findViewById(R.id.imageButtonMap)
        mapButton.setOnClickListener {
            val intent = Intent(context, MapActivity::class.java)
            intent.putExtra("lat", viewModel.selectedRestaurant.value?.lat.toString())
            intent.putExtra("lng", viewModel.selectedRestaurant.value?.lng.toString())
            intent.putExtra("name", viewModel.selectedRestaurant.value?.name.toString())
            startActivity(intent)
        }

        restaurantName = view.findViewById<TextView>(R.id.textViewTitle)
        restaurantAddress = view.findViewById<TextView>(R.id.textViewAddress)
        restaurantPhone = view.findViewById<TextView>(R.id.textViewPhone)
        restaurantPrice = view.findViewById<TextView>(R.id.textViewPrice)
        phoneButton = view.findViewById(R.id.imageViewPhoneIcon)
        restaurantImageView = view.findViewById(R.id.imageView)

        phoneButton.setOnClickListener {
            if (restaurantPhone.text.toString().isNotEmpty()) {
                callRestaurant()
            }
        }

        restaurantPhone.setOnClickListener {
            if (restaurantPhone.text.toString().isNotEmpty()) {
                callRestaurant()
            }
        }

        // displaying the selected restaurant data on the fragment
        viewModel.selectedRestaurant.observe(requireActivity(), Observer { restaurant ->
            Glide.with(restaurantImageView.context).load(restaurant.image_url)
                .placeholder(R.drawable.cheflogo)
                .error(R.drawable.cheflogo)
                .into(restaurantImageView)
            restaurantName.text = restaurant.name
            restaurantAddress.text = restaurant.address
            restaurantPhone.text = restaurant.phone.take(10)
            restaurantPrice.text = restaurant.price.toString()
        })


    }

    fun callRestaurant() {
        val phoneNumber : String = restaurantPhone.text.toString()
        if (phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")

            // permission request
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), 1)
            }
            else {
                startActivity(intent)
            }
        }
    }
}