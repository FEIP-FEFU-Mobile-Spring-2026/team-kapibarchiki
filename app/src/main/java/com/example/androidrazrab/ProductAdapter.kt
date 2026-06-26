package com.example.androidrazrab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

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
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_product_detail, null)

        val image = view.findViewById<ImageView>(R.id.detailProductImage)
        val title = view.findViewById<TextView>(R.id.detailTitle)
        val description = view.findViewById<TextView>(R.id.detailDescription)
        val price = view.findViewById<TextView>(R.id.detailPrice)
        val tagsChipGroup = view.findViewById<ChipGroup>(R.id.tagsChipGroup)
        val sizesChipGroup = view.findViewById<ChipGroup>(R.id.sizesChipGroup)
        val infoButton = view.findViewById<ImageButton>(R.id.infoButton)
        val addToCartButton = view.findViewById<Button>(R.id.addToCartButton)

        Glide.with(context)
            .load(product.imageUrl)
            .centerCrop()
            .into(image)

        title.text = product.name
        description.text = product.longDescription
        price.text = product.priceInKopecks.toRubles()
        product.tags.forEach { tag ->
            val chip = Chip(context).apply {
                text = tag
                isClickable = false
                isCheckable = false
                setTextColor(context.getColor(R.color.white))
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    context.getColor(R.color.light_brown)
                )
            }
            tagsChipGroup.addView(chip)
        }
        product.sizes.forEachIndexed { index, size ->
            val chip = Chip(context).apply {
                id = View.generateViewId()
                text = size.name
                isCheckable = true
                isClickable = true
                tag = size.id
                chipBackgroundColor = context.getColorStateList(R.color.chip_color_selector)
                setTextColor(context.getColorStateList(R.color.chip_color_text_selector))
                chipStrokeColor = null
                chipStrokeWidth = 0f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
                chipMinHeight = 120f
                textSize = 18f
            }
            sizesChipGroup.addView(chip)
            if (index == 0) {
                chip.isChecked = true
            }
        }
        infoButton.setOnClickListener {
            showInfoDialog(product)
        }
        addToCartButton.setOnClickListener {
            val selectedChipId = sizesChipGroup.checkedChipId
            val selectedChip = sizesChipGroup.findViewById<Chip>(selectedChipId)
            val selectedSize = selectedChip?.text?.toString() ?: "не выбран"
            Toast.makeText(
                context,
                "Товар добавлен в корзину. Размер: $selectedSize",
                Toast.LENGTH_SHORT
            ).show()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun showInfoDialog(product: Product) {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_custom, null)

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        view.findViewById<TextView>(R.id.dialogIcon).text = "ℹ"
        view.findViewById<TextView>(R.id.dialogTitle).text = "Информация о товаре"

        view.findViewById<TextView>(R.id.dialogMessage).text =
            """
        Материал: ${product.material}
        Вес: ${product.weight}
        Сезон: ${product.season}
        Страна: ${product.countryOfOrigin}
        """.trimIndent()

        view.findViewById<Button>(R.id.dialogButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}
fun Long.toRubles(): String {
    val rubles = this / 100
    val locale = Locale.forLanguageTag("ru-RU")
    return NumberFormat.getIntegerInstance(locale).format(rubles) + " ₽"
}