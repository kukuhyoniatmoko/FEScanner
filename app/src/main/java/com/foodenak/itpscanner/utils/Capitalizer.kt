package com.foodenak.itpscanner.utils

/**
 * Created by ITP on 1/15/2015.
 */

fun String.capitalizeEachWord(): String {
    if (this.isBlank()) return this

    val arr = this.toCharArray()
    var capitalizeNext = true
    val phrase = StringBuilder()
    for (c in arr) {
        if (capitalizeNext && Character.isLetter(c)) {
            phrase.append(Character.toUpperCase(c))
            capitalizeNext = false
            continue
        } else if (Character.isWhitespace(c)) {
            capitalizeNext = true
        }
        phrase.append(c)
    }
    return phrase.toString()
}
