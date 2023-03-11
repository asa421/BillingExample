package su.salut.data.lib.rustore

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import ru.rustore.sdk.billingclient.model.common.ResponseWithCode
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase

/**
 * Let's convert the RuStore billing library through RxJava methods.
 *
 * To track changes in purchases, you need to follow the methods of working with purchases,
 * these are confirmation, deletion and creation of a purchase!
 *
 * @date 02.03.2023
 * @author asa421
 */
class BillingClientWrapperWithPurchaseListener(
    private val billingClientWrapper: BillingClientWrapper
) : BillingClientWrapper {

    private val purchasesUpdatedBySubject = PublishSubject.create<List<Purchase>>()

    /** Purchase change listener. */
    fun getPurchasesUpdatedAsObservable(): Observable<List<Purchase>> {
        return purchasesUpdatedBySubject
    }

    /** Check the answer that there are no errors. */
    override fun isOkResponse(responseWithCode: ResponseWithCode): Boolean {
        return billingClientWrapper.isOkResponse(responseWithCode)
    }

    /** Check the answer that there are no errors. */
    override fun isOkPaymentResult(paymentResult: PaymentResult): Boolean {
        return billingClientWrapper.isOkPaymentResult(paymentResult)
    }

    /**
     * Checking the availability of working with payments.
     *
     * @exception RuStoreException(message: String)
     */
    @Suppress("KDocUnresolvedReference")
    override fun checkPurchasesAvailabilityAsCompletable(): Completable {
        return billingClientWrapper.checkPurchasesAvailabilityAsCompletable()
    }

    /**
     * Cancellation of purchase.
     *
     * @exception BillingResultException
     */
    override fun deletePurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return billingClientWrapper.deletePurchaseAsCompletable(purchaseId, isOkResponse)
            .doOnComplete { purchasesUpdatedBySubject.onNext(emptyList()) }
    }

    /**
     * Purchase consumption.
     *
     * @exception BillingResultException
     */
    override fun confirmPurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return billingClientWrapper.confirmPurchaseAsCompletable(purchaseId, isOkResponse)
            .doOnComplete { purchasesUpdatedBySubject.onNext(emptyList()) }
    }

    /**
     * Buying a product.
     *
     * @exception BillingResultException
     */
    override fun purchaseProductAsCompletable(
        productId: String,
        isOkPaymentResult: IsOkPaymentResult
    ): Completable {
        return billingClientWrapper.purchaseProductAsCompletable(productId, isOkPaymentResult)
            .doOnError { purchasesUpdatedBySubject.onNext(emptyList()) }
            // Tracking purchases in purchasesUpdatedBySubject
            .andThen(Completable.fromSingle(reloadPurchasesUpdatedAsSingle(productId)))
    }

    /**
     * Getting a user's shopping list.
     *
     * @exception BillingResultException
     */
    override fun getPurchasesAsSingle(isOkResponse: IsOkResponse): Single<List<Purchase>> {
        return billingClientWrapper.getPurchasesAsSingle(isOkResponse)
    }

    /**
     * Getting up-to-date information on the list of products.
     *
     * @exception BillingResultException
     */
    override fun getProductsAsSingle(
        productIds: List<String>,
        isOkResponse: IsOkResponse
    ): Single<List<Product>> {
        return billingClientWrapper.getProductsAsSingle(productIds, isOkResponse)
    }

    private fun reloadPurchasesUpdatedAsSingle(productId: String): Single<List<Purchase>> {
        return getPurchasesAsSingle(::isOkResponse)
            .map { purchases -> purchases.filter { it.productId == productId } }
            .doAfterSuccess(purchasesUpdatedBySubject::onNext)
    }
}