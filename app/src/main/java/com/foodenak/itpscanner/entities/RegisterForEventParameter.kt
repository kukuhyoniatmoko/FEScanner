package com.foodenak.itpscanner.entities

import java.io.Serializable

/**
 * Created by ITP on 10/6/2015.
 */
data class RegisterForEventParameter(var userHashId: String? = null, var deviceId: String? = null) : Serializable {
}
