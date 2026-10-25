package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GrowthGreen
import kotlin.math.sin

data class ChartPoint(val label: String, val rate: Double)

@Composable
fun HistoricalRateChart(
    fromCode: String,
    toCode: String,
    currentRate: Double,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf("30D") }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    // Generate mock historical points based on selected period
    val points = remember(selectedPeriod, currentRate) {
        val count = when (selectedPeriod) {
            "1D" -> 8
            "7D" -> 7
            "30D" -> 15
            "90D" -> 20
            "1Y" -> 12
            else -> 10
        }

        List(count) { i ->
            val variation = sin(i * 0.8) * 0.035 * currentRate
            val rate = (currentRate + variation).coerceAtLeast(0.0001)
            val label = when (selectedPeriod) {
                "1D" -> "${8 + i * 2}:00"
                "7D" -> "Day ${i + 1}"
                "30D" -> "Day ${i * 2 + 1}"
                "90D" -> "Wk ${i + 1}"
                "1Y" -> "M${i + 1}"
                else -> "P${i + 1}"
            }
            ChartPoint(label, rate)
        }
    }

    val minRate = points.minOfOrNull { it.rate } ?: currentRate
    val maxRate = points.maxOfOrNull { it.rate } ?: currentRate
    val firstRate = points.firstOrNull()?.rate ?: currentRate
    val lastRate = points.lastOrNull()?.rate ?: currentRate
    val changePercent = if (firstRate > 0) ((lastRate - firstRate) / firstRate) * 100 else 0.0

    val primaryColor = MaterialTheme.colorScheme.primary
    val isPositive = changePercent >= 0
    val trendColor = if (isPositive) GrowthGreen else MaterialTheme.colorScheme.error

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$fromCode / $toCode Rate Chart",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format("%.4f", lastRate),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(trendColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = String.format("%s%.2f%%", if (isPositive) "+" else "", changePercent),
                    color = trendColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Period Selector Chips
        val periods = listOf("1D", "7D", "30D", "90D", "1Y")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            periods.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = {
                        selectedPeriod = period
                        selectedPointIndex = null
                    },
                    label = { Text(period, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Tooltip
        selectedPointIndex?.let { index ->
            if (index in points.indices) {
                val pt = points[index]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${pt.label}: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = String.format("%.4f", pt.rate),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = primaryColor
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Canvas Chart
        val gradientBrush = Brush.verticalGradient(
            colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        val pointWidth = size.width / (points.size - 1).coerceAtLeast(1)
                        val index = (offset.x / pointWidth).toInt().coerceIn(0, points.size - 1)
                        selectedPointIndex = index
                    }
                }
        ) {
            if (points.isEmpty()) return@Canvas

            val width = size.width
            val height = size.height
            val range = (maxRate - minRate).coerceAtLeast(0.00001)

            val coordinates = points.mapIndexed { index, point ->
                val x = index * (width / (points.size - 1).coerceAtLeast(1))
                val y = height - ((point.rate - minRate) / range * (height - 30.dp.toPx())).toFloat() - 15.dp.toPx()
                Offset(x, y)
            }

            val strokePath = Path().apply {
                moveTo(coordinates.first().x, coordinates.first().y)
                for (i in 0 until coordinates.size - 1) {
                    val p1 = coordinates[i]
                    val p2 = coordinates[i + 1]
                    val control1 = Offset((p1.x + p2.x) / 2, p1.y)
                    val control2 = Offset((p1.x + p2.x) / 2, p2.y)
                    cubicTo(control1.x, control1.y, control2.x, control2.y, p2.x, p2.y)
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(path = fillPath, brush = gradientBrush)
            drawPath(
                path = strokePath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw point circle for selected item or last item
            val highlightIndex = selectedPointIndex ?: (coordinates.size - 1)
            if (highlightIndex in coordinates.indices) {
                val point = coordinates[highlightIndex]
                drawCircle(
                    color = primaryColor,
                    radius = 6.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = point
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // High & Low Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Low: ${String.format("%.4f", minRate)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "High: ${String.format("%.4f", maxRate)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
