package com.example.group_chat.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(onClick: ()->Unit,
                 painter: Painter? = null,
                 imageVector: ImageVector? = null,
                 text:String?=null,
                 @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
                 enabled:Boolean = true,
                 shape: Shape = CircleShape
)
{

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150)
    )
    val buttonAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    Button(onClick = {onClick()},
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White.copy(buttonAlpha),
            containerColor = MaterialTheme.colorScheme.secondary.copy(buttonAlpha)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        interactionSource = interactionSource,
        modifier = modifier.
        scale(scale),
        enabled = enabled

    )
    {
        if (painter !=null) CustomImage(buttonAlpha,painter,null)
        else if (imageVector!= null) CustomImage(buttonAlpha,null,imageVector)
        if (!text.isNullOrEmpty()) Text(text=text,color = Color.White, fontSize = 15.sp,
            modifier =  if (painter!=null || imageVector!=null)  Modifier.padding(start = 10.dp) else Modifier
        )
    }
}


@Composable
fun CustomButtonRow(
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    button1:@Composable () ->Unit,
    button2: @Composable ()->Unit
)
{
    Row(modifier = modifier
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ){
        button1()
        button2()
    }
}