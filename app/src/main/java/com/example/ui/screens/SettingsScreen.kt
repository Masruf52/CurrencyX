package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CurrencyViewModel

@Composable
fun SettingsScreen(
    viewModel: CurrencyViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val themeMode by viewModel.themeMode.collectAsState()
    val precision by viewModel.decimalPrecision.collectAsState()
    val languageCode by viewModel.languageCode.collectAsState()
    val autoRefresh by viewModel.autoRefresh.collectAsState()

    var showLanguageModal by remember { mutableStateOf(false) }
    var showPrecisionModal by remember { mutableStateOf(false) }
    var showInfoModalTitle by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        // User Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "CurrencyX Guest User",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Guest Mode Active • Local Sync",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Appearance Section
        Text(
            text = "APPEARANCE & THEME",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                SettingsRowSwitch(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Theme",
                    subtitle = if (themeMode == "DARK") "Dark Enabled" else "Light / System Default",
                    checked = themeMode == "DARK",
                    onCheckedChange = { isChecked ->
                        viewModel.setThemeMode(if (isChecked) "DARK" else "LIGHT")
                    }
                )
            }
        }

        // Preferences Section
        Text(
            text = "PREFERENCES",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                val currentLangName = when (languageCode) {
                    "bn" -> "বাংলা (Bangla)"
                    "ar" -> "العربية (Arabic)"
                    "hi" -> "हिंदी (Hindi)"
                    else -> "English (US)"
                }

                SettingsRowClickable(
                    icon = Icons.Default.Language,
                    title = "Language",
                    value = currentLangName,
                    onClick = { showLanguageModal = true }
                )

                SettingsRowSwitch(
                    icon = Icons.Default.Refresh,
                    title = "Auto Refresh Rates",
                    subtitle = "Hourly background sync",
                    checked = autoRefresh,
                    onCheckedChange = { viewModel.setAutoRefresh(it) }
                )

                SettingsRowClickable(
                    icon = Icons.Default.Numbers,
                    title = "Decimal Precision",
                    value = "$precision places",
                    onClick = { showPrecisionModal = true }
                )
            }
        }

        // Support & Legal Section
        Text(
            text = "ABOUT & LEGAL",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                SettingsRowClickable(
                    icon = Icons.Default.Info,
                    title = "About CurrencyX",
                    value = "",
                    onClick = { showInfoModalTitle = "About CurrencyX" }
                )

                SettingsRowClickable(
                    icon = Icons.Default.Policy,
                    title = "Privacy Policy",
                    value = "",
                    onClick = { showInfoModalTitle = "Privacy Policy" }
                )

                SettingsRowClickable(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    value = "",
                    onClick = { showInfoModalTitle = "Terms of Service" }
                )
            }
        }

        // App Footer Info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CurrencyX v2.4.1 (Build 892)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Built with Jetpack Compose, Room & MVVM",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    // Language Picker Dialog
    if (showLanguageModal) {
        AlertDialog(
            onDismissRequest = { showLanguageModal = false },
            title = { Text("Select App Language") },
            text = {
                Column {
                    val langs = listOf(
                        "en" to "English (US)",
                        "bn" to "বাংলা (Bangla)",
                        "ar" to "العربية (Arabic)",
                        "hi" to "हिंदी (Hindi)"
                    )
                    langs.forEach { (code, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguageCode(code)
                                    showLanguageModal = false
                                    Toast.makeText(context, "Language set to $label", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = languageCode == code,
                                onClick = {
                                    viewModel.setLanguageCode(code)
                                    showLanguageModal = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageModal = false }) { Text("Close") }
            }
        )
    }

    // Decimal Precision Dialog
    if (showPrecisionModal) {
        AlertDialog(
            onDismissRequest = { showPrecisionModal = false },
            title = { Text("Decimal Precision") },
            text = {
                Column {
                    listOf(2, 3, 4).forEach { p ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDecimalPrecision(p)
                                    showPrecisionModal = false
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = precision == p,
                                onClick = {
                                    viewModel.setDecimalPrecision(p)
                                    showPrecisionModal = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "$p Decimal Places (e.g. ${String.format("%.${p}f", 12.34567)})")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrecisionModal = false }) { Text("Close") }
            }
        )
    }

    // Info Modal Dialog
    showInfoModalTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { showInfoModalTitle = null },
            title = { Text(title) },
            text = {
                val infoText = when (title) {
                    "About CurrencyX" -> "CurrencyX is a fast, modern, scalable currency converter application. Supports 150+ real-time exchange rates, offline cached rates, favorites, conversion history, rate alert notifications, and historical rate charts."
                    "Privacy Policy" -> "CurrencyX respects your privacy. All your conversion history, rate alerts, and favorite currencies are stored 100% locally on your device in an encrypted Room SQLite Database."
                    else -> "Terms of Service: Exchange rates are retrieved from public market APIs for informational and calculation purposes."
                }
                Text(text = infoText, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = { showInfoModalTitle = null }) { Text("OK") }
            }
        )
    }
}

@Composable
fun SettingsRowSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsRowClickable(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
