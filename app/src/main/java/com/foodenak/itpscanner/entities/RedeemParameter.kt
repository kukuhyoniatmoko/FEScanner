package com.foodenak.itpscanner.entities

import java.io.Serializable

/**
 * Created by ITP on 10/6/2015.
 */
data class RedeemParameter(

        var userHashId: String? = null,

        var redeemVoucher: Int? = null,

        var redeemLuckydip: Int? = null) : Serializable {
}