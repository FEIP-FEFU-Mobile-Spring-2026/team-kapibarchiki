package com.example.androidrazrab

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var adapter: CartAdapter
    private lateinit var backButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartText: TextView
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: Button
    private lateinit var clearCartButton: ImageButton
    private lateinit var orderForm: LinearLayout
    private lateinit var bottomOrderPanel: LinearLayout

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var commentEditText: EditText

    private var catalogProducts: List<Product> = emptyList()
    private var cartItems: List<CartUiItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cart)

        cartRepository = CartRepository(this)
        productRepository = ProductRepository(this)

        bindViews()
        setupRecycler()
        setupListeners()
        loadCart()
    }

    private fun bindViews() {
        recyclerView = findViewById(R.id.cartRecyclerView)
        emptyCartText = findViewById(R.id.emptyCartText)
        totalPriceText = findViewById(R.id.totalPriceText)
        checkoutButton = findViewById(R.id.checkoutButton)
        clearCartButton = findViewById(R.id.clearCartButton)
        backButton = findViewById(R.id.backButton)
        orderForm = findViewById(R.id.orderForm)
        bottomOrderPanel = findViewById(R.id.bottomOrderPanel)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        commentEditText = findViewById(R.id.commentEditText)
    }

    private fun setupRecycler() {
        adapter = CartAdapter(
            items = emptyList(),
            onPlus = { item ->
                cartRepository.updateQuantity(
                    item.product.id,
                    item.size.id,
                    item.quantity + 1
                )
                loadCart()
            },
            onMinus = { item ->
                cartRepository.updateQuantity(
                    item.product.id,
                    item.size.id,
                    item.quantity - 1
                )
                loadCart()
            },
            onRemove = { item ->
                cartRepository.removeItem(
                    item.product.id,
                    item.size.id
                )
                loadCart()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        clearCartButton.setOnClickListener {
            showClearCartDialog()
        }
        backButton.setOnClickListener {
            finish()
        }
        checkoutButton.setOnClickListener {
            checkout()
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateCheckoutButton()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) = Unit
        }

        nameEditText.addTextChangedListener(watcher)
        emailEditText.addTextChangedListener(watcher)
    }

    private fun loadCart() {
        val catalog = productRepository.getCachedCatalog()

        catalogProducts = catalog?.items ?: emptyList()
        cartItems = cartRepository.getCartUiItems(catalogProducts)

        adapter.updateItems(cartItems)

        val isEmpty = cartItems.isEmpty()

        recyclerView.isVisible = !isEmpty
        emptyCartText.isVisible = isEmpty
        orderForm.isVisible = !isEmpty
        bottomOrderPanel.isVisible = !isEmpty
        clearCartButton.isVisible = !isEmpty

        updateTotalPrice()
        updateCheckoutButton()
    }

    private fun updateTotalPrice() {
        val total = cartItems.sumOf {
            it.product.priceInKopecks * it.quantity
        }

        totalPriceText.text = total.toRubles()
    }

    private fun updateCheckoutButton() {
        val nameValid = nameEditText.text.toString().isNotBlank()
        val emailValid = emailEditText.text.toString()
            .matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))

        checkoutButton.isEnabled = nameValid && emailValid && cartItems.isNotEmpty()
        checkoutButton.alpha = if (checkoutButton.isEnabled) 1f else 0.5f
    }

    private fun showClearCartDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Очистить корзину?")
            .setMessage("Все товары будут удалены из корзины.")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Очистить") { _, _ ->
                cartRepository.clearCart()
                loadCart()
            }
            .show()
    }
    private fun showSuccessDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_custom, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.findViewById<TextView>(R.id.dialogIcon).text = "✓"
        view.findViewById<TextView>(R.id.dialogTitle).text = "Заказ оформлен"
        view.findViewById<TextView>(R.id.dialogMessage).text =
            "Спасибо! Ваш заказ успешно оформлен."

        view.findViewById<Button>(R.id.dialogButton).setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun checkout() {
        cartRepository.clearCart()
        showSuccessDialog()
    }
}