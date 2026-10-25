package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    // Currencies
    @Query("SELECT * FROM currencies ORDER BY isFavorite DESC, isPinned DESC, code ASC")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE code = :code LIMIT 1")
    suspend fun getCurrencyByCode(code: String): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Query("UPDATE currencies SET isFavorite = :isFavorite WHERE code = :code")
    suspend fun updateFavorite(code: String, isFavorite: Boolean)

    // Exchange Rates
    @Query("SELECT * FROM exchange_rates")
    fun getAllRates(): Flow<List<ExchangeRateEntity>>

    @Query("SELECT * FROM exchange_rates WHERE currencyCode = :code LIMIT 1")
    suspend fun getRateByCode(code: String): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<ExchangeRateEntity>)

    // Conversion History
    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<ConversionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: ConversionHistoryEntity)

    @Query("DELETE FROM conversion_history WHERE id = :id")
    suspend fun deleteHistoryItem(id: Long)

    @Query("DELETE FROM conversion_history")
    suspend fun clearHistory()

    // Rate Alerts
    @Query("SELECT * FROM rate_alerts ORDER BY createdAt DESC")
    fun getRateAlerts(): Flow<List<RateAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRateAlert(alert: RateAlertEntity)

    @Query("DELETE FROM rate_alerts WHERE id = :id")
    suspend fun deleteRateAlert(id: Long)

    @Query("UPDATE rate_alerts SET isTriggered = :isTriggered WHERE id = :id")
    suspend fun setAlertTriggered(id: Long, isTriggered: Boolean)
}
