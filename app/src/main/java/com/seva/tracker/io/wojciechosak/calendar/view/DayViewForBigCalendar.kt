package com.seva.tracker.io.wojciechosak.calendar.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seva.tracker.TextStyleLocal
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

@Composable
fun DayViewForBigCalendar(
    date: LocalDate,
    isCurrentMonth: Boolean,
    onClick: () -> Unit = {},
    isSelected: Boolean,
    isDotVisible: Boolean = true,
    modifier: Modifier = Modifier,
    selectedMonth: Month,
    selectedYear: Int,
) {
    Box {
        val today = LocalDate.today()
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.aspectRatio(1f).padding(3.dp),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(if (isDotVisible) 1.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else if (isDotVisible)
                    MaterialTheme.colorScheme.surfaceContainer
                else
                    MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(
                text = "${date.dayOfMonth}",
                color = if (date == today && isCurrentMonth) MaterialTheme.colorScheme.onPrimaryContainer
                //   else if (!isCurrentMonth) TextSecondary
                else if (date.month == selectedMonth && date.year == selectedYear) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onPrimaryContainer,
                style = if (date == today && isCurrentMonth) TextStyleLocal.bold24 else TextStyleLocal.semibold16,
                textAlign = TextAlign.Center
            )
        }
//        if (isDotVisible) {
//            Canvas(
//                modifier =
//                Modifier
//                    .padding(bottom = 7.dp)
//                    .size(4.dp)
//                    .align(Alignment.BottomCenter),
//                onDraw = { drawCircle(color = Color.White) },
//            )
//        }
    }
}
