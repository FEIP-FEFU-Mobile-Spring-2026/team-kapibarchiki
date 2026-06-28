package com.example.androidrazrab

import android.content.Context

class CartRepository(context: Context) {

    private val db = CartDatabaseHelper(context.applicationContext)

    fun addToCart(productId: String, sizeId: String) {
        db.addToCart(productId, sizeId)
    }

    fun getCartUiItems(products: List<Product>): List<CartUiItem> {
        val cartItems = db.getCartItems()

        return cartItems.mapNotNull { cartItem ->
            val product = products.find { it.id == cartItem.productId }
            val size = product?.sizes?.find { it.id == cartItem.sizeId }

            if (product != null && size != null) {
                CartUiItem(product, size, cartItem.quantity)
            } else {
                null
            }
        }
    }

    fun updateQuantity(productId: String, sizeId: String, quantity: Int) {
        db.updateQuantity(productId, sizeId, quantity)
    }

    fun removeItem(productId: String, sizeId: String) {
        db.removeItem(productId, sizeId)
    }

    fun clearCart() {
        db.clearCart()
    }

    fun getTotalCount(): Int {
        return db.getCartItems().sumOf { it.quantity }
    }
}