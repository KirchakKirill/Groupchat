package com.example.group_chat.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
 fun CustomImage(buttonAlpha: Float, painter: Painter?, imageVector: ImageVector?) {
    val sourceImage = painter.takeIf { it != null } ?: imageVector
    when(sourceImage){
        is Painter -> {
            Image(
                painter = sourceImage,
                contentDescription = "",
                modifier = Modifier.alpha(buttonAlpha)
            )
        }
        is ImageVector ->{

            Image(
                imageVector = sourceImage,
                contentDescription = "",
                modifier = Modifier.alpha(buttonAlpha)
            )
        }
    }

}