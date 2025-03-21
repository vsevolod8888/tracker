package com.seva.tracker.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seva.tracker.R
import com.seva.tracker.TextStyleLocal

@Composable
fun NoInternetPicture(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Image(
            painter = painterResource(R.drawable.no_internet),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .wrapContentHeight()
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.nointernetconnection),
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 20.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = TextStyleLocal.semibold24,
        )
        Spacer(modifier = Modifier.height(40.dp))
    }
}