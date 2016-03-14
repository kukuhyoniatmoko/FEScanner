package com.foodenak.itpscanner.services

/**
 * Created by ITP on 10/5/2015.
 */
class ListRedeemWrapper<T> : FEResponse {

    var status: String? = null;

    var errors: Map<String, List<String>>? = null;

    var results: Result<T>? = null;

    var voucherRemaining = 0

    override fun responseStatus(): String? {
        return status
    }

    override fun responseErrors(): Map<String, List<String>>? {
        return errors
    }
}