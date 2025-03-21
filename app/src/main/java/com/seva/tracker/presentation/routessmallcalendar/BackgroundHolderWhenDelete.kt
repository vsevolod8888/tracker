package com.seva.tracker.presentation.routessmallcalendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.seva.tracker.R

@Composable
fun BackgroundHolderWhenDelete (){
    Row(
        modifier = Modifier
            //.padding(start = 20.dp, top = 5.dp, end = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Иконки для смахивания
        Icon(
            painter = painterResource(id = R.drawable.ic_delete_sweep),
            contentDescription = "",
            modifier = Modifier
                .size(30.dp),
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
}