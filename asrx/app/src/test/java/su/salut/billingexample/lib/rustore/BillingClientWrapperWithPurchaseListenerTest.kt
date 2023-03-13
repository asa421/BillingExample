package su.salut.billingexample.lib.rustore

import io.reactivex.rxjava3.core.Completable
import org.junit.Test
import su.salut.billingexample.lib.rustore.fortesting.BillingClientWrapperImplForTesting
import su.salut.billingexample.lib.rustore.fortesting.EntitiesFactory

internal class BillingClientWrapperWithPurchaseListenerTest {

    private val productId = "Test product id"
    private val purchaseId = "Test purchase id"

    private val factory = EntitiesFactory()
    private val returnPurchase1 = factory.generatePurchase()
    private val returnPurchase2 = factory.generatePurchase(productId = productId)

    private val throwable = Throwable("Testing the returned error")

    private val testBillingClient = BillingClientWrapperImplForTesting()
    private val testPurchaseListener = BillingClientWrapperWithPurchaseListener(testBillingClient)

    @Test
    fun `checking the deletion of the purchase`() {
        // arrange
        testBillingClient.reset(returnCompletable = Completable.complete())

        // act
        val testObserver = testPurchaseListener.getPurchasesUpdatedAsObservable().test()
        val actObserver = testPurchaseListener.deletePurchaseAsCompletable(purchaseId).test()

        // assert
        actObserver // assert Act
            .assertNoErrors()
            .assertComplete()
        testObserver // assert answer
            .assertValueCount(1)
            .assertValue(emptyList())
    }

    @Test
    fun `checking for error deleting a purchase`() {
        // arrange
        testBillingClient.reset(returnCompletable = Completable.error(throwable))

        // act
        val testObserver = testPurchaseListener.getPurchasesUpdatedAsObservable().test()
        val actObserver = testPurchaseListener.deletePurchaseAsCompletable(purchaseId).test()

        // assert
        actObserver // assert Act
            .assertError(throwable)
            .assertNotComplete()
        testObserver // assert answer
            .assertValueCount(0)
    }

    @Test
    fun `checking the confirm of the purchase`() {
        // arrange
        testBillingClient.reset(returnCompletable = Completable.complete())

        // act
        val testObserver = testPurchaseListener.getPurchasesUpdatedAsObservable().test()
        val actObserver = testPurchaseListener.confirmPurchaseAsCompletable(purchaseId).test()

        // assert
        actObserver // assert Act
            .assertNoErrors()
            .assertComplete()
        testObserver // assert answer
            .assertValueCount(1)
            .assertValue(emptyList())
    }

    @Test
    fun `checking for error confirm a purchase`() {
        // arrange
        testBillingClient.reset(returnCompletable = Completable.error(throwable))

        // act
        val testObserver = testPurchaseListener.getPurchasesUpdatedAsObservable().test()
        val actObserver = testPurchaseListener.confirmPurchaseAsCompletable(purchaseId).test()

        // assert
        actObserver // assert Act
            .assertError(throwable)
            .assertNotComplete()
        testObserver // assert answer
            .assertValueCount(0)
    }

    @Test
    fun `checking the purchase of the product`() {
        // arrange
        val listPurchases1 = listOf(returnPurchase1, returnPurchase2)
        testBillingClient.reset(
            returnPurchases = listOf(listPurchases1, listPurchases1),
            returnCompletable = Completable.complete()
        )

        // act
        val testObserver = testPurchaseListener.getPurchasesUpdatedAsObservable().test()
        val actObserver = testPurchaseListener.purchaseProductAsCompletable(productId).test()

        // assert
        actObserver // assert Act
            .assertNoErrors()
            .assertComplete()
        testObserver // assert answer
            .assertValueCount(1)
            .assertValue(listOf(returnPurchase2))
    }

    @Test
    fun `checking for error purchase a product`() {
        // arrange
        val listPurchases1 = listOf(returnPurchase1, returnPurchase2)
        testBillingClient.reset(
            returnPurchases = listOf(listPurchases1, listPurchases1),
            returnCompletable = Completable.error(throwable)
        )

        // act
        val testObserver = testPurchaseListener.getPurchasesUpdatedAsObservable().test()
        val actObserver = testPurchaseListener.purchaseProductAsCompletable(productId).test()
        // assert
        actObserver // assert Act
            .assertError(throwable)
            .assertNotComplete()
        testObserver // assert answer
            .assertValueCount(1)
            .assertValue(emptyList())
    }
}