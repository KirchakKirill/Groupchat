package com.example.group_chat.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun CustomOutlinedTextField(changed: (String) -> Unit, value:String, image: ImageVector, strResource:String)
{
    OutlinedTextField(
        modifier = Modifier.padding(top=10.dp)
            .width(300.dp),
        value = value,
        onValueChange = {changed(it)},
        leadingIcon ={
            Icon(painter = rememberVectorPainter(image = image),
                contentDescription = ""
            )
        },
        placeholder = {
            Text(text= strResource)
        },
        textStyle = LocalTextStyle.current.copy(color = Color.White),

        )
}