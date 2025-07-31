package dev.kigya.headway.core.designSystem.theme.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.urbanist_bold
import headway.core.design_system.generated.resources.urbanist_extrabold
import headway.core.design_system.generated.resources.urbanist_semibold
import org.jetbrains.compose.resources.Font

@Immutable
data object FreudFont {

    val extraBold: FreudDsToken<FontFamily>
        @Composable get() = FreudDsToken(
            FontFamily(
                Font(
                    resource = Res.font.urbanist_extrabold,
                    weight   = FontWeight.ExtraBold,
                    style    = FontStyle.Normal
                )
            )
        )

    val bold: FreudDsToken<FontFamily>
        @Composable get() = FreudDsToken(
            FontFamily(
                Font(
                    resource = Res.font.urbanist_bold,
                    weight   = FontWeight.Bold,
                    style    = FontStyle.Normal
                )
            )
        )

    val semiBold: FreudDsToken<FontFamily>
        @Composable get() = FreudDsToken(
            FontFamily(
                Font(
                    resource = Res.font.urbanist_semibold,
                    weight   = FontWeight.SemiBold,
                    style    = FontStyle.Normal
                )
            )
        )
}
