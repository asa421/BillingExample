package su.salut.billingexample.application.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import su.salut.billingexample.application.domain.models.BillingException
import java.util.concurrent.TimeUnit

/**
 * @date 31.01.2023
 * @author asa421
 */
abstract class RequestReload {
    /** The time after which to try to reload the request. */
    protected abstract val counterReloadTime: Long

    /** The total request time before the error. */
    protected abstract val totalCounterReloadTime: Long

    /**
     * Execute the request within the specified timeout,
     * if an error is attempted to reload it with a delay.
     *
     * @throws BillingException
     */
    fun <T : Any> Single<T>.requestReload(): Single<T> {
        var throwable: Throwable = BillingException.getTimeoutException()
        val delayError = Single.create<T> { it.onError(throwable) }

        return retryWhen { flowableThrowable ->
            flowableThrowable
                .doAfterNext { throwable = it }
                .delay(counterReloadTime, TimeUnit.MILLISECONDS)
        }.timeout(totalCounterReloadTime, TimeUnit.MILLISECONDS, delayError)
    }

    /**
     * Execute the request within the specified timeout,
     * if an error is attempted to reload it with a delay.
     *
     * @throws BillingException
     */
    fun Completable.requestReload(): Completable {
        var throwable: Throwable = BillingException.getTimeoutException()
        val delayError = Completable.create { it.onError(throwable) }

        return retryWhen { flowableThrowable ->
            flowableThrowable
                .doAfterNext { throwable = it }
                .delay(counterReloadTime, TimeUnit.MILLISECONDS)
        }.timeout(totalCounterReloadTime, TimeUnit.MILLISECONDS, delayError)
    }
}