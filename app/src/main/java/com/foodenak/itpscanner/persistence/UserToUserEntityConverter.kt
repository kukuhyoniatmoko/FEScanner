package com.foodenak.itpscanner.persistence

import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.persistence.db.UserEntity

/**
 * Created by ITP on 10/8/2015.
 */
class UserToUserEntityConverter : Converter<User?, UserEntity?> {
    override fun convert(source: User?): UserEntity? {
        if (source == null) {
            return null
        }
        val result = UserEntity()
        result.hashId = source.hashId
        result.name = source.name
        result.username = source.username
        result.email = source.email
        result.accessToken = source.accessToken
        result.userPrivilegeId = source.userPrivilegeId
        result.imageUrl = source.thumbUrl?.original
        val pivot = source.pivot ?: return result
        result.redeemVoucherAt = pivot.redeemVoucherAt
        result.redeemLuckydipAt = pivot.redeemLuckydipAt
        //        if(result.redeemLuckydipAt != null || result.redeemVoucherAt != null){
        //            if(result.redeemLuckydipAt != null && result.redeemVoucherAt == null){
        //                result.lastRedeemDate = result.redeemLuckydipAt
        //            } else if (result.redeemLuckydipAt == null && result.redeemVoucherAt != null){
        //                result.lastRedeemDate = result.redeemVoucherAt
        //            } else if(result.redeemLuckydipAt != null && result.redeemVoucherAt != null){
        //                result.lastRedeemDate = Date(Math.max(result.redeemLuckydipAt.time, result.redeemVoucherAt.time))
        //            }
        //        }
        return result
    }
}