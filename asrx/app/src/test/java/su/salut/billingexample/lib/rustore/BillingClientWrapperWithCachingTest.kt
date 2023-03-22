package su.salut.billingexample.lib.rustore

import org.junit.Before
import org.junit.Test
import su.salut.billingexample.extensions.lib.rustore.BillingClientWrapperWithCaching
import su.salut.billingexample.lib.rustore.fortesting.BillingClientWrapperImplForTesting
import su.salut.billingexample.lib.rustore.fortesting.EntitiesFactory

internal class BillingClientWrapperWithCachingTest {

    private val factory = EntitiesFactory()

    private val returnProduct1 = factory.generateProduct("Test product id 1")
    private val returnProduct2 = factory.generateProduct("Test product id 2")
    private val returnProduct3 = factory.generateProduct("Test product id 3")

    private val returnPurchase1 = factory.generatePurchase()
    private val returnPurchase2 = factory.generatePurchase()
    private val returnPurchase3 = factory.generatePurchase()

    private val listProducts1 = listOf(returnProduct1, returnProduct2)
    private val listProducts2 = listOf(returnProduct2, returnProduct3)

    private val listPurchases1 = listOf(returnPurchase1, returnPurchase2)
    private val listPurchases2 = listOf(returnPurchase2, returnPurchase3)

    private val throwable = Throwable("Testing the returned error")

    private val testBillingClient = BillingClientWrapperImplForTesting()
    private val testCaching = BillingClientWrapperWithCaching(
        billingClientWrapper = testBillingClient,
        lifetimeMillis = CACHE_LIFETIME_MILLIS
    )

    @Before
    fun setUp() {
        testBillingClient.reset(
            returnPurchases = listOf(listPurchases1, listPurchases2, throwable),
            returnProducts = listOf(listProducts1, listProducts2, throwable)
        )
    }

    @Test
    fun `checking adding products to the cache`() {
        // arrange
        val listProductsCombined = listOf(returnProduct1, returnProduct3)
        val productIds1 = listProducts1.map { it.productId }
        val productIds2 = listProducts2.map { it.productId }
        val productIdsCombined = listProductsCombined.map { it.productId }

        // act
        val actObserver1 = testCaching.getProductsAsSingle(productIds1).test()
        val actObserver2 = testCaching.getProductsAsSingle(productIds2).test()
        val testObserver = testCaching.getProductsAsSingle(productIdsCombined).test()

        // assert
        actObserver1 // assert Act
            .assertValueCount(1)
            .assertValue(listProducts1)
        actObserver2 // assert Act
            .assertValueCount(1)
            .assertValue(listProducts2)
        testObserver // assert answer
            .assertValueCount(1)
            .assertValue(listProductsCombined)
    }

    @Test
    fun `checking adding purchases to the cache`() {
        // arrange

        // act
        val actObserver1 = testCaching.getPurchasesAsSingle().test()
        Thread.sleep(CACHE_LIFETIME_MILLIS.div(2))
        val actObserver2 = testCaching.getPurchasesAsSingle().test()
        Thread.sleep(CACHE_LIFETIME_MILLIS.div(2) + 10)
        val actObserver3 = testCaching.getPurchasesAsSingle().test()

        Thread.sleep(CACHE_LIFETIME_MILLIS + 10)
        val testObserver = testCaching.getPurchasesAsSingle().test()

        // assert
        actObserver1 // assert Act
            .assertValueCount(1)
            .assertValue(listPurchases1)
        actObserver2 // assert Act
            .assertValueCount(1)
            .assertValue(listPurchases1)
        actObserver3 // assert Act
            .assertValueCount(1)
            .assertValue(listPurchases2)

        testObserver // assert answer
            .assertError(throwable)
    }

    companion object {
        private const val CACHE_LIFETIME_MILLIS = 100L
    }
}