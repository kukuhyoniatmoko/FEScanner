package com.foodenak.itpscanner.entities

import java.io.Serializable

/**
 * Created by ITP on 10/5/2015.
 */
data class FacebookCredential(

        var fbId: String? = null,

        var name: String? = null,

        var email: String? = null,

        var gender: String? = null) : Serializable {
}
