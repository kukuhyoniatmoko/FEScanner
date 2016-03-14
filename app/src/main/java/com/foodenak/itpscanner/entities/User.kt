package com.foodenak.itpscanner.entities

import java.io.Serializable

/**
 * Created by ITP on 10/5/2015.
 */
data class User(

        var hashId: String? = null,

        var accessToken: String? = null,

        var name: String? = null,

        var username: String? = null,

        var password: String? = null,

        var email: String? = null,

        var userPrivilegeId: Int = -1,

        var thumbUrl: ThumbUrl? = null,

        var pivot: UserPivot? = null) : Serializable