package com.foodenak.itpscanner.model

import android.content.Context
import com.foodenak.itpscanner.entities.FacebookCredential
import com.foodenak.itpscanner.entities.GoogleCredential
import com.foodenak.itpscanner.entities.TwitterCredential
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.persistence.UserRepository
import com.foodenak.itpscanner.services.ResponseWrapper
import com.foodenak.itpscanner.services.UserService
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.services.emptyBody
import com.foodenak.itpscanner.services.exception.validateAdmin
import com.foodenak.itpscanner.services.exception.validateResponse
import okhttp3.Credentials
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Singleton
class UserModel @Inject constructor(val context: Context, val repository: UserRepository, val service: UserService) {

    fun login(user: User): Observable<User> {
        val basic = Credentials.basic(user.username, user.password)
        return service.login(basic, emptyBody())
                .saveUser()
    }

    fun editProfile(user: User): Observable<User> {
        return repository.getUser(user.hashId!!)
                .flatMap { oldUser ->
                    oldUser.email = user.email
                    oldUser.username = user.username
                    oldUser.name = user.name
                    repository.saveUser(oldUser)
                    service.editProfile(user)
                }.validateResponse()
                .map { wrapper ->
                    return@map wrapper.result!!
                }
                .doOnNext { user ->
                    repository.saveUser(user!!)
                    UserSession.initialize(UserSession(user.hashId!!, user.accessToken!!), context)
                    UserSession.currentSession.user = user
                }
    }

    fun editPassword(password: String, newPassword: String): Observable<User> {
        val map = hashMapOf(
                Pair("old_password_confirmation", password),
                Pair("password", newPassword),
                Pair("password_confirmation", newPassword)
        )
        return service.editPassword(map)
                .validateResponse()
                .map { wrapper ->
                    return@map wrapper.result!!
                }
                .doOnNext { user ->
                    repository.saveUser(user!!)
                    UserSession.initialize(UserSession(user.hashId!!, user.accessToken!!), context)
                    UserSession.currentSession.user = user
                }
    }

    fun loginWithTwitter(credential: TwitterCredential): Observable<User> {
        return service.loginWithTwitter(credential)
                .saveUser()
    }

    fun loginWithGoogle(credential: GoogleCredential): Observable<User> {
        return service.loginWithGoogle(credential)
                .saveUser()
    }

    fun loginWithFacebook(credential: FacebookCredential): Observable<User> {
        return service.loginWithFacebook(credential)
                .saveUser()
    }

    fun Observable<ResponseWrapper<User>>.saveUser(): Observable<User> {
        return this.compose(userSaver)
    }

    val userSaver = Observable.Transformer<ResponseWrapper<User>, User> { t ->
        t.validateResponse()
                .validateAdmin()
                .map { wrapper ->
                    return@map wrapper.result!!
                }
                .doOnNext { user ->
                    repository.saveUser(user!!)
                    UserSession.initialize(UserSession(user.hashId!!, user.accessToken!!), context)
                    UserSession.currentSession.user = user
                }
    }

    fun getUser(hashId: String): Observable<User> {
        return repository.getUser(hashId)
    }
}