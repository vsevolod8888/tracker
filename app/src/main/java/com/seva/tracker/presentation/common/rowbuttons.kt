package com.seva.tracker.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seva.tracker.R
import kotlin.math.absoluteValue

@Composable
fun rowbuttons(
    modifier: Modifier,
    isClickedSportRowww: (Int) -> Unit
) {
    var isClickedSportRow by remember { mutableStateOf(1) }
    Row(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier
                .height(50.dp)
                .width(0.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isClickedSportRow.absoluteValue == 1)
                        colorResource(id = R.color.purple_500)
                    else
                        colorResource(id = R.color.purple_200)
                )
                .weight(1f)
                .clickable {
                    isClickedSportRowww(1)
                    isClickedSportRow = 1

                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

               Text(
                    text = "Routes",
                    style = TextStyle(
                        colorResource(id = R.color.white),

                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize =
                        12.sp
                    ),
                    modifier = Modifier
                        .wrapContentHeight()
                        .width(0.dp)
                        .weight(2f)
                )
        }
        Row(
            modifier = Modifier
                .height(50.dp)
                .width(0.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isClickedSportRow.absoluteValue == 2)
                        colorResource(id = R.color.purple_500)
                    else
                        colorResource(id = R.color.purple_200)
                )
                .weight(1f)
                .clickable {
                    isClickedSportRowww(2)
                    isClickedSportRow = 2

                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                    text = "Drawing",
                    style = TextStyle(
                        color = colorResource(id = R.color.white),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize =
                        12.sp
                    ),
                    modifier = Modifier
                        .wrapContentHeight()
                        .width(0.dp)
                        .weight(2f)
                )
            }
        }
    }
