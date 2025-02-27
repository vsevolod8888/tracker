package com.seva.tracker.presentation.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seva.tracker.R


@Composable
fun ToolBar(
    textToolbar: String,
    isVisiblePicturesInRight: Boolean = false,
    isVisiblePictureInLeft: Boolean = false,
    onArrowBackClick: () -> Unit,
    goToSettings: () -> Unit,
    goToCalendar: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .background(color = Color.Blue),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (isVisiblePictureInLeft){
            Image(
                painter = painterResource(R.drawable.btn_back),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(48.dp)
                    .clickable { onArrowBackClick() }
            )
        } else {
            Spacer(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(10.dp)
            )
        }
        Text(
            text = textToolbar,
          //  style = TextStyleLocal.headerSmall,
            modifier = Modifier
                .padding(start = 5.dp)
                .weight(1f),
            color = Color.White,
            textAlign = TextAlign.Start
        )
        if (isVisiblePicturesInRight) {

                Image(
                    painter = painterResource(R.drawable.btn_calendar),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(48.dp)
                        .clickable {
                            goToCalendar()
                        }
                )
                Image(
                    painter = painterResource(R.drawable.btn_settings),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(48.dp)
                        .clickable {
                            goToSettings()
                        }
                )


        } else {
            Spacer(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .size(48.dp)
            )
        }


    }
}