package com.seva.tracker.presentation.routessmallcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.presentation.bottomnavigation.NavigationItem

@Composable
fun RouteHolder(routeEntity: RouteEntity, navController: NavHostController) {
    val leftBorderColor = MaterialTheme.colorScheme.surfaceContainer//MaterialTheme.colorScheme.onSurface
    val rightBorderColor = MaterialTheme.colorScheme.surfaceContainer
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                navController.navigate(
                    "${NavigationItem.MapReady.route}/${routeEntity.id}/${routeEntity.recordRouteName}"
                )
            }
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1.3f)
                .drawWithContent {
                    drawContent()
                    val strokeWidth = 1.dp.toPx()
                    val rightStrokeWidth = 2.dp.toPx()
                    val cornerRadius = 10.dp.toPx()
                    val leftPath = Path().apply {
                        moveTo(0f, cornerRadius)
                        arcTo(
                            rect = Rect(0f, 0f, 2 * cornerRadius, 2 * cornerRadius),
                            startAngleDegrees = 180f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )

                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)

                        arcTo(
                            rect = Rect(0f, size.height - 2 * cornerRadius, 2 * cornerRadius, size.height),
                            startAngleDegrees = 90f,
                            sweepAngleDegrees = 90f,
                            forceMoveTo = false
                        )

                        close()
                    }

                    drawPath(
                        path = leftPath,
                        color = leftBorderColor,
                        style = Stroke(width = strokeWidth)
                    )

                    val rightPath = Path().apply {
                        moveTo(size.width, 0f)
                        lineTo(size.width, size.height)
                    }

                    drawPath(
                        path = rightPath,
                        color = rightBorderColor,
                        style = Stroke(width = rightStrokeWidth)
                    )
                }
,
                    verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = routeEntity.recordRouteName,
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 12.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyleLocal.semibold14,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = routeEntity.lenght,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyleLocal.semibold14,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (routeEntity.isDrawing) {
                Icon(
                    painterResource(R.drawable.ic_brush),
                    contentDescription = stringResource(R.string.nextmonth),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(30.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}