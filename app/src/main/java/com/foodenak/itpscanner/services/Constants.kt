package com.foodenak.itpscanner.services

import okhttp3.RequestBody

/**
 * Created by ITP on 10/5/2015.
 */
fun emptyBody(): RequestBody = EmptyBody();

const val OK = "OK"

const val VALIDATION_ERROR = "VALIDATION_ERROR"

const val RESULT_NOT_FOUND = "RESULT_NOT_FOUND" // When querying db table without any result

const val INVALID_USER = "INVALID_USER"

const val INVALID_TYPE = "INVALID_TYPE"

const val MISSING_FILE = "MISSING_FILE"

const val TRANSACTION_FAILED = "TRANSACTION_FAILED"

const val PRIVATE_PROFILE = "PRIVATE_PROFILE"

const val INSUFFICIENT_GEMS = "INSUFFICIENT_GEMS"

const val INVALID_TOKEN = "INVALID_TOKEN" // When token is invalid (API call only)

const val INVALID_CREDENTIAL = "INVALID_CREDENTIAL" // When login information is incorrect

const val INVALID_ADMIN_CREDENTIAL = "INVALID_ADMIN_CREDENTIAL" // When login information is not admin

const val UNKNOWN_ERROR = "UNKNOWN_ERROR"

const val NO_STATUS_RECEIVED = "NO_STATUS_RECEIVED"

const val USER_EMAIL_UNVERIFIED = "USER_EMAIL_UNVERIFIED"

const val INVALID_EVENT = "INVALID_EVENT"

const val INVALID_EVENT_DEVICE_ID = "INVALID_EVENT_DEVICE_ID"

const val EVENT_INSUFFICIENT_VOUCHER = "EVENT_INSUFFICIENT_VOUCHER"