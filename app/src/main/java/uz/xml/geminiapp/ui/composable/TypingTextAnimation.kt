package uz.xml.geminiapp.ui.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TypingTextAnimation(
    fullText: String,
    animationSpeedInMillis: Long = 5
) {
    var visibleText by remember { mutableStateOf("") }

    LaunchedEffect(fullText) {
        visibleText = ""
        fullText.forEachIndexed { index, _ ->
            delay(animationSpeedInMillis)
            visibleText = fullText.substring(0, index + 1)
        }
    }

    AnimatedContent(
        targetState = visibleText,
        transitionSpec = {
            if (targetState.length > initialState.length) {
                fadeIn(animationSpec = tween(durationMillis = animationSpeedInMillis.toInt())) togetherWith fadeOut(
                    animationSpec = tween(durationMillis = 0)
                )
            } else {
                fadeIn(animationSpec = tween(durationMillis = 0)) togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = animationSpeedInMillis.toInt()
                    )
                )
            }
        },
        label = "typingAnimation"
    ) { targetText ->
        Text(
//            fontWeight = FontWeight.Thin,
            text = targetText,
            fontSize = 20.sp,
            style = TextStyle(fontSize = 20.sp)
        )
    }
}
