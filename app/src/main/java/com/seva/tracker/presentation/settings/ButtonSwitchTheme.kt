package com.seva.tracker.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.seva.tracker.TextStyleLocal
import com.seva.tracker.presentation.MyViewModel
import kotlinx.coroutines.launch


@Composable
fun ButtonSwitchTheme(
    text: String,
    viewModel: MyViewModel,
    modifier: Modifier
) {
    var switcThemeState = viewModel.isThemeDark.collectAsState()
    var scope = rememberCoroutineScope()
    Row(
        modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp)
            .fillMaxSize()
            .clip(RoundedCornerShape(50.dp))
            .border(
                1.dp, Color.White, RoundedCornerShape(50.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = text,
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp)
                .weight(6f),
            color = Color.White,
            style = TextStyleLocal.regular16,
        )
        switcThemeState.value.let { it1 ->
            Switch(
                checked = switcThemeState.value,
                onCheckedChange = { newValue ->
                    scope.launch {
                        viewModel.updateTheme(newValue)
                    }

                },
                modifier = Modifier
                    .height(35.dp)
                    .wrapContentWidth()
                    .weight(1f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Blue,
                    uncheckedThumbColor = Color.Red,
                    checkedTrackColor = Color.Cyan,
                    checkedBorderColor = Color.Magenta,
                    uncheckedBorderColor = Color.LightGray,
                    uncheckedTrackColor = Color.Blue
                ),
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
}

