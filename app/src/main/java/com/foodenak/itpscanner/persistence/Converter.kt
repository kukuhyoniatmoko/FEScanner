package com.foodenak.itpscanner.persistence

/**
 * Created by ITP on 10/8/2015.
 */
interface Converter<T, R> {

    fun convert(source: T): R
}