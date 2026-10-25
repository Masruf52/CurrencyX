package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey val code: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val category: String,
    val isFavorite: Boolean = false,
    val isPinned: Boolean = false
)

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val currencyCode: String,
    val rateAgainstUSD: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversion_history")
data class ConversionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromCode: String,
    val toCode: String,
    val fromAmount: Double,
    val toAmount: Double,
    val exchangeRate: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "rate_alerts")
data class RateAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromCode: String,
    val toCode: String,
    val targetRate: Double,
    val condition: String, // "GREATER_THAN" or "LESS_THAN"
    val isActive: Boolean = true,
    val isTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
