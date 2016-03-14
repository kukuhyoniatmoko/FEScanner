package com.foodenak.itpscanner.services.exception

import android.text.TextUtils
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.services.*
import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */

fun create(errors: Map<String, List<String>>?, status: String?): ResponseException {
    if (RESULT_NOT_FOUND.equals(status)) {
        return ResultNotFoundException(errors, status);
    } else {
        return ResponseException(errors, status);
    }
}

fun createExceptionMessage(errors: Map<String, List<String>>?, status: String?): String {
    val builder = StringBuilder();
    builder.append(createStatusMessage(status));
    val lineSeparator = System.getProperty("line.separator");
    builder.append(lineSeparator);
    builder.append(createErrorMessage(errors));
    return builder.toString();
}

inline fun <reified T : FEResponse> Observable<T>.validateResponse(): Observable<T> {
    return this.compose(RESPONSE_VALIDATOR as (Observable.Transformer<T, T>))
}

val RESPONSE_VALIDATOR = Observable.Transformer<FEResponse, FEResponse> { observable ->
    observable.map { wrapper ->
        checkResponse(wrapper.responseErrors(), wrapper.responseStatus())
        wrapper
    }
}

fun Observable<ResponseWrapper<User>>.validateAdmin(): Observable<ResponseWrapper<User>> {
    return this.compose(ADMIN_VALIDATOR)
}

private val ADMIN_VALIDATOR = Observable.Transformer<ResponseWrapper<User>, ResponseWrapper<User>> { observable ->
    observable.doOnNext { wrapper ->
        val user = wrapper.result;
        if (user != null) {
            if (user.userPrivilegeId == 3 || user.userPrivilegeId == 4) {
                return@doOnNext
            }
        }
        throw (InvalidAdminCredentialException());
    }
}

fun checkResponse(errors: Map<String, List<String>>?, status: String?) {
    if (OK.equals(status)) {
        return
    }
    throw create(errors, status)
}

fun createStatusMessage(status: String?): String {
    val builder = StringBuilder();
    if (TextUtils.isEmpty(status)) {
        builder.append(NO_STATUS_RECEIVED);
    } else {
        builder.append(status);
    }
    return builder.toString();
}

fun createErrorMessage(wrapper: Map<String, List<String>>?): String {
    val builder = StringBuilder();
    if (wrapper != null && wrapper.size > 0) {
        val lineSeparator = System.getProperty("line.separator");
        var newLine = "";
        for ((key, messages) in wrapper) {
            builder.append(newLine);
            builder.append(TextUtils.join(lineSeparator, messages));
            newLine = lineSeparator;
        }
        builder.append(lineSeparator);
    }
    return builder.toString();
}
