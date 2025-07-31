package dev.kigya.headway.core.designSystem.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import dev.kigya.headway.core.designSystem.theme.FreudDsToken

@ConsistentCopyVisibility
@Immutable
data class FreudColor internal constructor(
    // — Mindful Brown
    val brown100: FreudDsToken<Color> = FreudDsToken(Color(0xFF1F160F)),
    val brown90: FreudDsToken<Color> = FreudDsToken(Color(0xFF332419)),
    val brown80: FreudDsToken<Color> = FreudDsToken(Color(0xFF4B3425)),
    val brown70: FreudDsToken<Color> = FreudDsToken(Color(0xFF6D4B36)),
    val brown60: FreudDsToken<Color> = FreudDsToken(Color(0xFF926247)),
    val brown50: FreudDsToken<Color> = FreudDsToken(Color(0xFFAC836C)),
    val brown40: FreudDsToken<Color> = FreudDsToken(Color(0xFFBDA193)),
    val brown30: FreudDsToken<Color> = FreudDsToken(Color(0xFFD5C2B9)),
    val brown20: FreudDsToken<Color> = FreudDsToken(Color(0xFFE8DDD9)),
    val brown10: FreudDsToken<Color> = FreudDsToken(Color(0xFFF7F4F2)),

    // — Optimistic Gray
    val gray100: FreudDsToken<Color> = FreudDsToken(Color(0xFF121619)),
    val gray90: FreudDsToken<Color> = FreudDsToken(Color(0xFF21262A)),
    val gray80: FreudDsToken<Color> = FreudDsToken(Color(0xFF343A3F)),
    val gray70: FreudDsToken<Color> = FreudDsToken(Color(0xFF4D5358)),
    val gray60: FreudDsToken<Color> = FreudDsToken(Color(0xFF697077)),
    val gray50: FreudDsToken<Color> = FreudDsToken(Color(0xFF878E96)),
    val gray40: FreudDsToken<Color> = FreudDsToken(Color(0xFFA2A9B0)),
    val gray30: FreudDsToken<Color> = FreudDsToken(Color(0xFFC1C6CD)),
    val gray20: FreudDsToken<Color> = FreudDsToken(Color(0xFFDDE1E6)),
    val gray10: FreudDsToken<Color> = FreudDsToken(Color(0xFFF2F5F8)),

    // — Serenity Green
    val green100: FreudDsToken<Color> = FreudDsToken(Color(0xFF191E10)),
    val green90: FreudDsToken<Color> = FreudDsToken(Color(0xFF29321A)),
    val green80: FreudDsToken<Color> = FreudDsToken(Color(0xFF3D4A26)),
    val green70: FreudDsToken<Color> = FreudDsToken(Color(0xFF5A6B38)),
    val green60: FreudDsToken<Color> = FreudDsToken(Color(0xFF7D944D)),
    val green50: FreudDsToken<Color> = FreudDsToken(Color(0xFF9BB068)),
    val green40: FreudDsToken<Color> = FreudDsToken(Color(0xFFB4C48D)),
    val green30: FreudDsToken<Color> = FreudDsToken(Color(0xFFCFD9B5)),
    val green20: FreudDsToken<Color> = FreudDsToken(Color(0xFFE5EAD7)),
    val green10: FreudDsToken<Color> = FreudDsToken(Color(0xFFF2F5EB)),

    // — Empathy Orange
    val orange100: FreudDsToken<Color> = FreudDsToken(Color(0xFF2E1200)),
    val orange90: FreudDsToken<Color> = FreudDsToken(Color(0xFF4C1D00)),
    val orange80: FreudDsToken<Color> = FreudDsToken(Color(0xFF702901)),
    val orange70: FreudDsToken<Color> = FreudDsToken(Color(0xFFA23901)),
    val orange60: FreudDsToken<Color> = FreudDsToken(Color(0xFFDF4B01)),
    val orange50: FreudDsToken<Color> = FreudDsToken(Color(0xFFFE631B)),
    val orange40: FreudDsToken<Color> = FreudDsToken(Color(0xFFFE814B)),
    val orange30: FreudDsToken<Color> = FreudDsToken(Color(0xFFFEAF8F)),
    val orange20: FreudDsToken<Color> = FreudDsToken(Color(0xFFFFD2C2)),
    val orange10: FreudDsToken<Color> = FreudDsToken(Color(0xFFFFF0EB)),

    // — Zen Yellow
    val yellow100: FreudDsToken<Color> = FreudDsToken(Color(0xFF2E2500)),
    val yellow90: FreudDsToken<Color> = FreudDsToken(Color(0xFF4D3C00)),
    val yellow80: FreudDsToken<Color> = FreudDsToken(Color(0xFF705600)),
    val yellow70: FreudDsToken<Color> = FreudDsToken(Color(0xFFA37A00)),
    val yellow60: FreudDsToken<Color> = FreudDsToken(Color(0xFFE0A500)),
    val yellow50: FreudDsToken<Color> = FreudDsToken(Color(0xFFFFCE5C)),
    val yellow40: FreudDsToken<Color> = FreudDsToken(Color(0xFFFFD85C)),
    val yellow30: FreudDsToken<Color> = FreudDsToken(Color(0xFFFFDB8F)),
    val yellow20: FreudDsToken<Color> = FreudDsToken(Color(0xFFFFEBC2)),
    val yellow10: FreudDsToken<Color> = FreudDsToken(Color(0xFFFFF4E0)),

    // — Gentle Purple
    val purple100: FreudDsToken<Color> = FreudDsToken(Color(0xFF0D002E)),
    val purple90: FreudDsToken<Color> = FreudDsToken(Color(0xFF14004D)),
    val purple80: FreudDsToken<Color> = FreudDsToken(Color(0xFF1C0070)),
    val purple70: FreudDsToken<Color> = FreudDsToken(Color(0xFF2F1093)),
    val purple60: FreudDsToken<Color> = FreudDsToken(Color(0xFF3D16CA)),
    val purple50: FreudDsToken<Color> = FreudDsToken(Color(0xFF5530E8)),
    val purple40: FreudDsToken<Color> = FreudDsToken(Color(0xFF7152FF)),
    val purple30: FreudDsToken<Color> = FreudDsToken(Color(0xFFA18FFF)),
    val purple20: FreudDsToken<Color> = FreudDsToken(Color(0xFFCBC2FF)),
    val purple10: FreudDsToken<Color> = FreudDsToken(Color(0xFFEDEBFF))
)
