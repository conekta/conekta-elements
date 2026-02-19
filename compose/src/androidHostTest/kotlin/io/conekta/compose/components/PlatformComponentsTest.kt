package io.conekta.compose.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import io.conekta.compose.initComposeResourcesContext
import io.conekta.compose.theme.ConektaTheme
import io.conekta.elements.tokenizer.models.CardBrand
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlatformComponentsTest {
    @Before
    fun setUp() = initComposeResourcesContext()

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun conektaLogoImageRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    ConektaLogoImage()
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconVisaRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIcon(brand = CardBrand.VISA)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconMastercardRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIcon(brand = CardBrand.MASTERCARD)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconAmexRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIcon(brand = CardBrand.AMEX)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconUnknownRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIcon(brand = CardBrand.UNKNOWN)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconsRowShowsMultipleBrandsWhenUnknown() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIconsRow(detectedBrand = CardBrand.UNKNOWN)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconsRowShowsMultipleBrandsWhenNull() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIconsRow(detectedBrand = null)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconsRowShowsSingleBrandWhenVisaDetected() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIconsRow(detectedBrand = CardBrand.VISA)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconsRowShowsSingleBrandWhenMastercardDetected() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIconsRow(detectedBrand = CardBrand.MASTERCARD)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cardBrandIconsRowShowsSingleBrandWhenAmexDetected() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CardBrandIconsRow(detectedBrand = CardBrand.AMEX)
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun closeIconRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CloseIcon()
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkCircleIconRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CheckCircleIcon()
                }
            }
            onRoot().assertExists()
        }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun cvvIconRendersWithoutCrash() =
        runComposeUiTest {
            setContent {
                ConektaTheme {
                    CvvIcon()
                }
            }
            onRoot().assertExists()
        }
}
