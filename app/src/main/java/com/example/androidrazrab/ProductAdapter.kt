package com.example.androidrazrab
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import androidx.appcompat.app.AlertDialog
class ProductAdapter(private var products: List<Product>, private val context: Context) : RecyclerView.Adapter<ProductAdapter.Holder>()
{
    fun updateProducts(newProducts: List<Product>)
    {
        products = newProducts
        notifyDataSetChanged()
    }
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(prod: Product) = with(itemView) {
            findViewById<TextView>(R.id.Title).text = prod.title
            findViewById<TextView>(R.id.Price).text = prod.price + " $"
            findViewById<TextView>(R.id.Description).text = prod.description
            Glide.with(context).load(prod.image).into(findViewById(R.id.productImage))
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) = Holder(
        LayoutInflater.from(p.context).inflate(R.layout.item_product, p, false)
    )

    override fun onBindViewHolder(h: Holder, i: Int)
    {
        h.bind(products[i])
        h.itemView.setOnClickListener { showProductDetailDialog(products[i]) }
    }
    override fun getItemCount() = products.size
    private fun showProductDetailDialog(product: Product)
    {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_detail, null)

        dialogView.findViewById<TextView>(R.id.detailTitle).text = product.title
        dialogView.findViewById<TextView>(R.id.detailDescription).text = product.description
        dialogView.findViewById<TextView>(R.id.detailPrice).text = product.price + " $"
        dialogView.findViewById<TextView>(R.id.detailCategory).text = product.category

        Glide.with(context).load(product.image).into(dialogView.findViewById(R.id.detailProductImage))

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Закрыть", null)
            .show()
    }

}