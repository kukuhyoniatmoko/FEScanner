package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.RedeemParameter
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.EventModel
import rx.Observable

/**
 * Created by ITP on 10/8/2015.
 */
class RedeemInteractor(val model: EventModel) : Interactor<User, RedeemInteractor.Argument> {
    override fun execute(args: Argument): Observable<User> {
        val param = RedeemParameter()
        param.userHashId = args.userId
        param.redeemLuckydip = if (args.redeemLuckyDip) 1 else 0
        param.redeemVoucher = if (args.redeemVoucher) 1 else 0
        return model.redeem(args.eventId, param)

    }

    data class Argument(val eventId: Long, val userId: String, val redeemVoucher: Boolean, val redeemLuckyDip: Boolean)

    data class Result(val user: User?, val voucherRemaining: Int)
}