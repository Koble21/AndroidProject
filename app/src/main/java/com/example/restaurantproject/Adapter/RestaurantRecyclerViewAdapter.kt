package com.example.restaurantproject.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.widget.ImageView
import com.example.restaurantproject.R
import com.example.restaurantproject.Models.Restaurant
class RestaurantRecyclerViewAdapter (dataSet: MutableList<Restaurant>,
                                     private val clickListener: OnRestaurantItemClickListener
) : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder>(){

    private val dataList: MutableList<Restaurant>

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.restaurant_items, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.image.context).load(dataList[position].image_url)
            .placeholder(R.drawable.cheflogo)
            .error(R.drawable.cheflogo)
            .into(holder.image)
        holder.restaurantName.setText(dataList[position].name)
        holder.price.setText(dataList[position].price.toString())
        holder.address.setText(dataList[position].address)

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener  {
        var image: ImageView = itemView.findViewById(R.id.base_avatar)
        var restaurantName: TextView = itemView.findViewById(R.id.textViewTitle)
        var parenLayout: ConstraintLayout = itemView.findViewById(R.id.restaurant_view_layout)
        var price: TextView = itemView.findViewById(R.id.textViewPrice)
        var address: TextView = itemView.findViewById(R.id.textViewAddress)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            clickListener.onItemClick(adapterPosition)

        }
    }

    init {
        this.dataList = dataSet
    }
}

interface OnRestaurantItemClickListener{
    fun onItemClick(position: Int)

}