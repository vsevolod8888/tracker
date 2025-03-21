package com.seva.tracker.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.seva.tracker.R
import kotlinx.datetime.LocalDate

@Composable
fun formatEpochDays(epochDays: Int): String {
    val date = LocalDate.fromEpochDays(epochDays)
    val monthNames = listOf(
        stringResource(R.string.january),
        stringResource(R.string.february),
        stringResource(R.string.march),
        stringResource(R.string.april),
        stringResource(R.string.may),
        stringResource(R.string.june),
        stringResource(R.string.july),
        stringResource(R.string.august),
        stringResource(R.string.september),
        stringResource(R.string.october),
        stringResource(R.string.november),
        stringResource(R.string.december)
    )

    val day = date.dayOfMonth
    val month = monthNames[date.monthNumber - 1]
    val year = date.year

    return "$day $month, $year"
}

fun shortenString(input: String): String {
    return if (input.length > 7) input.take(7) + "…" else input
}
fun makeToastNoInternet(context: Context) {
    Toast.makeText(context, R.string.nointernetconnection, Toast.LENGTH_LONG).show()

}

@Composable
fun getBitmapDescriptor(@DrawableRes vectorResId: Int): BitmapDescriptor? {
    val context = LocalContext.current
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}