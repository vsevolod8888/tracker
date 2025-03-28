package com.seva.tracker.presentation.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seva.tracker.TextStyleLocal

@Composable
fun NoRoutesText(modifier: Modifier = Modifier, text: String) {

    Box(modifier = modifier, contentAlignment = Alignment.Center) {

        Box(
            modifier = modifier
                .wrapContentSize()
                .border(
                    1.dp, MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(50.dp)
                )
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(22.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyleLocal.semibold14,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}