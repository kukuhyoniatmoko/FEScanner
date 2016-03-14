package com.foodenak.itpscanner.services

/**
 * Created by ITP on 10/6/2015.
 */
class Result<T> {

    var total: Int = 0

    var currentPage: Int = 0

    var lastPage: Int = 0

    var perPage: Int = 0

    var from: Int = 0

    var to: Int = 0

    var data: List<T>? = null;

    fun isComplete(): Boolean {
        return currentPage == lastPage
    }
}