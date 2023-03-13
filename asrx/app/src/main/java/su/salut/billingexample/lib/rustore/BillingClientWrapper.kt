package su.salut.billingexample.lib.rustore

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.rustore.sdk.billingclient.model.common.ResponseWithCode
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.purchase.PaymentFinishCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase

/**
 * Let's convert the RuStore billing library through RxJava methods.
 *
 * @date 06.02.2023
 * @author asa421
 */
interface BillingClientWrapper {

    /** Check the answer that there are no errors. */
    fun isOkResponse(responseWithCode: ResponseWithCode): Boolean {
        return responseWithCode.code == ResponseCode.OK
    }

    /** Check the answer that there are no errors. */
    fun isOkPaymentResult(paymentResult: PaymentResult): Boolean {
        return paymentResult is PaymentResult.PurchaseResult &&
                paymentResult.finishCode == PaymentFinishCode.SUCCESSFUL_PAYMENT
    }

    /**
     * Checking the availability of working with payments.
     *
     * @exception RuStoreException(message: String)
     */
    @Suppress("KDocUnresolvedReference")
    fun checkPurchasesAvailabilityAsCompletable(): Completable

    /**
     * Cancellation of purchase.
     *
     * @exception BillingResultException
     */
    fun deletePurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Completable

    /**
     * Purchase consumption.
     *
     * @exception BillingResultException
     */
    fun confirmPurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Completable

    /**
     * Buying a product.
     *
     * @exception BillingResultException
     */
    fun purchaseProductAsCompletable(
        productId: String,
        isOkPaymentResult: IsOkPaymentResult = ::isOkPaymentResult
    ): Completable

    /**
     * Getting a user's shopping list.
     *
     * @exception BillingResultException
     */
    fun getPurchasesAsSingle(isOkResponse: IsOkResponse = ::isOkResponse): Single<List<Purchase>>

    /**
     * Getting up-to-date information on the list of products.
     *
     * @exception BillingResultException
     */
    fun getProductsAsSingle(
        productIds: List<String>,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Single<List<Product>>
}