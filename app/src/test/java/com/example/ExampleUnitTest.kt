package com.example

import com.example.utils.CurrencyUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCurrencyFormatting() {
        val formatted2 = CurrencyUtils.formatAmount(12250.456, 2)
        assertEquals("12250.46", formatted2)

        val formatted4 = CurrencyUtils.formatAmount(0.924512, 4)
        assertEquals("0.9245", formatted4)
    }

    @Test
    fun testCurrencySymbols() {
        assertEquals("$", CurrencyUtils.getSymbolForCode("USD"))
        assertEquals("€", CurrencyUtils.getSymbolForCode("EUR"))
        assertEquals("£", CurrencyUtils.getSymbolForCode("GBP"))
        assertEquals("৳", CurrencyUtils.getSymbolForCode("BDT"))
        assertEquals("₹", CurrencyUtils.getSymbolForCode("INR"))
    }

    @Test
    fun testCurrencyFlags() {
        assertEquals("🇺🇸", CurrencyUtils.getFlagForCode("USD"))
        assertEquals("🇪🇺", CurrencyUtils.getFlagForCode("EUR"))
        assertEquals("🇬🇧", CurrencyUtils.getFlagForCode("GBP"))
        assertEquals("🇧🇩", CurrencyUtils.getFlagForCode("BDT"))
    }
}
