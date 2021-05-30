package com.example.restaurantproject

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.restaurantproject.data.User
import com.example.restaurantproject.data.UserViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import java.io.ByteArrayOutputStream
import java.io.IOException

class registerFragment :  Fragment() {
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var image:ImageView
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_register, container, false)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        val chooseB=view.findViewById<Button>(R.id.chooseButton)
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
                pickImageFromGallery()
            }
        }

         image=view.findViewById<ImageView>(R.id.imageProfile)
        val insert=view.findViewById<Button>(R.id.registerButton)
        val name = view.findViewById<EditText>(R.id.namePerson)
        val address = view.findViewById<EditText>(R.id.postal_Address)
        val number = view.findViewById<EditText>(R.id.phone_number)
        val email = view.findViewById<EditText>(R.id.emailAdddress)

        insert.setOnClickListener{
            if (name.length() ==0||address.length() ==0|| email.length() ==0||number.length() ==0)
                Snackbar.make(this.requireView(), "Please fill the fields correctly!", Snackbar.LENGTH_SHORT).show()
            else
            {
                val picture:ByteArray=imageViewToByte(image)

                val user = User(0, name.text.toString(), address.text.toString(), number.text.toString(), email.text.toString(),picture)
                mUserViewModel.deleteTheUsers()
                mUserViewModel.addUser(user)
                Snackbar.make(this.requireView(), "Data successfully added :D !", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_profileFragment)
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

    // processing and uploading selected image to the database
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        //super method removed
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                // getting the image uri
                val returnUri: Uri? = data.data

                // converting image to bitmap
                val bitmapImage =
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, returnUri)

                // converting bitmap to bytearray
                val imageByteArray = convertBitmapToByteArray(bitmapImage)

                if(imageByteArray != null) {

                    // load image in profilePic imageview
                    view?.post {
                        Glide.with(image.context).load(imageByteArray)
                                .placeholder(R.drawable.cheflogo)
                                .error(R.drawable.cheflogo)
                                .into(image)
                    }
                }
            }
        }
    }

    // converting bitmap to bytearray
    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        return try {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 35, baos)
            baos.toByteArray()
        } finally {
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) { }
            }
        }
    }


}