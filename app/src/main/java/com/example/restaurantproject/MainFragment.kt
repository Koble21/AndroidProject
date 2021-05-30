package com.example.restaurantproject

import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantproject.*
import com.example.restaurantproject.Adapter.RestaurantRecyclerViewAdapter
import com.example.restaurantproject.Adapter.OnRestaurantItemClickListener
import com.example.restaurantproject.Models.Restaurant
import com.example.restaurantproject.Repository.Repository
import com.example.restaurantproject.ViewModel.MainViewModel
import com.example.restaurantproject.ViewModel.MainViewModelFactory
import kotlinx.coroutines.selects.select

import kotlin.collections.ArrayList

class MainFragment : Fragment() ,OnRestaurantItemClickListener{

    private lateinit var viewModel: MainViewModel

    // flag that signals if the recycle view has reached the end or some arbitrary end position
    var hasStartedDataRetrieval = false
    var displayList: MutableList<Restaurant> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // retrofit test
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel =
            ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel::class.java)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val prof_but = view.findViewById<ImageButton>(R.id.profile_Button)
        prof_but.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_profileFragment)

        }
        val searchView=view.findViewById<SearchView>(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.filter(newText)
                }
                return false
            }
        })
        val selectcity=view.findViewById<ImageButton>(R.id.locationRest)
        selectcity.setOnClickListener{
            val dialog = CitiesFragment()
            dialog.show(requireActivity().supportFragmentManager, "cities_list")
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.listOfRes)
        val adapter = RestaurantRecyclerViewAdapter(displayList, this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // add listener for when the recycleview reaches the last couple of items
        // so we can start loading the next batch of data
        recyclerView.addOnScrollListener(getOnScrollListener())


        // loading and displaying the restaurants from the API
        // if the user searched for something, the search result will come through here
        viewModel.restaurantsFilteredList.observe(requireActivity(), Observer { response ->
            displayList.clear()
            displayList.addAll(response)
            recyclerView.adapter!!.notifyDataSetChanged()
            hasStartedDataRetrieval = false

        })
        if(!viewModel.restaurantsFilteredList.value.isNullOrEmpty()){
            displayList.clear()
            displayList.addAll(viewModel.restaurantsFilteredList.value!!)
            recyclerView.adapter!!.notifyDataSetChanged()
            hasStartedDataRetrieval = false
        }

        // if there are errors regarding the API, an alertdialog is displayed
        viewModel.error.observe(requireActivity(), Observer { result ->
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(result.toString())
            alertDialog.setMessage("Please check your internet connection or try again later!")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    requireActivity().finish()
                })
            alertDialog.create()
            alertDialog.show()
        })

        val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        val drawable = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(-0x7373730, -0x7373730)
        )
        drawable.setSize(1, 5)
        itemDecoration.setDrawable(drawable)
        recyclerView.addItemDecoration(itemDecoration)
       // Log.d("andi","erty")
    }

    // listener which notifies if the recyclerview has been moved
    private fun getOnScrollListener(): RecyclerView.OnScrollListener {

        return object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                // if the recyclerview has displayed the fifth from last element
                // we start loading the next page of restaurants from API
                val endHasBeenReached = lastVisible + 5 >= totalItemCount

                if (totalItemCount > 0 && endHasBeenReached) {
                    // get the next batch of data
                    val currentPage = viewModel.myResponse.value?.page
                    val perPageItems = viewModel.myResponse.value?.per_page
                    val totalItems = viewModel.myResponse.value?.total_entries

                    if (currentPage != null && perPageItems != null && totalItems != null) {

                        // checking if there are more restaurants to be downloaded
                        if (currentPage * perPageItems < totalItems && !hasStartedDataRetrieval) {
                            hasStartedDataRetrieval = true
                            viewModel.getRestaurantsPaginated(viewModel.cityName, currentPage + 1)
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        val restaurant = displayList[position]
        viewModel.setSelectedRestaurant(restaurant)
        findNavController().navigate(R.id.action_mainFragment_to_detailFragment)
    }

}