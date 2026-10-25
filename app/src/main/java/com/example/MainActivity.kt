package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.components.CurrencySelectorBottomSheet
import com.example.ui.components.RateAlertModal
import com.example.ui.screens.ChartsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveRatesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CurrencyXTheme
import com.example.ui.viewmodel.CurrencyViewModel

enum class NavigationScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Default.Home, Icons.Outlined.Home),
    RATES("Rates", Icons.Default.CurrencyExchange, Icons.Outlined.CurrencyExchange),
    HISTORY("History", Icons.Default.History, Icons.Outlined.History),
    CHARTS("Charts", Icons.Default.Insights, Icons.Outlined.Insights),
    SETTINGS("Settings", Icons.Default.Settings, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: CurrencyViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val useDarkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            CurrencyXTheme(darkTheme = useDarkTheme) {
                var currentTab by remember { mutableStateOf(NavigationScreen.HOME) }

                // Currency Picker Modal Sheet State
                var activePickerType by remember { mutableStateOf<String?>(null) } // "FROM" or "TO"
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                val filteredCurrencies by viewModel.filteredCurrencies.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()
                val selectedCategory by viewModel.selectedCategory.collectAsState()

                val fromCode by viewModel.fromCurrency.collectAsState()
                val toCode by viewModel.toCurrency.collectAsState()
                val currentRate = viewModel.calculateConversionRate(fromCode, toCode)

                // Rate Alert Modal State
                var showRateAlertModal by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationScreen.values().forEach { screen ->
                                val isSelected = currentTab == screen
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = screen },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = { Text(screen.title) },
                                    modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            NavigationScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onOpenFromPicker = { activePickerType = "FROM" },
                                onOpenToPicker = { activePickerType = "TO" },
                                onNavigateToFavorites = {
                                    viewModel.setSelectedCategory("Favorites")
                                    currentTab = NavigationScreen.RATES
                                },
                                onNavigateToHistory = { currentTab = NavigationScreen.HISTORY },
                                onNavigateToCharts = { currentTab = NavigationScreen.CHARTS },
                                onOpenRateAlertModal = { showRateAlertModal = true }
                            )

                            NavigationScreen.RATES -> LiveRatesScreen(
                                viewModel = viewModel,
                                onSelectCurrencyForConversion = { code ->
                                    viewModel.setToCurrency(code)
                                    currentTab = NavigationScreen.HOME
                                }
                            )

                            NavigationScreen.HISTORY -> HistoryScreen(
                                viewModel = viewModel,
                                onRepeatConversion = { from, to, amount ->
                                    viewModel.repeatConversion(from, to, amount)
                                    currentTab = NavigationScreen.HOME
                                }
                            )

                            NavigationScreen.CHARTS -> ChartsScreen(
                                viewModel = viewModel,
                                onOpenFromPicker = { activePickerType = "FROM" },
                                onOpenToPicker = { activePickerType = "TO" },
                                onOpenRateAlertModal = { showRateAlertModal = true }
                            )

                            NavigationScreen.SETTINGS -> SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }

                    // Currency Selector Bottom Sheet Modal
                    activePickerType?.let { pickerType ->
                        CurrencySelectorBottomSheet(
                            sheetState = sheetState,
                            title = if (pickerType == "FROM") "Select 'You Send' Currency" else "Select 'They Receive' Currency",
                            currencies = filteredCurrencies,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onCategorySelect = { viewModel.setSelectedCategory(it) },
                            onCurrencySelect = { currency ->
                                if (pickerType == "FROM") {
                                    viewModel.setFromCurrency(currency.code)
                                } else {
                                    viewModel.setToCurrency(currency.code)
                                }
                            },
                            onToggleFavorite = { code, isFav ->
                                viewModel.toggleFavorite(code, isFav)
                            },
                            onDismiss = { activePickerType = null }
                        )
                    }

                    // Rate Alert Modal Dialog
                    if (showRateAlertModal) {
                        RateAlertModal(
                            fromCode = fromCode,
                            toCode = toCode,
                            currentRate = currentRate,
                            onSetAlert = { targetRate, condition ->
                                viewModel.addRateAlert(fromCode, toCode, targetRate, condition)
                                Toast.makeText(
                                    this@MainActivity,
                                    "Rate Alert saved for 1 $fromCode = $targetRate $toCode!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onDismiss = { showRateAlertModal = false }
                        )
                    }
                }
            }
        }
    }
}
