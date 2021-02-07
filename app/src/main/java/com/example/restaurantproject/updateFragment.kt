package com.example.restaurantproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.restaurantproject.data.User
import com.example.restaurantproject.data.UserViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class updateFragment :  Fragment() {
    private lateinit var mUserViewModel: UserViewModel
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_update, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        val chooseB=view.findViewById<Button>(R.id.updatePhoto)
        chooseB.setOnClickListener{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                {
//                    permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
//                    show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else
                {
//                    permission already granted
                    pickImageFromGallery()
                }
            }
            else
            {
//                system OS is < Marshmalow
                pickImageFromGallery()
            }
        }

        val image=view.findViewById<ImageView>(R.id.picChoosed)
        val insert=view.findViewById<Button>(R.id.updateButton)
        val name = view.findViewById<EditText>(R.id.updateName)
        val address = view.findViewById<EditText>(R.id.updatePostalAddress)
        val number = view.findViewById<EditText>(R.id.updatePhone)
        val email = view.findViewById<EditText>(R.id.updateEmail)

        mUserViewModel.readUserData().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                name.setText(it.name.toString())
                address.setText(it.address.toString())
                email.setText(it.email.toString())
                number.setText(it.phone.toString())
                if(it.image?.size != null)
                {
                    image.setImageBitmap(BitmapFactory.decodeByteArray(it.image, 0, it.image?.size!!))
                }
            }
        })

        insert.setOnClickListener{
            if (name.length() ==0||address.length() ==0|| email.length() ==0||number.length() ==0)
                Snackbar.make(this.requireView(), "Fill the fields correctly!", Snackbar.LENGTH_SHORT).show()
            else
            {
                val picture:ByteArray=imageViewToByte(image)

                val user = User(0, name.text.toString(), address.text.toString(), number.text.toString(), email.text.toString(),picture)
                mUserViewModel.deleteTheUsers()
                mUserViewModel.addUser(user)
                Snackbar.make(this.requireView(), "Data successfully added :D !", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_updateFragment_to_profileFragment)
            }
        }
        return view

    }
    private fun imageViewToByte(image: ImageView): ByteArray {
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)

        return stream.toByteArray()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object{
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode){
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else
                {
                    //permission from popup denied
                    Snackbar.make(this.requireView(), "Permission denied!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val image = view?.findViewById<ImageView>(R.id.imageProfile)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image?.setImageURI(data?.data)
        }
    }
}