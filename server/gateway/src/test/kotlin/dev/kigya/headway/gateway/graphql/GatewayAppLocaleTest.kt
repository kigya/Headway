package dev.kigya.headway.gateway.graphql

import kotlin.test.Test
import kotlin.test.assertEquals

class GatewayAppLocaleTest {

    @Test
    fun `accept language picks highest q value`() {
        assertEquals(
            GatewayAppLocale.EN,
            parseGatewayAppLocale(
                xHeadwayLocale = null,
                acceptLanguage = "ru;q=0.2, en;q=0.9",
            ),
        )
    }

    @Test
    fun `accept language uses document order when q ties`() {
        assertEquals(
            GatewayAppLocale.EN,
            parseGatewayAppLocale(
                xHeadwayLocale = null,
                acceptLanguage = "en, ru",
            ),
        )
        assertEquals(
            GatewayAppLocale.RU,
            parseGatewayAppLocale(
                xHeadwayLocale = null,
                acceptLanguage = "ru, en",
            ),
        )
    }

    @Test
    fun `x headway locale wins over accept language`() {
        assertEquals(
            GatewayAppLocale.RU,
            parseGatewayAppLocale(
                xHeadwayLocale = "ru",
                acceptLanguage = "en;q=1.0",
            ),
        )
    }
}
