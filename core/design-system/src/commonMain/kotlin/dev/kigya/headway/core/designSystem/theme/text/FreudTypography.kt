package dev.kigya.headway.core.designSystem.theme.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.sp
import dev.kigya.headway.core.designSystem.theme.FreudDsToken

@Immutable
object FreudTypography {

    @get:Composable
    val displayLgExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp180,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val displayLgBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp180,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val displayMdExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp128,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val displayMdBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp128,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val displaySmExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp96,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val displaySmBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp96,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val heading2xlExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp72,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val heading2xlBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp72,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val heading2xlSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp72,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val headingXlExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp60,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val headingXlBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp60,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val headingXlSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp60,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val headingLgExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp48,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val headingLgBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp48,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val headingLgSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp48,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val headingMdExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp36,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val headingMdBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp36,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val headingMdSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp36,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val headingSmExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp30,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val headingSmBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp30,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val headingSmSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp30,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val headingXsExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp24,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val headingXsBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp24,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val headingXsSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp24,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val text2xlExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp24,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val text2xlBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp24,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val text2xlSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp24,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val textXlExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp20,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val textXlBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp20,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val textXlSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp20,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val textLgExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp18,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val textLgBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp18,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val textLgSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp18,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val textMdExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp16,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val textMdBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp16,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val textMdSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp16,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val textSmExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp14,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val textSmBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp14,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val textSmSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp14,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val textXsExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp12,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val textXsBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp12,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val textXsSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp12,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val text2xsExtraBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp10,
                fontWeight = FreudFontWeight.extraBold,
                fontFamily = FreudFont.extraBold,
            )

    @get:Composable
    val text2xsBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp10,
                fontWeight = FreudFontWeight.bold,
                fontFamily = FreudFont.bold,
            )

    @get:Composable
    val text2xsSemiBold
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp10,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val paragraph2xl
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp24,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
                lineHeight = FreudDsToken(160.sp),
            )

    @get:Composable
    val paragraphXl
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp20,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
                lineHeight = FreudDsToken(160.sp),
            )

    @get:Composable
    val paragraphLg
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp18,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
                lineHeight = FreudDsToken(160.sp),
            )

    @get:Composable
    val paragraphMd
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp16,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
                lineHeight = FreudDsToken(160.sp),
            )

    @get:Composable
    val paragraphSm
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp14,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
                lineHeight = FreudDsToken(160.sp),
            )

    @get:Composable
    val paragraphXs
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp12,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
                lineHeight = FreudDsToken(160.sp),
            )

    @get:Composable
    val label2xl
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp20,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val labelXl
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp18,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val labelLg
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp16,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val labelMd
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp14,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val labelSm
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp12,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )

    @get:Composable
    val labelXs
        get() = this provides
            FreudTextStyle(
                fontSize = FreudTextSize.sp10,
                fontWeight = FreudFontWeight.semiBold,
                fontFamily = FreudFont.semiBold,
            )
}
