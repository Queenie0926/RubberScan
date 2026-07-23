package com.example.rubberscan

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

// ── Press feedback ───────────────────────────────────────────
// Scales a card down slightly while pressed, springing back on
// release. Purely a graphicsLayer transform — no layout cost.
@Composable
fun Modifier.pressScale(
    pressedScale: Float = 0.97f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pressScale"
    )
    return this
        .scale(scale)
        .indication(interactionSource, null)
}
