package com.example.androidrazrab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private var items: List<CartUiItem>,
    private val onPlus: (CartUiItem) -> Unit,
    private val onMinus: (CartUiItem) -> Unit,
    private val onRemove: (CartUiItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.Holder>() {

    fun updateItems(newItems: List<CartUiItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cartProductImage)
        val title: TextView = view.findViewById(R.id.cartTitle)
        val size: TextView = view.findViewById(R.id.cartSize)
        val price: TextView = view.findViewById(R.id.cartPrice)
        val quantity: TextView = view.findViewById(R.id.quantityText)
        val plus: TextView = view.findViewById(R.id.plusButton)
        val minus: TextView = view.findViewById(R.id.minusButton)
        val remove: TextView = view.findViewById(R.id.removeItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]

        holder.title.text = item.product.name
        holder.size.text = item.size.name
        holder.quantity.text = item.quantity.toString()
        holder.price.text = (item.product.priceInKopecks * item.quantity).toRubles()

        Glide.with(holder.itemView.context)
            .load(item.product.imageUrl)
            .centerCrop()
            .into(holder.image)

        holder.plus.setOnClickListener {
            onPlus(item)
        }

        holder.minus.setOnClickListener {
            onMinus(item)
        }

        holder.remove.setOnClickListener {
            onRemove(item)
        }
    }

    override fun getItemCount(): Int = items.size
}