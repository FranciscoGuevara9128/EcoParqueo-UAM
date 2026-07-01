package com.uam.ecoparqueo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uam.ecoparqueo.model.RegistroAcceso

data class ParetoItem(
    val label: String,
    val value: Int
)

@Composable
fun ParetoChartCard(
    registros: List<RegistroAcceso>,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    
    val chartItems = remember(registros) {
        if (registros.isEmpty()) {
            listOf(
                ParetoItem("Recepción", 210),
                ParetoItem("Plazoleta", 140),
                ParetoItem("Observatorio", 75),
                ParetoItem("FIA", 35),
                ParetoItem("Edificio M", 15)
            )
        } else {
            registros.groupBy { it.parqueoNombre }
                .map { (parqueo, list) ->
                    val cleanName = when {
                        parqueo.contains("Plazoleta", ignoreCase = true) -> "Plazoleta"
                        parqueo.contains("Recepción", ignoreCase = true) -> "Recepción"
                        parqueo.contains("edificio C", ignoreCase = true) -> "Edificio C"
                        parqueo.contains("Clinicas", ignoreCase = true) -> "Clínicas"
                        parqueo.contains("Observatorio", ignoreCase = true) -> "Observatorio"
                        parqueo.contains("Biblioteca", ignoreCase = true) -> "Biblioteca"
                        parqueo.contains("Edificio M", ignoreCase = true) -> "Edificio M"
                        parqueo.contains("FIA", ignoreCase = true) -> "FIA"
                        else -> if (parqueo.isBlank()) "Otro" else parqueo.take(10)
                    }
                    ParetoItem(cleanName, list.size)
                }
                .sortedByDescending { it.value }
                .take(5)
        }
    }

    val sumOfValues = remember(chartItems) {
        chartItems.sumOf { it.value }.toFloat().coerceAtLeast(1f)
    }

    val cumulativePercentages = remember(chartItems, sumOfValues) {
        var runningSum = 0f
        chartItems.map { item ->
            runningSum += item.value
            (runningSum / sumOfValues) * 100f
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Análisis de Ocupación UAM",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            Text(
                text = buildAnnotatedString {
                    append("Gráfico de Pareto · ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colorScheme.primary)) {
                        append("Zonas de mayor flujo")
                    }
                },
                fontSize = 12.sp,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            val textMeasurer = rememberTextMeasurer()
            
            // Colores del gráfico
            val barColor = colorScheme.primary.copy(alpha = 0.85f)
            val lineColor = Color(0xFFE65100) // Naranja / Rojo oscuro para la curva acumulada
            val gridColor = colorScheme.outline.copy(alpha = 0.12f)
            val textColor = colorScheme.onSurfaceVariant
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                
                val leftPadding = 45f  // espacio para el eje Y izquierdo (Vehículos)
                val rightPadding = 45f // espacio para el eje Y derecho (%)
                val bottomPadding = 35f // espacio para etiquetas del eje X
                val topPadding = 25f   // margen superior
                
                val chartWidth = canvasWidth - leftPadding - rightPadding
                val chartHeight = canvasHeight - topPadding - bottomPadding
                
                val maxVal = chartItems.maxOf { it.value }.toFloat().coerceAtLeast(10f)
                // Redondear maxVal al múltiplo de 50 más cercano para escala bonita
                val yMax = (kotlin.math.ceil(maxVal / 50f) * 50f).coerceAtLeast(50f)
                
                // 1. Dibujar líneas de cuadrícula y etiquetas del Eje Y
                val gridSteps = 4
                for (j in 0..gridSteps) {
                    val ratio = j.toFloat() / gridSteps
                    val y = topPadding + chartHeight * (1 - ratio)
                    
                    // Línea horizontal
                    drawLine(
                        color = gridColor,
                        start = Offset(leftPadding, y),
                        end = Offset(canvasWidth - rightPadding, y),
                        strokeWidth = 1f
                    )
                    
                    // Etiqueta izquierda (Cantidad de Vehículos)
                    val leftLabel = (ratio * yMax).toInt().toString()
                    drawText(
                        textMeasurer = textMeasurer,
                        text = leftLabel,
                        topLeft = Offset(5f, y - 10f),
                        style = TextStyle(color = textColor, fontSize = 9.sp)
                    )
                    
                    // Etiqueta derecha (Porcentaje Acumulado)
                    val rightLabel = "${(ratio * 100).toInt()}%"
                    drawText(
                        textMeasurer = textMeasurer,
                        text = rightLabel,
                        topLeft = Offset(canvasWidth - rightPadding + 8f, y - 10f),
                        style = TextStyle(color = textColor, fontSize = 9.sp)
                    )
                }
                
                // 2. Dibujar las barras e hitos de la curva acumulada
                val n = chartItems.size
                val spaceBetweenItems = chartWidth / n
                val barWidth = spaceBetweenItems * 0.55f
                val barOffset = spaceBetweenItems * 0.225f
                
                val points = ArrayList<Offset>()
                
                chartItems.forEachIndexed { index, item ->
                    val xLeft = leftPadding + index * spaceBetweenItems + barOffset
                    val barHeight = (item.value / yMax) * chartHeight
                    val yTop = topPadding + chartHeight - barHeight
                    
                    // Dibujar barra con esquinas redondeadas
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(xLeft, yTop),
                        size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                    
                    // Escribir el valor en la cima de la barra
                    drawText(
                        textMeasurer = textMeasurer,
                        text = item.value.toString(),
                        topLeft = Offset(xLeft + (barWidth / 2f) - 10f, (yTop - 16f).coerceAtLeast(5f)),
                        style = TextStyle(color = barColor, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                    )
                    
                    // Escribir la etiqueta del Parqueo abajo
                    drawText(
                        textMeasurer = textMeasurer,
                        text = if (item.label.length > 8) item.label.take(7) + "." else item.label,
                        topLeft = Offset(xLeft - 10f, topPadding + chartHeight + 8f),
                        style = TextStyle(color = textColor, fontSize = 9.sp, textAlign = TextAlign.Center)
                    )
                    
                    // Calcular punto de la curva de Pareto (acumulada)
                    val cumPercent = cumulativePercentages[index]
                    val pX = xLeft + (barWidth / 2f)
                    val pY = topPadding + chartHeight - (cumPercent / 100f) * chartHeight
                    points.add(Offset(pX, pY))
                }
                
                // 3. Dibujar la línea de curva acumulada (Pareto)
                if (points.isNotEmpty()) {
                    val path = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (k in 1 until points.size) {
                            lineTo(points[k].x, points[k].y)
                        }
                    }
                    
                    // Dibujar trazo de la línea
                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    
                    // Dibujar círculos indicadores en cada punto
                    points.forEachIndexed { idx, pt ->
                        // Círculo exterior blanco
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = pt
                        )
                        // Círculo interior naranja
                        drawCircle(
                            color = lineColor,
                            radius = 4.dp.toPx(),
                            center = pt
                        )
                        
                        // Etiqueta del % acumulado sobre el punto
                        val pctLabel = "${cumulativePercentages[idx].toInt()}%"
                        drawText(
                            textMeasurer = textMeasurer,
                            text = pctLabel,
                            topLeft = Offset(pt.x + 8f, pt.y - 12f),
                            style = TextStyle(color = lineColor, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            // Leyenda explicativa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(barColor, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Vehículos ocupados",
                    fontSize = 11.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(20.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(lineColor, RoundedCornerShape(5.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "% Acumulado (Pareto)",
                    fontSize = 11.sp,
                    color = textColor
                )
            }
        }
    }
}
