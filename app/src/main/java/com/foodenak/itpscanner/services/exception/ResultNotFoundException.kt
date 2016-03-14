package com.foodenak.itpscanner.services.exception

/**
 * Created by ITP on 10/5/2015.
 */
class ResultNotFoundException(mErrors: Map<String, List<String>>?, mStatus: String?) : ResponseException(mErrors, mStatus) {
}