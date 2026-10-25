package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ConversionHistoryEntity
import com.example.data.local.CurrencyEntity
import com.example.data.local.ExchangeRateEntity
import com.example.data.local.RateAlertEntity
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.CurrencyRepository
import com.example.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CurrencyRepository(application)
    private val preferencesRepository = UserPreferencesRepository(application)

    // User preferences
    val themeMode: StateFlow<String> = preferencesRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val decimalPrecision: StateFlow<Int> = preferencesRepository.decimalPrecision
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    val languageCode: StateFlow<String> = preferencesRepository.languageCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val autoRefresh: StateFlow<Boolean> = preferencesRepository.autoRefresh
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Conversion State
    private val _fromCurrency = MutableStateFlow("USD")
    val fromCurrency: StateFlow<String> = _fromCurrency.asStateFlow()

    private val _toCurrency = MutableStateFlow("BDT")
    val toCurrency: StateFlow<String> = _toCurrency.asStateFlow()

    private val _amountInput = MutableStateFlow("1000")
    val amountInput: StateFlow<String> = _amountInput.asStateFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _lastUpdatedText = MutableStateFlow("Just now")
    val lastUpdatedText: StateFlow<String> = _lastUpdatedText.asStateFlow()

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Repository Flows
    val allCurrencies: StateFlow<List<CurrencyEntity>> = repository.allCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRates: StateFlow<List<ExchangeRateEntity>> = repository.allRates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<ConversionHistoryEntity>> = repository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rateAlerts: StateFlow<List<RateAlertEntity>> = repository.rateAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Currencies
    val filteredCurrencies: StateFlow<List<CurrencyEntity>> = combine(
        allCurrencies,
        _searchQuery,
        _selectedCategory
    ) { currencies, query, category ->
        currencies.filter { currency ->
            val matchesQuery = query.isEmpty() ||
                    currency.code.contains(query, ignoreCase = true) ||
                    currency.name.contains(query, ignoreCase = true) ||
                    currency.country.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" ||
                    (category == "Favorites" && currency.isFavorite) ||
                    currency.category.equals(category, ignoreCase = true)

            matchesQuery && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializePrepopulatedDataIfNeeded()
            refreshRates()
        }
    }

    fun refreshRates() {
        viewModelScope.launch {
            _isRefreshing.value = true
            when (val result = repository.refreshExchangeRates()) {
                is NetworkResult.Success -> {
                    _isOffline.value = false
                    val sdf = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault())
                    _lastUpdatedText.value = "Updated at " + sdf.format(Date())
                }
                is NetworkResult.Error -> {
                    _isOffline.value = result.isOfflineCached
                    _lastUpdatedText.value = if (result.isOfflineCached) "Offline Cache" else "Update Failed"
                }
                is NetworkResult.Loading -> {}
            }
            _isRefreshing.value = false
        }
    }

    fun onAmountChange(amount: String) {
        _amountInput.value = amount
    }

    fun setFromCurrency(code: String) {
        _fromCurrency.value = code
    }

    fun setToCurrency(code: String) {
        _toCurrency.value = code
    }

    fun swapCurrencies() {
        val temp = _fromCurrency.value
        _fromCurrency.value = _toCurrency.value
        _toCurrency.value = temp
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(code: String, currentFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(code, currentFavorite)
        }
    }

    fun performConversionAndSave() {
        val amount = _amountInput.value.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0) return

        val ratesMap = allRates.value.associateBy { it.currencyCode }
        val fromRate = ratesMap[_fromCurrency.value]?.rateAgainstUSD ?: 1.0
        val toRate = ratesMap[_toCurrency.value]?.rateAgainstUSD ?: 1.0

        // Calculate rate: from USD base
        val effectiveRate = if (fromRate > 0) toRate / fromRate else 1.0
        val convertedAmount = amount * effectiveRate

        viewModelScope.launch {
            repository.addConversionHistory(
                fromCode = _fromCurrency.value,
                toCode = _toCurrency.value,
                fromAmount = amount,
                toAmount = convertedAmount,
                rate = effectiveRate
            )
        }
    }

    fun repeatConversion(from: String, to: String, amount: Double) {
        _fromCurrency.value = from
        _toCurrency.value = to
        _amountInput.value = amount.toString()
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryItem(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun addRateAlert(fromCode: String, toCode: String, targetRate: Double, condition: String) {
        viewModelScope.launch {
            repository.addRateAlert(fromCode, toCode, targetRate, condition)
        }
    }

    fun deleteRateAlert(id: Long) {
        viewModelScope.launch {
            repository.deleteRateAlert(id)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setDecimalPrecision(precision: Int) {
        viewModelScope.launch {
            preferencesRepository.setDecimalPrecision(precision)
        }
    }

    fun setLanguageCode(language: String) {
        viewModelScope.launch {
            preferencesRepository.setLanguageCode(language)
        }
    }

    fun setAutoRefresh(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAutoRefresh(enabled)
        }
    }

    fun calculateConversionRate(from: String, to: String): Double {
        val ratesMap = allRates.value.associateBy { it.currencyCode }
        val fromRate = ratesMap[from]?.rateAgainstUSD ?: 1.0
        val toRate = ratesMap[to]?.rateAgainstUSD ?: 1.0
        return if (fromRate > 0) toRate / fromRate else 1.0
    }
}
