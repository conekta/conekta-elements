package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.utils.formatEpochSecondsInMexicoCity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaySummaryExpirationRowTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun paySummaryExpirationRowDisplaysFormattedDateAndTime() =
        runComposeUiTest {
            val epochSeconds = 1735689540L
            val (dateText, timeText) = formatEpochSecondsInMexicoCity(epochSeconds)
            val wrappedTimeText = timeText.replace(" ", "\u00A0")

            setContent {
                ConektaTheme {
                    PaySummaryExpirationRow(epochSeconds = epochSeconds)
                }
            }

            onNodeWithText(dateText, substring = true).assertIsDisplayed()
            onNodeWithText(wrappedTimeText, substring = true).assertIsDisplayed()
        }
}
