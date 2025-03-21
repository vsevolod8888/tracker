package com.seva.tracker.presentation.routessmallcalendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.io.wojciechosak.calendar.view.DayState
import kotlinx.datetime.LocalDate
import com.seva.tracker.io.wojciechosak.calendar.view.today

@Composable
fun CalendarDaySmall(
    state: DayState,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit = {},
    isSelected: Boolean,
    modifier: Modifier = Modifier,

    isDotVisible: Boolean = true,
) = with(state) {
    val today = LocalDate.today()
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border =  BorderStroke(if (isDotVisible) 1.dp else 1.dp, if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else if (isDotVisible)
                MaterialTheme.colorScheme.surfaceContainer
            else
                MaterialTheme.colorScheme.primary,
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (date == today) "Today ${date.dayOfMonth}" else date.dayOfMonth.toString(),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                  style = if (date == today) TextStyleLocal.semibold16 else TextStyleLocal.semibold16,
                textAlign = TextAlign.Center
            )
        }
    }
}