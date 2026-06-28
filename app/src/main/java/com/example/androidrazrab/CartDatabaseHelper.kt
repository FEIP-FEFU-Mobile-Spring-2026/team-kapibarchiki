package com.example.androidrazrab

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CartDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "cart.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE cart_items (
                product_id TEXT NOT NULL,
                size_id TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                PRIMARY KEY(product_id, size_id)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS cart_items")
        onCreate(db)
    }

    fun addToCart(productId: String, sizeId: String) {
        val db = writableDatabase

        val cursor = db.rawQuery(
            """
        SELECT quantity 
        FROM cart_items 
        WHERE product_id = ? AND size_id = ?
        """.trimIndent(),
            arrayOf(productId, sizeId)
        )

        cursor.use {
            if (it.moveToFirst()) {
                val currentQuantity = it.getInt(0)

                db.execSQL(
                    """
                UPDATE cart_items 
                SET quantity = ? 
                WHERE product_id = ? AND size_id = ?
                """.trimIndent(),
                    arrayOf(currentQuantity + 1, productId, sizeId)
                )
            } else {
                db.execSQL(
                    """
                INSERT INTO cart_items(product_id, size_id, quantity)
                VALUES(?, ?, 1)
                """.trimIndent(),
                    arrayOf(productId, sizeId)
                )
            }
        }
    }

    fun updateQuantity(productId: String, sizeId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(productId, sizeId)
        } else {
            writableDatabase.execSQL(
                "UPDATE cart_items SET quantity = ? WHERE product_id = ? AND size_id = ?",
                arrayOf(quantity, productId, sizeId)
            )
        }
    }

    fun removeItem(productId: String, sizeId: String) {
        writableDatabase.execSQL(
            "DELETE FROM cart_items WHERE product_id = ? AND size_id = ?",
            arrayOf(productId, sizeId)
        )
    }

    fun clearCart() {
        writableDatabase.execSQL("DELETE FROM cart_items")
    }

    fun getCartItems(): List<CartDbItem> {
        val result = mutableListOf<CartDbItem>()

        val cursor = readableDatabase.rawQuery(
            "SELECT product_id, size_id, quantity FROM cart_items",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    CartDbItem(
                        productId = it.getString(0),
                        sizeId = it.getString(1),
                        quantity = it.getInt(2)
                    )
                )
            }
        }

        return result
    }
}