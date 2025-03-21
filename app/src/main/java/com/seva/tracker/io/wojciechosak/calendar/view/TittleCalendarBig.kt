package com.seva.tracker.io.wojciechosak.calendar.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal
import androidx.compose.material3.Text
import kotlinx.datetime.Month
import java.util.Locale

@Composable
fun TittleCalendarBig(
    month: Month,
    year: Int,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.arrow_left),
            contentDescription = stringResource(R.string.prevmonth),
            modifier = Modifier
                .padding(start = 1.dp)
                .size(48.dp)
                .clickable { onClickPrevious() }, tint = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = month.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + ", " + year,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(1f),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = TextStyleLocal.bold22,
            textAlign = TextAlign.Center
        )

        Icon(painterResource(R.drawable.arrow_right),
            contentDescription = stringResource(R.string.nextmonth),
            modifier = Modifier
                .padding(end = 1.dp)
                .size(48.dp)
                .clickable { onClickNext() },
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    }
}