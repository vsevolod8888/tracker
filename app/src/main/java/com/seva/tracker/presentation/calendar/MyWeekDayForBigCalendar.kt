package com.seva.tracker.presentation.calendar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seva.tracker.TextStyleLocal

@Composable
fun MyWeekDayForBigCalendar(
    day: String
) {
    Text(
        text = day,
        modifier = Modifier.padding(bottom = 10.dp).wrapContentSize(),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        style = TextStyleLocal.bold18,
    )
}