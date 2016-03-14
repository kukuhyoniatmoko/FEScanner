package com.foodenak.itpscanner.services

/**
 * Created by ITP on 10/5/2015.
 */
public class ResponseWrapper<T> : FEResponse {

    var status: String? = null;

    var errors: Map<String, List<String>>? = null;

    var result: T? = null;

    override fun responseStatus(): String? {
        return status
    }

    override fun responseErrors(): Map<String, List<String>>? {
        return errors
    }
}