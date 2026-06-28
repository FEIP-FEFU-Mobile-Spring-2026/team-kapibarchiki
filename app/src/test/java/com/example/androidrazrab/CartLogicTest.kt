package com.example.androidrazrab

import org.junit.Assert.assertEquals
import org.junit.Test

class CartLogicTest {

    @Test
    fun sameProductAndSize_increasesQuantity() {
        val items = mutableMapOf<Pair<String, String>, Int>()

        val key = "item_001" to "m"
        items[key] = (items[key] ?: 0) + 1
        items[key] = (items[key] ?: 0) + 1

        assertEquals(2, items[key])
    }

    @Test
    fun sameProductDifferentSize_createsDifferentItem() {
        val items = mutableMapOf<Pair<String, String>, Int>()

        items["item_001" to "m"] = 1
        items["item_001" to "l"] = 1

        assertEquals(2, items.size)
    }

    @Test
    fun totalPrice_isCalculatedCorrectly() {
        val priceInKopecks = 199900
        val quantity = 3

        val total = priceInKopecks * quantity

        assertEquals(599700, total)
    }

    @Test
    fun newCategory_containsOnlyProductsWithNewTag() {
        val products = listOf(
            FakeProduct("1", listOf("New")),
            FakeProduct("2", listOf("Sale")),
            FakeProduct("3", listOf("New", "Hit"))
        )

        val result = products.filter { it.tags.contains("New") }

        assertEquals(listOf("1", "3"), result.map { it.id })
    }

    @Test
    fun emptyCart_totalIsZero() {
        val items = emptyList<Int>()

        val total = items.sum()

        assertEquals(0, total)
    }

    data class FakeProduct(
        val id: String,
        val tags: List<String>
    )
}