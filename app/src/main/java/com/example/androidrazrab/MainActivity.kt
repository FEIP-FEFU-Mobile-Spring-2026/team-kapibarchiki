package com.example.androidrazrab

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.List)
        tabLayout = findViewById(R.id.upView)

        adapter = ProductAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        observeViewModel()
        viewModel.load()
        setupTabLayout()
    }
    private fun observeViewModel() {
        viewModel.products.observe(this)
        {
            products -> allProducts = products
            adapter.updateProducts(products)
        }
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> filterProducts("new")
                    1 -> filterProducts("jeans")
                    2 -> filterProducts("shirts")
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })
    }
    private fun filterProducts(category: String) {
        val allProducts = viewModel.products.value
        if (!allProducts.isNullOrEmpty())
        {
            val filteredProducts = when (category)
            {
                "jeans" -> allProducts.filter { it.category.contains("jeans", ignoreCase = true) || it.title.contains("jeans", ignoreCase = true)  }
                "shirts" -> allProducts.filter { it.category.contains("shirts", ignoreCase = true) || it.title.contains("shirts", ignoreCase = true)  || it.title.contains("short", ignoreCase = true)}
                else -> allProducts
            }
            adapter.updateProducts(filteredProducts)
        }
    }
}