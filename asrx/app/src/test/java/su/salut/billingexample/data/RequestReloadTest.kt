package su.salut.billingexample.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import su.salut.billingexample.data.fortesting.RequestReloadImplForTesting
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.properties.Delegates

internal class RequestReloadTest {
    private lateinit var testRequestReload: RequestReloadImplForTesting
    private lateinit var throwable: Throwable
    private var numberOfAttempts: Int by Delegates.notNull()

    @Before
    fun setUp() {
        testRequestReload = RequestReloadImplForTesting(
            counterReloadTime = COUNTER_RELOAD_TIME,
            totalCounterReloadTime = TOTAL_COUNT_QUERY_TIME
        )
        throwable = Throwable("Testing the returned error")
        numberOfAttempts = ceil(TOTAL_COUNT_QUERY_TIME.toDouble() / COUNTER_RELOAD_TIME.toDouble()).toInt()
    }

    @Test
    fun `checking the work of Single`() {
        // arrange
        val item = 1
        val single = Single.just(item)
        // act
        val testObserver = testRequestReload.testSingle(single).test()
        // assert
        testObserver
            .assertValueCount(1)
            .assertResult(item)
    }

    @Test
    fun `checking for a never terminating Single`() {
        // arrange
        var currentTimeMillis: Long = 0
        var executionTimeMillis: Long = 0
        val single = Single.never<Unit>()
        // act
        val testObserver = testRequestReload.testSingle(single)
            .doOnSubscribe { currentTimeMillis = System.currentTimeMillis() }
            .doFinally { executionTimeMillis = System.currentTimeMillis().minus(currentTimeMillis) }
            .test()
            .awaitDone(TOTAL_COUNT_QUERY_TIME + 100, TimeUnit.MILLISECONDS)
        // assert
        testObserver.assertError(testRequestReload::isTimeoutException)
        // Convert to sec
        Assert.assertEquals(executionTimeMillis.div(1000), TOTAL_COUNT_QUERY_TIME.div(1000))
    }

    @Test
    fun `checking the returned error and the number of attempts Single`() {
        // arrange
        var attempts = 0
        var currentTimeMillis: Long = 0
        var executionTimeMillis: Long = 0
        val single = Single.create<Unit> { emitter ->
            attempts++
            emitter.onError(throwable)
        }
        // act
        val testObserver = testRequestReload.testSingle(single)
            .doOnSubscribe { currentTimeMillis = System.currentTimeMillis() }
            .doFinally { executionTimeMillis = System.currentTimeMillis().minus(currentTimeMillis) }
            .test()
            .awaitDone(TOTAL_COUNT_QUERY_TIME + 100, TimeUnit.MILLISECONDS)
        // assert
        testObserver.assertError(throwable)
        Assert.assertEquals(attempts, numberOfAttempts)
        // Convert to sec
        Assert.assertEquals(executionTimeMillis.div(100), TOTAL_COUNT_QUERY_TIME.div(100))
    }

    @Test
    fun `checking that the request was not executed on the first try Single`() {
        // arrange
        val complete = numberOfAttempts - 1
        var attempts = 0
        val single = Single.create<Int> { emitter ->
            attempts++
            if (attempts == complete) emitter.onSuccess(attempts)
            else emitter.onError(throwable)
        }
        // act
        val testObserver = testRequestReload.testSingle(single).test()
            .awaitDone(TOTAL_COUNT_QUERY_TIME, TimeUnit.MILLISECONDS)
        // assert
        testObserver
            .assertValueCount(1)
            .assertValue(complete)
    }


    @Test
    fun `checking the work of Completable`() {
        // arrange
        val completable = Completable.complete()
        // act
        val testObserver = testRequestReload.testCompletable(completable).test()
        // assert
        testObserver.assertComplete()
    }

    @Test
    fun `checking for a never terminating Completable`() {
        // arrange
        var currentTimeMillis: Long = 0
        var executionTimeMillis: Long = 0
        val completable = Completable.never()
        // act
        val testObserver = testRequestReload.testCompletable(completable)
            .doOnSubscribe { currentTimeMillis = System.currentTimeMillis() }
            .doFinally { executionTimeMillis = System.currentTimeMillis().minus(currentTimeMillis) }
            .test()
            .awaitDone(TOTAL_COUNT_QUERY_TIME + 100, TimeUnit.MILLISECONDS)
        // assert
        testObserver.assertError(testRequestReload::isTimeoutException)
        // Convert to sec
        Assert.assertEquals(executionTimeMillis.div(100), TOTAL_COUNT_QUERY_TIME.div(100))
    }

    @Test
    fun `checking the returned error and the number of attempts Completable`() {
        // arrange
        var attempts = 0
        var currentTimeMillis: Long = 0
        var executionTimeMillis: Long = 0
        val completable = Completable.create { emitter ->
            attempts++
            emitter.onError(throwable)
        }
        // act
        val testObserver = testRequestReload.testCompletable(completable)
            .doOnSubscribe { currentTimeMillis = System.currentTimeMillis() }
            .doFinally { executionTimeMillis = System.currentTimeMillis().minus(currentTimeMillis) }
            .test()
            .awaitDone(TOTAL_COUNT_QUERY_TIME + 100, TimeUnit.MILLISECONDS)
        // assert
        testObserver.assertError(throwable)
        Assert.assertEquals(attempts, numberOfAttempts)
        // Convert to sec
        Assert.assertEquals(executionTimeMillis.div(100), TOTAL_COUNT_QUERY_TIME.div(100))
    }

    @Test
    fun `checking that the request was not executed on the first try Completable`() {
        // arrange
        val complete = numberOfAttempts - 1
        var attempts = 0
        val completable = Completable.create { emitter ->
            attempts++
            if (attempts == complete) emitter.onComplete()
            else emitter.onError(throwable)
        }
        // act
        val testObserver = testRequestReload.testCompletable(completable).test()
            .awaitDone(TOTAL_COUNT_QUERY_TIME, TimeUnit.MILLISECONDS)
        // assert
        testObserver.assertComplete()
        Assert.assertEquals(attempts, complete)
    }

    companion object {
        private const val COUNTER_RELOAD_TIME = 400L
        private const val TOTAL_COUNT_QUERY_TIME = 1_000L
    }
}