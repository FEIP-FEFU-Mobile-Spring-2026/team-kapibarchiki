package com.example.androidrazrab
import com.example.androidrazrab.RetrofitClient
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
class ProductViewModel : ViewModel() {
    val products = MutableLiveData<List<Product>>()
    val loading = MutableLiveData(false)

    fun load() {
        loading.value = true
        viewModelScope.launch {
            products.value = try {
                RetrofitClient.productApiService.getProducts()
            } catch (e: Exception) {
                emptyList()
            }
            loading.value = false
        }
    }
}