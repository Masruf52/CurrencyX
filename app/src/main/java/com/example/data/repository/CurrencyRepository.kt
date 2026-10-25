package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.ConversionHistoryEntity
import com.example.data.local.CurrencyEntity
import com.example.data.local.ExchangeRateEntity
import com.example.data.local.RateAlertEntity
import com.example.data.remote.ExchangeRateApi
import com.example.utils.CurrencyUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T, val isOfflineCached: Boolean = false) : NetworkResult<T>()
    data class Error(val message: String, val isOfflineCached: Boolean = true) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

class CurrencyRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.currencyDao()

    private val api: ExchangeRateApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ExchangeRateApi::class.java)
    }

    val allCurrencies: Flow<List<CurrencyEntity>> = dao.getAllCurrencies()
    val allRates: Flow<List<ExchangeRateEntity>> = dao.getAllRates()
    val history: Flow<List<ConversionHistoryEntity>> = dao.getHistory()
    val rateAlerts: Flow<List<RateAlertEntity>> = dao.getRateAlerts()

    suspend fun initializePrepopulatedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingCurrencies = dao.getAllCurrencies().firstOrNull()
        if (existingCurrencies.isNullOrEmpty()) {
            val entities = CurrencyUtils.INITIAL_CURRENCIES.map { prep ->
                CurrencyEntity(
                    code = prep.code,
                    name = prep.name,
                    country = prep.country,
                    flagEmoji = prep.flagEmoji,
                    category = prep.category,
                    isFavorite = prep.code == "USD" || prep.code == "EUR" || prep.code == "GBP" || prep.code == "BDT"
                )
            }
            dao.insertCurrencies(entities)

            // Seed initial exchange rates
            val initialRates = CurrencyUtils.INITIAL_CURRENCIES.map { prep ->
                ExchangeRateEntity(
                    currencyCode = prep.code,
                    rateAgainstUSD = prep.defaultRateUSD,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            dao.insertRates(initialRates)
        }
    }

    suspend fun refreshExchangeRates(): NetworkResult<List<ExchangeRateEntity>> = withContext(Dispatchers.IO) {
        try {
            initializePrepopulatedDataIfNeeded()
            val response = api.getLatestRates("USD")
            if (response.isSuccessful && response.body() != null && response.body()?.rates != null) {
                val ratesMap = response.body()!!.rates!!
                val timestamp = (response.body()!!.timeLastUpdateUnix ?: (System.currentTimeMillis() / 1000)) * 1000

                val newRates = ratesMap.map { (code, rate) ->
                    ExchangeRateEntity(
                        currencyCode = code,
                        rateAgainstUSD = rate,
                        lastUpdated = timestamp
                    )
                }
                dao.insertRates(newRates)

                // Update currencies database if new currencies were found in API
                val currentCurrencies = dao.getAllCurrencies().firstOrNull() ?: emptyList()
                val existingCodes = currentCurrencies.map { it.code }.toSet()
                val missingCodes = ratesMap.keys.filter { !existingCodes.contains(it) }

                if (missingCodes.isNotEmpty()) {
                    val missingEntities = missingCodes.map { code ->
                        CurrencyEntity(
                            code = code,
                            name = "$code Currency",
                            country = code.take(2),
                            flagEmoji = CurrencyUtils.getFlagForCode(code),
                            category = "Other"
                        )
                    }
                    dao.insertCurrencies(missingEntities)
                }

                NetworkResult.Success(newRates, isOfflineCached = false)
            } else {
                val cached = dao.getAllRates().firstOrNull() ?: emptyList()
                NetworkResult.Error("API Error: ${response.message()}", isOfflineCached = cached.isNotEmpty())
            }
        } catch (e: Exception) {
            val cached = dao.getAllRates().firstOrNull() ?: emptyList()
            NetworkResult.Error("Offline mode: ${e.localizedMessage ?: "Network unavailable"}", isOfflineCached = cached.isNotEmpty())
        }
    }

    suspend fun toggleFavorite(code: String, currentFavorite: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFavorite(code, !currentFavorite)
    }

    suspend fun addConversionHistory(
        fromCode: String,
        toCode: String,
        fromAmount: Double,
        toAmount: Double,
        rate: Double
    ) = withContext(Dispatchers.IO) {
        val historyItem = ConversionHistoryEntity(
            fromCode = fromCode,
            toCode = toCode,
            fromAmount = fromAmount,
            toAmount = toAmount,
            exchangeRate = rate,
            timestamp = System.currentTimeMillis()
        )
        dao.insertHistory(historyItem)
    }

    suspend fun deleteHistoryItem(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteHistoryItem(id)
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        dao.clearHistory()
    }

    suspend fun addRateAlert(
        fromCode: String,
        toCode: String,
        targetRate: Double,
        condition: String
    ) = withContext(Dispatchers.IO) {
        val alert = RateAlertEntity(
            fromCode = fromCode,
            toCode = toCode,
            targetRate = targetRate,
            condition = condition,
            isActive = true,
            createdAt = System.currentTimeMillis()
        )
        dao.insertRateAlert(alert)
    }

    suspend fun deleteRateAlert(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteRateAlert(id)
    }
}
