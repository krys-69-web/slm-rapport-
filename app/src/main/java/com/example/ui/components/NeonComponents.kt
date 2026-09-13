package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LocationItem
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.CardGlassBg
import com.example.ui.theme.DangerRed
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.InputPlaceholder
import com.example.ui.theme.InputText
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun NeonGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color(0x3300F5FF)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x1FFFFFFF),
                        Color(0x0AFFFFFF)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x33FFFFFF),
                        Color(0x1AFFFFFF)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
    ) {
        content()
    }
}

@Composable
fun NeonLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        style = TextStyle(
            color = NeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp,
            fontFamily = FontFamily.SansSerif
        ),
        modifier = modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun NeonInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    testTag: String = ""
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            NeonLabel(text = label)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isFocused) 6.dp else 2.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = if (isFocused) NeonCyan.copy(alpha = 0.35f) else Color.Transparent,
                    spotColor = if (isFocused) NeonCyan else Color.Transparent
                )
                .background(InputBg, shape = RoundedCornerShape(12.dp))
                .border(
                    width = if (isFocused) 2.dp else 1.5.dp,
                    color = if (isFocused) NeonCyan else InputBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 14.dp, vertical = if (singleLine) 13.dp else 11.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = InputPlaceholder,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                minLines = minLines,
                cursorBrush = SolidColor(NeonCyan),
                textStyle = TextStyle(
                    color = InputText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
                    .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
            )
        }
    }
}

@Composable
fun NeonBadge(
    text: String,
    modifier: Modifier = Modifier,
    isAccent: Boolean = true
) {
    val bgBrush = if (isAccent) {
        Brush.horizontalGradient(listOf(NeonCyan, NeonViolet))
    } else {
        Brush.horizontalGradient(listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF)))
    }
    val textColor = if (isAccent) Color.Black else TextWhite

    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = if (isAccent) NeonCyan else Color.Transparent)
            .background(
                brush = bgBrush,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
    }
}

enum class NeonButtonStyle {
    PRIMARY_NEON,
    NEON_GLASS,
    GHOST_GLASS,
    DANGER_GLASS
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NeonButtonStyle = NeonButtonStyle.PRIMARY_NEON,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    testTag: String = ""
) {
    val shape = RoundedCornerShape(14.dp)

    val backgroundModifier = if (!enabled) {
        Modifier
            .background(Color(0x33555555), shape = shape)
            .border(1.dp, Color(0x33888888), shape)
    } else when (style) {
        NeonButtonStyle.PRIMARY_NEON -> Modifier
            .shadow(8.dp, shape, spotColor = NeonCyan)
            .background(
                brush = Brush.horizontalGradient(listOf(NeonCyan, NeonViolet)),
                shape = shape
            )

        NeonButtonStyle.NEON_GLASS -> Modifier
            .shadow(6.dp, shape, spotColor = NeonCyan.copy(alpha = 0.4f))
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        NeonCyan.copy(alpha = 0.25f),
                        NeonViolet.copy(alpha = 0.25f)
                    )
                ),
                shape = shape
            )
            .border(1.dp, NeonCyan.copy(alpha = 0.6f), shape)

        NeonButtonStyle.GHOST_GLASS -> Modifier
            .background(Color(0x1AFFFFFF), shape = shape)
            .border(1.dp, Color(0x33FFFFFF), shape)

        NeonButtonStyle.DANGER_GLASS -> Modifier
            .background(DangerRed.copy(alpha = 0.15f), shape = shape)
            .border(1.dp, DangerRed.copy(alpha = 0.5f), shape)
    }

    val textColor = if (!enabled) {
        TextMuted
    } else when (style) {
        NeonButtonStyle.PRIMARY_NEON -> Color.Black
        NeonButtonStyle.NEON_GLASS -> NeonCyan
        NeonButtonStyle.GHOST_GLASS -> TextWhite
        NeonButtonStyle.DANGER_GLASS -> DangerRed
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .then(backgroundModifier)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = if (style == NeonButtonStyle.PRIMARY_NEON) FontWeight.Black else FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
fun LieuBlockCard(
    index: Int,
    item: LocationItem,
    onLocationChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onCauseChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val blockShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0x40000000), shape = blockShape)
            .border(1.dp, NeonViolet.copy(alpha = 0.35f), shape = blockShape)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonBadge(text = "LIEU ${index + 1} → CAUSE ${index + 1}")
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .background(DangerRed.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Supprimer ce lieu",
                        tint = DangerRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NeonInputField(
                value = item.locationName,
                onValueChange = onLocationChange,
                label = "LIEU ${index + 1} - On est allé à...",
                placeholder = "Ex: Chantier Port Autonome d'Abidjan",
                testTag = "input_lieu_${index + 1}"
            )

            Spacer(modifier = Modifier.height(10.dp))

            NeonInputField(
                value = item.daysDuration,
                onValueChange = onDurationChange,
                label = "Sur... jours",
                placeholder = "Ex: 4 jours",
                testTag = "input_duree_${index + 1}"
            )

            Spacer(modifier = Modifier.height(10.dp))

            NeonInputField(
                value = item.cause,
                onValueChange = onCauseChange,
                label = "CAUSE ${index + 1} - Pourquoi? Rattachée au LIEU ${index + 1}",
                placeholder = "Ex: Prise de cotes, pose de platines et soudure de finition",
                singleLine = false,
                minLines = 2,
                testTag = "input_cause_${index + 1}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Format : « ${item.formattedDisplay} »",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
