package com.foodenak.itpscanner.ui.profile

import com.foodenak.itpscanner.entities.User

/**
 * Created by ITP on 10/12/2015.
 */
interface ProfileView {
    fun hideConfirmNewPasswordErrorMessage()

    fun hideNewPasswordErrorMessage()

    fun hidePasswordErrorMessage()

    fun hideEmailErrorMessage()

    fun hideUsernameErrorMessage()

    fun hideNameErrorMessage()

    fun validateProfile()

    fun validatePassword()

    fun initializeUser(user: User)
}