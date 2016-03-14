package com.foodenak.itpscanner.persistence

import com.foodenak.itpscanner.entities.ThumbUrl
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.entities.UserPivot
import com.foodenak.itpscanner.persistence.db.UserEntity

/**
 * Created by ITP on 10/8/2015.
 */
class UserEntityToUserConverter : Converter<UserEntity?, User?> {
    override fun convert(source: UserEntity?): User? {
        if (source == null) {
            return null
        }
        val result = User()
        result.hashId = source.hashId
        result.name = source.name
        result.username = source.username
        result.email = source.email
        result.accessToken = source.accessToken
        result.userPrivilegeId = source.userPrivilegeId
        result.thumbUrl = if (source.imageUrl == null) null else ThumbUrl(source.imageUrl)
        if (source.redeemVoucherAt != null || source.redeemLuckydipAt != null) {
            result.pivot = UserPivot(source.redeemVoucherAt, source.redeemLuckydipAt)
        }
        return result
    }
}