package com.example.onspot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.onspot.data.model.ParkingSpot
import com.example.onspot.data.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuParkingSpots(
    label: String,
    options: List<ParkingSpot>,
    onTextSelected: (ParkingSpot) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedText by rememberSaveable { mutableStateOf("No parking spot selected") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default,
                label = { Text(text = label) },
                singleLine = true,
                maxLines = 1,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                if (options.isNotEmpty()) {
                    options.forEach { parkingSpot ->
                        DropdownMenuItem(
                            onClick = {
                                selectedText = parkingSpot.address
                                onTextSelected(parkingSpot)
                                expanded = false
                            },
                            text = { Text(text = parkingSpot.address) }
                        )
                    }
                } else {
                    DropdownMenuItem(
                        text = { Text("You have not registered any parking spots yet.") },
                        onClick = { },
                        enabled = false
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenuVehicles(
    label: String,
    options: List<Vehicle>,
    onTextSelected: (Vehicle) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedText by rememberSaveable { mutableStateOf("No vehicle selected") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default,
                label = { Text(text = label) },
                singleLine = true,
                maxLines = 1,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                if (options.isNotEmpty()) {
                    options.forEach { vehicle ->
                        DropdownMenuItem(
                            onClick = {
                                selectedText = vehicle.licensePlate
                                onTextSelected(vehicle)
                                expanded = false
                            },
                            text = { Text(text = vehicle.licensePlate) }
                        )
                    }
                } else {
                    DropdownMenuItem(
                        text = { Text("You have not registered any vehicles yet.") },
                        onClick = { },
                        enabled = false
                    )
                }
            }
        }
    }
}