package com.foodenak.itpscanner.services

/**
 * Created by ITP on 10/5/2015.
 */
class ListResponseWrapper<T> : FEResponse {

    var status: String? = null;

    var errors: Map<String, List<String>>? = null;

    var results: Result<T>? = null;

    override fun responseStatus(): String? = status

    override fun responseErrors(): Map<String, List<String>>? = errors
}