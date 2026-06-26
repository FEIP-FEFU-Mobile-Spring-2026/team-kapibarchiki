package com.example.androidrazrab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
class ProductAdapter(
    private var products: List<Product>,
    private val context: Context
) : RecyclerView.Adapter<ProductAdapter.Holder>() {

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.Title)
        private val price: TextView = view.findViewById(R.id.Price)
        private val description: TextView = view.findViewById(R.id.Description)
        private val image: ImageView = view.findViewById(R.id.productImage)

        fun bind(product: Product) {
            title.text = product.name
            price.text = product.priceInKopecks.toRubles()
            description.text = product.shortDescription

            Glide.with(itemView.context)
                .load(product.imageUrl)
                .centerCrop()
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val product = products[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            showProductDetailDialog(product)
        }
    }

    override fun getItemCount(): Int = products.size

    private fun showProductDetailDialog(product: Product) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.item_detail, null)

        dialogView.findViewById<TextView>(R.id.detailTitle).text = product.name
        dialogView.findViewById<TextView>(R.id.detailDescription).text = product.longDescription
        dialogView.findViewById<TextView>(R.id.detailPrice).text = product.priceInKopecks.toRubles()

        Glide.with(context)
            .load(product.imageUrl)
            .centerCrop()
            .into(dialogView.findViewById(R.id.detailProductImage))

        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
    }
}

fun Long.toRubles(): String {
    val rubles = this / 100
    val locale = Locale.forLanguageTag("ru-RU")
    return NumberFormat.getIntegerInstance(locale).format(rubles) + " ₽"
}