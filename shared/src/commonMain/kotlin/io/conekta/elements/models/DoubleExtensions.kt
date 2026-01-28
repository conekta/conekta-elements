package io.conekta.elements.models

/**
 * Formats a Double with a fixed number of decimal places using native platform implementation.
 *
 * - **JavaScript**: uses `Number.toFixed()`
 * - **Android**: uses `String.format()`
 * - **iOS**: uses `NSString.stringWithFormat()`
 *
 * @param decimals Number of decimal places to display
 * @return Formatted string with the specified number of decimals
 */
expect fun Double.toFixed(decimals: Int): String
