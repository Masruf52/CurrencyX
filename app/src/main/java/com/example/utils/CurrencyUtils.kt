package com.example.utils

data class PrepopulatedCurrency(
    val code: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val category: String,
    val defaultRateUSD: Double
)

object CurrencyUtils {

    val POPULAR_CURRENCIES = listOf("USD", "EUR", "GBP", "BDT", "INR", "JPY", "CNY", "CAD", "AUD", "SAR", "AED")

    val INITIAL_CURRENCIES = listOf(
        PrepopulatedCurrency("USD", "United States Dollar", "United States", "🇺🇸", "Majors", 1.0),
        PrepopulatedCurrency("EUR", "Euro", "European Union", "🇪🇺", "Europe", 0.9245),
        PrepopulatedCurrency("GBP", "British Pound Sterling", "United Kingdom", "🇬🇧", "Europe", 0.7892),
        PrepopulatedCurrency("BDT", "Bangladeshi Taka", "Bangladesh", "🇧🇩", "Asia", 122.50),
        PrepopulatedCurrency("INR", "Indian Rupee", "India", "🇮🇳", "Asia", 86.40),
        PrepopulatedCurrency("JPY", "Japanese Yen", "Japan", "🇯🇵", "Asia", 154.20),
        PrepopulatedCurrency("CNY", "Chinese Yuan", "China", "🇨🇳", "Asia", 7.28),
        PrepopulatedCurrency("CAD", "Canadian Dollar", "Canada", "🇨🇦", "Americas", 1.41),
        PrepopulatedCurrency("AUD", "Australian Dollar", "Australia", "🇦🇺", "Majors", 1.58),
        PrepopulatedCurrency("SAR", "Saudi Riyal", "Saudi Arabia", "🇸🇦", "Middle East", 3.75),
        PrepopulatedCurrency("AED", "United Arab Emirates Dirham", "United Arab Emirates", "🇦🇪", "Middle East", 3.67),
        PrepopulatedCurrency("CHF", "Swiss Franc", "Switzerland", "🇨🇭", "Europe", 0.8850),
        PrepopulatedCurrency("SGD", "Singapore Dollar", "Singapore", "🇸🇬", "Asia", 1.345),
        PrepopulatedCurrency("MYR", "Malaysian Ringgit", "Malaysia", "🇲🇾", "Asia", 4.42),
        PrepopulatedCurrency("NZD", "New Zealand Dollar", "New Zealand", "🇳🇿", "Majors", 1.72),
        PrepopulatedCurrency("BRL", "Brazilian Real", "Brazil", "🇧🇷", "Americas", 5.75),
        PrepopulatedCurrency("ZAR", "South African Rand", "South Africa", "🇿🇦", "Africa", 18.10),
        PrepopulatedCurrency("KRW", "South Korean Won", "South Korea", "🇰🇷", "Asia", 1395.0),
        PrepopulatedCurrency("MXN", "Mexican Peso", "Mexico", "🇲🇽", "Americas", 20.30),
        PrepopulatedCurrency("HKD", "Hong Kong Dollar", "Hong Kong", "🇭🇰", "Asia", 7.78),
        PrepopulatedCurrency("RUB", "Russian Ruble", "Russia", "🇷🇺", "Europe", 98.50),
        PrepopulatedCurrency("TRY", "Turkish Lira", "Turkey", "🇹🇷", "Middle East", 34.50),
        PrepopulatedCurrency("IDR", "Indonesian Rupiah", "Indonesia", "🇮🇩", "Asia", 15850.0),
        PrepopulatedCurrency("THB", "Thai Baht", "Thailand", "🇹🇭", "Asia", 34.80),
        PrepopulatedCurrency("SEK", "Swedish Krona", "Sweden", "🇸🇪", "Europe", 10.85),
        PrepopulatedCurrency("NOK", "Norwegian Krone", "Norway", "🇳🇴", "Europe", 11.05),
        PrepopulatedCurrency("DKK", "Danish Krone", "Denmark", "🇩🇰", "Europe", 6.89),
        PrepopulatedCurrency("PLN", "Polish Zloty", "Poland", "🇵🇱", "Europe", 4.05),
        PrepopulatedCurrency("EGP", "Egyptian Pound", "Egypt", "🇪🇬", "Middle East", 49.50),
        PrepopulatedCurrency("PKR", "Pakistani Rupee", "Pakistan", "🇵🇰", "Asia", 278.50),
        PrepopulatedCurrency("LKR", "Sri Lankan Rupee", "Sri Lanka", "🇱🇰", "Asia", 291.0),
        PrepopulatedCurrency("NPR", "Nepalese Rupee", "Nepal", "🇳🇵", "Asia", 138.2),
        PrepopulatedCurrency("PHP", "Philippine Peso", "Philippines", "🇵🇭", "Asia", 58.60),
        PrepopulatedCurrency("VND", "Vietnamese Dong", "Vietnam", "🇻🇳", "Asia", 25380.0),
        PrepopulatedCurrency("QAR", "Qatari Riyal", "Qatar", "🇶🇦", "Middle East", 3.64),
        PrepopulatedCurrency("KWD", "Kuwaiti Dinar", "Kuwait", "🇰🇼", "Middle East", 0.308),
        PrepopulatedCurrency("BHD", "Bahraini Dinar", "Bahrain", "🇧🇭", "Middle East", 0.377),
        PrepopulatedCurrency("OMR", "Omani Rial", "Oman", "🇴🇲", "Middle East", 0.385),
        PrepopulatedCurrency("JOD", "Jordanian Dinar", "Jordan", "🇯🇴", "Middle East", 0.709),
        PrepopulatedCurrency("IQD", "Iraqi Dinar", "Iraq", "🇮🇶", "Middle East", 1310.0),
        PrepopulatedCurrency("CLP", "Chilean Peso", "Chile", "🇨🇱", "Americas", 975.0),
        PrepopulatedCurrency("COP", "Colombian Peso", "Colombia", "🇨🇴", "Americas", 4380.0),
        PrepopulatedCurrency("PEN", "Peruvian Sol", "Peru", "🇵🇪", "Americas", 3.78),
        PrepopulatedCurrency("ARS", "Argentine Peso", "Argentina", "🇦🇷", "Americas", 1005.0),
        PrepopulatedCurrency("CZK", "Czech Koruna", "Czech Republic", "🇨🇿", "Europe", 23.50),
        PrepopulatedCurrency("HUF", "Hungarian Forint", "Hungary", "🇭🇺", "Europe", 382.0),
        PrepopulatedCurrency("ILS", "Israeli Shekel", "Israel", "🇮🇱", "Middle East", 3.72),
        PrepopulatedCurrency("MAD", "Moroccan Dirham", "Morocco", "🇲🇦", "Africa", 10.05),
        PrepopulatedCurrency("NGN", "Nigerian Naira", "Nigeria", "🇳🇬", "Africa", 1680.0),
        PrepopulatedCurrency("KES", "Kenyan Shilling", "Kenya", "🇰🇪", "Africa", 129.5)
    )

    fun getSymbolForCode(code: String): String {
        return when (code.uppercase()) {
            "USD", "CAD", "AUD", "NZD", "HKD", "SGD", "ARS", "CLP", "COP", "MXN" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "BDT" -> "৳"
            "INR" -> "₹"
            "JPY", "CNY" -> "¥"
            "KRW" -> "₩"
            "RUB" -> "₽"
            "TRY" -> "₺"
            "THB" -> "฿"
            "PHP" -> "₱"
            "VND" -> "₫"
            "SAR", "AED", "QAR", "OAR" -> "ر.س"
            "EGP" -> "E£"
            "PKR", "LKR", "NPR" -> "₨"
            else -> code
        }
    }

    fun getFlagForCode(code: String): String {
        val found = INITIAL_CURRENCIES.find { it.code.equals(code, ignoreCase = true) }
        if (found != null) return found.flagEmoji

        // Calculate flag emoji from currency code fallback (e.g. US -> 🇺🇸)
        if (code.length >= 2) {
            val countryCode = code.substring(0, 2).uppercase()
            val firstChar = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6
            val secondChar = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        }
        return "🌐"
    }

    fun formatAmount(amount: Double, precision: Int = 2): String {
        return String.format("%.${precision}f", amount)
    }
}
