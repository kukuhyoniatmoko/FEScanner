package com.foodenak.itpscanner.entities

import java.io.Serializable
import java.util.*

/**
 * Created by ITP on 10/8/2015.
 */
data class UserPivot constructor(var redeemVoucherAt: Date? = null, var redeemLuckydipAt: Date? = null) : Serializable