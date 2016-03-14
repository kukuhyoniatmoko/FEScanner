package com.foodenak.itpscanner.entities

import java.io.Serializable

/**
 * Created by ITP on 10/5/2015.
 */
data class TwitterCredential(

        var twId: String? = null,

        var name: String? = null,

        var twitterProfilePicture: String? = null,

        var twitterAccessToken: String? = null,

        var twitterAccessTokenSecret: String? = null,

        var email: String? = null) : Serializable