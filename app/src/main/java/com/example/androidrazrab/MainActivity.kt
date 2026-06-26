package com.example.androidrazrab

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ProductAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var errorContainer: View
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    private var currentCategories: List<Category> = emptyList()
    private var isUpdatingTabs = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.List)
        tabLayout = findViewById(R.id.upView)

        progressBar = findViewById(R.id.progressBar)
        errorContainer = findViewById(R.id.errorContainer)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        adapter = ProductAdapter(emptyList(), this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val factory = ProductViewModelFactory(this, applicationContext)
        viewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        setupTabLayout()
        setupBottomNavigation()
        setupRetryButton()
        observeViewModel()

        viewModel.load()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is CatalogUiState.Loading -> showLoading()

                is CatalogUiState.Error -> showError(state.message)

                is CatalogUiState.Success -> {
                    showCatalog()
                    currentCategories = state.categories
                    setupTabs(state.categories, state.selectedCategoryId)
                    adapter.updateProducts(state.visibleProducts)
                }
            }
        }
    }

    private fun setupTabLayout() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (isUpdatingTabs) return

                val category = currentCategories.getOrNull(tab?.position ?: 0)
                if (category != null) {
                    viewModel.selectCategory(category.id)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })
    }

    private fun setupTabs(
        categories: List<Category>,
        selectedCategoryId: String
    ) {
        isUpdatingTabs = true

        tabLayout.removeAllTabs()

        categories.forEach { category ->
            tabLayout.addTab(
                tabLayout.newTab().setText(category.name),
                category.id == selectedCategoryId
            )
        }

        isUpdatingTabs = false
    }

    private fun setupRetryButton() {
        retryButton.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_catalog -> {
                    recyclerView.isVisible = true
                    tabLayout.isVisible = true
                    true
                }

                R.id.menu_cart -> {
                    recyclerView.isVisible = false
                    tabLayout.isVisible = false
                    true
                }

                else -> false
            }
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        recyclerView.isVisible = false
        tabLayout.isVisible = false
        errorContainer.isVisible = false
    }

    private fun showCatalog() {
        progressBar.isVisible = false
        recyclerView.isVisible = true
        tabLayout.isVisible = true
        errorContainer.isVisible = false
    }

    private fun showError(message: String) {
        progressBar.isVisible = false
        recyclerView.isVisible = false
        tabLayout.isVisible = false
        errorContainer.isVisible = true
        errorText.text = message
    }
}