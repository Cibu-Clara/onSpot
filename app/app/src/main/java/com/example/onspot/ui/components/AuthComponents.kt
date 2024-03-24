package com.example.onspot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onspot.ui.theme.RegularFont
import com.example.onspot.ui.theme.lightPurple
import com.example.onspot.ui.theme.purple

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = lightPurple,
            focusedBorderColor = Color.Gray,
            cursorColor = Color.Gray
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        maxLines = 1,
        label = { Text(text = label, color = Color.Gray) }
    )
}

@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = lightPurple,
            focusedBorderColor = Color.Gray,
            cursorColor = Color.Gray
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        maxLines = 1,
        trailingIcon = {
            val iconImage = if (visible) {
                Icons.Default.Visibility
            } else {
                Icons.Default.VisibilityOff
            }

            val description = if (visible) {
                "Hide Password"
            } else {
                "Show Password"
            }

            IconButton(
                onClick = {
                    visible = !visible
                }
            ) {
                Icon(
                    imageVector = iconImage,
                    contentDescription = description
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        label = { Text(text = label, color = Color.Gray) }
    )
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    buttonText: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    buttonColor: Color = purple,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(15.dp)
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 30.dp, end = 30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = contentColor
        ),
        enabled = enabled,
        shape = shape
    ) {
        Text(
            text = buttonText,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            modifier = Modifier.padding(7.dp)
        )
    }
}

@Composable
fun CustomClickableText(
    text1: String,
    text2: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Gray,
    fontWeight: FontWeight = FontWeight.Medium
) {
    Text(
        text = text1,
        fontFamily = RegularFont,
        fontWeight = fontWeight,
        color = textColor,
    )
    Text(
        text = text2,
        fontWeight = FontWeight.Bold,
        fontFamily = RegularFont,
        color = purple,
        modifier = modifier.clickable { onClick() }
    )
}

@Composable
fun DividerWithText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(color = Color.Gray, modifier = Modifier.weight(1f))
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Divider(color = Color.Gray, modifier = Modifier.weight(1f))
    }
}

@Composable
fun CustomInputDialog(
    title: String,
    text: String,
    label: String,
    confirmButtonText: String = "Confirm",
    dismissButtonText: String = "Cancel",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    var inputText by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        },
        text = {
            Column {
                Text(
                    text = text,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.padding(top = 20.dp),
                    label = { Text(label) },
                    singleLine = true,
                    visualTransformation = visualTransformation,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(inputText)
                }
            ) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(dismissButtonText)
            }
        }
    )
}