package com.foodenak.itpscanner.model

import android.util.Log
import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.entities.HistoryParameter
import com.foodenak.itpscanner.entities.RedeemParameter
import com.foodenak.itpscanner.entities.RegisterForEventParameter
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.persistence.EventRepository
import com.foodenak.itpscanner.persistence.UserRepository
import com.foodenak.itpscanner.persistence.VoucherRepository
import com.foodenak.itpscanner.services.EventService
import com.foodenak.itpscanner.services.PoolService
import com.foodenak.itpscanner.services.exception.validateResponse
import rx.Observable
import rx.schedulers.Schedulers
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Singleton
class EventModel @Inject constructor(val userRepository: UserRepository,
    val voucherRepository: VoucherRepository,
    val repository: EventRepository, val service: EventService, val poolService: PoolService) {

  fun getEvents(): Observable<List<Event>> {
    return Observable.merge(repository.getEvents(), service.getEvents().validateResponse()
        .map { wrapper -> wrapper.results!!.data!! }.doOnNext { events ->
      repository.deleteAllEvent()
      repository.saveEvents(events)
    })
  }

  fun getEvent(id: Long): Observable<Event> {
    return Observable.merge(repository.getEvent(id), service.getEvent(id).validateResponse()
        .map { wrapper -> wrapper.result!! }.doOnNext { event -> repository.saveEvent(event) })
  }

  fun register(id: Long, parameter: RegisterForEventParameter): Observable<User> {
    return service.register(id, parameter).validateResponse().map { wrapper -> wrapper.result!! }
        .doOnNext { user -> userRepository.saveUser(user) }
  }

  fun redeem(id: Long, parameter: RedeemParameter): Observable<User> {
    return service.redeem(id, parameter).validateResponse().map { wrapper ->
      userRepository.saveUser(wrapper.result!!)
      voucherRepository.setVoucherRemaining(wrapper.voucherRemaining)
      wrapper.result!!
    }
  }

  fun getHistory(id: Long, parameter: HistoryParameter): Observable<List<User>> {
    return service.getHistory(id, parameter.createMap()).validateResponse().map {
      voucherRepository.setVoucherRemaining(it.voucherRemaining)
      if (parameter.isEmpty() && it.results!!.data!!.isNotEmpty()) saveLastHistory(it.results?.data)
      it.results!!.data!!
    }
  }

  fun getVoucherRemaining(): Observable<Int> = voucherRepository.getVoucherRemaining()

  internal fun saveLastHistory(users: List<User>?) {
    users ?: return
    if (users.isEmpty()) return
    val pivot = users[0].pivot
    if (pivot != null) if (pivot.redeemLuckydipAt != null || pivot.redeemVoucherAt != null) {
      if (pivot.redeemLuckydipAt != null && pivot.redeemVoucherAt == null) {
        val lastRedeemDate = pivot.redeemLuckydipAt!!
        repository.setLastHistory(lastRedeemDate.time)
      } else if (pivot.redeemLuckydipAt == null && pivot.redeemVoucherAt != null) {
        val lastRedeemDate = pivot.redeemVoucherAt!!
        repository.setLastHistory(lastRedeemDate.time)
      } else if (pivot.redeemLuckydipAt != null && pivot.redeemVoucherAt != null) {
        val lastRedeemDate = Date(
            Math.max(pivot.redeemLuckydipAt!!.time, pivot.redeemVoucherAt!!.time))
        repository.setLastHistory(lastRedeemDate.time)
      }
    }
  }

  fun poolHistory(id: Long): Observable<List<User>> {
    return Observable.create<List<User>> {
      val worker = Schedulers.io().createWorker();
      it.add(worker)
      worker.schedulePeriodically({
        Log.d("PoolHistory", "pooling start ...")
        try {
          val time = repository.getLastHistory()
          val lastTime = if (time == EventRepository.INVALID_HISTORY_TIME) {
            time
          } else {
            System.currentTimeMillis()
          }
          val wrapper = poolService.getHistory(id,
              HistoryParameter(id, filterAfter = Date(lastTime)).createMap())
              .validateResponse()
              .toBlocking()
              .firstOrDefault(null) ?: return@schedulePeriodically
          voucherRepository.setVoucherRemaining(wrapper.voucherRemaining)
          if (wrapper.results?.data == null) return@schedulePeriodically
          saveLastHistory(wrapper.results?.data)
          it.onNext(wrapper.results!!.data!!)
        } catch (e: Exception) {
          Log.d("PoolHistory", "pooling error ...", e)
          it.onError(e)
        }
        Log.d("PoolHistory", "pooling finish ...")
      }, 0, 0, TimeUnit.MINUTES);
    }
  }
}