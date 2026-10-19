package com.aurel.notes.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aurel.notes.core.designsystem.AurelTheme
import com.aurel.notes.core.designsystem.components.FloatingParticlesBackground
import kotlinx.coroutines.launch

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val highlightFeature: String,
    val icon: ImageVector
)

private val steps = listOf(
    OnboardingStep(
        title = "Organize Your Thoughts",
        subtitle = "Experience fluid note-taking with markdown, custom notebooks, smart tags, and structured daily journals.",
        highlightFeature = "Notebooks • Markdown • Task Checklists",
        icon = Icons.Filled.Description
    ),
    OnboardingStep(
        title = "Voice & Visual Canvas",
        subtitle = "Capture audio memos with instant playback or sketch freeform diagrams with real-time vector strokes.",
        highlightFeature = "Voice Memos • Canvas Sketches • Smart Prompts",
        icon = Icons.Filled.Brush
    ),
    OnboardingStep(
        title = "Private & Truly Offline",
        subtitle = "Your thoughts never leave your device. Shield sensitive notes behind hardware biometric security & FLAG_SECURE.",
        highlightFeature = "Biometric Lock • Privacy Shield • 100% Offline",
        icon = Icons.Filled.Fingerprint
    )
)

@Composable
fun OnboardingScreen(
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val scaleAnim = remember { Animatable(1f) }

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("notepad.json"))
    var isPlaying by remember { mutableStateOf(true) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever,
        speed = 1.0f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AurelTheme.colors.background)
    ) {
        // 1. Ambient Floating Particles Background
        FloatingParticlesBackground(
            modifier = Modifier.fillMaxSize(),
            particleCount = 38,
            primaryColor = AurelTheme.colors.primaryAccent,
            secondaryColor = AurelTheme.colors.primaryAccent.copy(alpha = 0.4f)
        )

        // 2. Top Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AurelTheme.colors.primaryAccent)
                )
                Text(
                    text = "AUREL NOTES",
                    style = AurelTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = AurelTheme.colors.primaryAccent
                    )
                )
            }

            if (currentStepIndex < steps.lastIndex) {
                TextButton(
                    onClick = onCompleteOnboarding,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Skip",
                        style = AurelTheme.typography.labelMedium.copy(
                            color = AurelTheme.colors.secondaryMuted
                        )
                    )
                }
            }
        }

        // 3. Main Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 70.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Interactive Lottie Animation (Tap to trigger a fun spring bounce!)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .scale(scaleAnim.value)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AurelTheme.colors.primaryAccent.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.launch {
                                isPlaying = !isPlaying
                                scaleAnim.animateTo(
                                    targetValue = 0.92f,
                                    animationSpec = tween(120, easing = FastOutSlowInEasing)
                                )
                                scaleAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                isPlaying = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(240.dp)
                    )
                }
            }

            // Interactive Text Content with horizontal animation
            AnimatedContent(
                targetState = steps[currentStepIndex],
                transitionSpec = {
                    (slideInHorizontally(
                        animationSpec = tween(350, easing = FastOutSlowInEasing),
                        initialOffsetX = { it / 2 }
                    ) + fadeIn(animationSpec = tween(350))).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(250),
                            targetOffsetX = { -it / 2 }
                        ) + fadeOut(animationSpec = tween(200))
                    )
                },
                label = "onboardingTextTransition"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(AurelTheme.colors.surface.copy(alpha = 0.85f))
                        .border(1.dp, AurelTheme.colors.divider, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Feature badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AurelTheme.colors.primaryAccent.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = AurelTheme.colors.primaryAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = step.highlightFeature,
                                style = AurelTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AurelTheme.colors.primaryAccent
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = step.title,
                        style = AurelTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AurelTheme.colors.primaryInk
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = step.subtitle,
                        style = AurelTheme.typography.bodyMedium.copy(
                            color = AurelTheme.colors.secondaryMuted,
                            lineHeight = 22.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Navigation & Interactive Pager Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.forEachIndexed { index, _ ->
                        val isSelected = index == currentStepIndex
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isSelected) 28.dp else 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isSelected) AurelTheme.colors.primaryAccent
                                    else AurelTheme.colors.divider
                                )
                                .clickable {
                                    currentStepIndex = index
                                }
                        )
                    }
                }

                // Next or Get Started Button
                Button(
                    onClick = {
                        if (currentStepIndex < steps.lastIndex) {
                            currentStepIndex++
                        } else {
                            onCompleteOnboarding()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AurelTheme.colors.primaryAccent,
                        contentColor = Color.White
                    ),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentStepIndex == steps.lastIndex) "Get Started" else "Next",
                            style = AurelTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = if (currentStepIndex == steps.lastIndex) Icons.Filled.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
