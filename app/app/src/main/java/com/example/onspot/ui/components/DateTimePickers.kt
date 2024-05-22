package com.example.onspot.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardOptions
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
        value = if (defaultDate) pickedDate.toString() else "",
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
            title = "Pick a date",
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
        value = if (defaultTime) pickedTime.toString() else "",
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
            title = "Pick a time",
            timeRange = greaterThan..LocalTime.MAX
        ) {
            pickedTime = it
            onTimeSelected(it)
        }
    }
}