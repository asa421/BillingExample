package su.salut.billingexample.data.fortesting

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import su.salut.billingexample.application.data.RequestReload
import su.salut.billingexample.application.domain.models.BillingException

class RequestReloadImplForTesting(
    override val counterReloadTime: Long,
    override val totalCounterReloadTime: Long
): RequestReload() {
    fun <T : Any> testSingle(single: Single<T>): Single<T> {
        return single.requestReload()
    }

    fun testCompletable(completable: Completable): Completable {
        return completable.requestReload()
    }

    fun isTimeoutException(throwable: Throwable): Boolean {
        val timeoutException = BillingException.getTimeoutException()

        return throwable is BillingException &&
                throwable.responseCode == timeoutException.responseCode
    }
}