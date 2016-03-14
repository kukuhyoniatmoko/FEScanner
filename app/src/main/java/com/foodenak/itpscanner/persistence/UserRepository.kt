package com.foodenak.itpscanner.persistence

import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.persistence.dao.UserEntityDao
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Singleton
class UserRepository @Inject constructor(val dao: UserEntityDao) {

    val toEntityConverter = UserToUserEntityConverter()

    val toUserConverter = UserEntityToUserConverter()

    val userQuery = dao.queryBuilder()
            .whereOr(UserEntityDao.Properties.Username.eq(null), UserEntityDao.Properties.HashId.eq(null))
            .build()

    fun getUser(hashId: String): Observable<User> {
        return Observable.defer {
            val query = userQuery.forCurrentThread()
            query.setParameter(0, hashId)
            query.setParameter(1, hashId)
            val entity = query.unique()
            val user = toUserConverter.convert(entity) ?: return@defer Observable.empty<User>()
            return@defer Observable.just(user)
        }
    }

    fun saveUser(user: User): Long {
        val newEntity = toEntityConverter.convert(user)

        val query = userQuery.forCurrentThread()
        query.setParameter(0, user.username)
        query.setParameter(1, user.hashId)
        val entity = query.unique() ?: return dao.insert(newEntity)
        newEntity!!.id = entity.id
        dao.update(newEntity)
        return entity.id
    }
}