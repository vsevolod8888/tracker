package com.seva.tracker.presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.seva.tracker.R

@Composable
fun RouteConfirmationDialog(
    title: String,
    message: String,
    routeName: String,
    onRouteNameChange: (String) -> Unit,
    onNewRoute: () -> Unit,
    onDrawRoute: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(message)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = routeName,
                    onValueChange = onRouteNameChange,
                    label = { Text(stringResource(R.string.enterroutename)) },
                    colors = TextFieldColors(
                        cursorColor = MaterialTheme.colorScheme.onSurface,
                        errorCursorColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        errorTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        errorContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface,
                        errorIndicatorColor = MaterialTheme.colorScheme.onSurface,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        errorLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        errorTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                        errorLabelColor = MaterialTheme.colorScheme.onSurface,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.surface,
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.surface,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.surface,
                        errorPlaceholderColor = MaterialTheme.colorScheme.surface,
                        focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        errorSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedPrefixColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedPrefixColor = MaterialTheme.colorScheme.onSurface,
                        disabledPrefixColor = MaterialTheme.colorScheme.onSurface,
                        errorPrefixColor = MaterialTheme.colorScheme.onSurface,
                        focusedSuffixColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedSuffixColor = MaterialTheme.colorScheme.onSurface,
                        disabledSuffixColor = MaterialTheme.colorScheme.onSurface,
                        errorSuffixColor = MaterialTheme.colorScheme.onSurface,
                        textSelectionColors = TextSelectionColors(
                            handleColor= MaterialTheme.colorScheme.onSurface,
                            backgroundColor= MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                )
            }
        },
        confirmButton = {
            Column {
                UniversalButtonForDialog(
                    text = stringResource(R.string.newroute),
                    onClick = onNewRoute,
                    containerColor = MaterialTheme.colorScheme.surface,
                    enabled = routeName.isNotBlank()
                )

                Spacer(modifier = Modifier.height(4.dp))
                UniversalButtonForDialog(
                    text = stringResource(R.string.drawroute),
                    onClick = onDrawRoute,
                    containerColor = MaterialTheme.colorScheme.surface,
                    enabled = routeName.isNotBlank()
                )
            }
        },
        dismissButton = {
            UniversalButtonForDialog(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.surface,
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
    )
}