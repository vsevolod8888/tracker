package com.seva.tracker.presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
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
                    label = { Text(stringResource(R.string.enterroutename)) }
                )
            }
        },
        confirmButton = {
            Column {
                Button(
                    onClick = onNewRoute,
                    enabled = routeName.isNotBlank()
                ) {
                    Text(stringResource(R.string.newroute))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onDrawRoute,
                    enabled = routeName.isNotBlank()
                ) {
                    Text(stringResource(R.string.drawroute))
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}