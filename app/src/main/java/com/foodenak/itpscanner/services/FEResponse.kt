package com.foodenak.itpscanner.services

/**
 * Created by ITP on 10/6/2015.
 */
interface FEResponse {

    fun responseStatus(): String?

    fun responseErrors(): Map<String, List<String>>?;
}