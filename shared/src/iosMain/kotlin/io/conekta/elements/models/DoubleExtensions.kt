package io.conekta.elements.models

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

actual fun Double.toFixed(decimals: Int): String = NSString.stringWithFormat("%.${decimals}f", this)
