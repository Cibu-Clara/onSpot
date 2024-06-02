package com.example.onspot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun DatePicker(
    label: String,
    autocompleteDate: String = "",
    enabled: Boolean = true,
    onDateSelected: (LocalDate) -> Unit,
    greaterThan: LocalDate,
    modifier: Modifier
) {
    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateDialogState = rememberMaterialDialogState()
    var defaultDate by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        enabled = enabled,
        label = { Text(text = label) },
        keyboardOptions = KeyboardOptions.Default,
        value = if (defaultDate) pickedDate.toString() else autocompleteDate,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        maxLines = 1,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            dateDialogState.show()
                        }
                    }
                }
            },
        trailingIcon = {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Date"
            )
        }
    )

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                defaultDate = true
            }
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = greaterThan.plusDays(1),
            allowedDateValidator = {
                it > greaterThan
            }
        ) {
            pickedDate = it
            onDateSelected(it)
        }
    }
}

@Composable
fun TimePicker(
    label: String,
    autocompleteTime: String = "",
    enabled: Boolean = true,
    onTimeSelected: (LocalTime) -> Unit,
    greaterThan: LocalTime,
    modifier: Modifier
){
    var pickedTime by remember { mutableStateOf(LocalTime.NOON) }
    val timeDialogState = rememberMaterialDialogState()
    var defaultTime by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier,
        enabled = enabled,
        label = {Text(text = label)},
        keyboardOptions = KeyboardOptions.Default,
        value = if (defaultTime) pickedTime.toString() else autocompleteTime,
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        maxLines = 1,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            timeDialogState.show()
                        }
                    }
                }
            },
        trailingIcon = {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = "Time"
            )
        }
    )
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                defaultTime = true
            }
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.MIDNIGHT,
            timeRange = greaterThan..LocalTime.MAX
        ) {
            pickedTime = it
            onTimeSelected(it)
        }
    }
}