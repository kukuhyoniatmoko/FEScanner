package com.foodenak.itpscanner.services.exception

import java.util.*

/**
 * Created by ITP on 10/5/2015.
 */
open class ResponseException(private val mErrors: Map<String,
        List<String>>?, private val mStatus: String?
) : RuntimeException(createExceptionMessage(mErrors, mStatus)) {

    fun getFormattedErrors(): String {
        return createErrorMessage(getErrors());
    }

    fun getErrors(): Map<String, List<String>> {
        if (mErrors != null) {
            return mErrors;
        } else {
            return Collections.emptyMap ();
        }
    }

    fun getStatus(): String? {
        return mStatus;
    }
}