package su.salut.billingexample.lib.rustore

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.product.ProductsResponse
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.billingclient.model.purchase.response.ConfirmPurchaseResponse
import ru.rustore.sdk.billingclient.model.purchase.response.DeletePurchaseResponse
import ru.rustore.sdk.billingclient.model.purchase.response.PurchasesResponse
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import ru.rustore.sdk.core.tasks.OnCompleteListener

/**
 * Let's convert the RuStore billing library through RxJava methods.
 *
 * @date 06.02.2023
 * @author asa421
 * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/quick_start
 */
class BillingClientWrapperImpl(factory: GoogleBillingFactory) : BillingClientWrapper {

    private val billingClient: RuStoreBillingClient by lazy {
        return@lazy factory.buildBillingClient()
    }

    /**
     * Checking the availability of working with payments.
     *
     * @exception RuStoreNotInstalledException() - RuStore is not installed on the user's device;
     * @exception RuStoreOutdatedException() - RuStore installed on the user's device does not support payments;
     * @exception RuStoreUserUnauthorizedException() - the user is not authorized in RuStore;
     * @exception RuStoreApplicationBannedException() - the application is blocked in RuStore;
     * @exception RuStoreUserBannedException() - the user is blocked in RuStore;
     * @exception RuStoreException(message: String) is the base RuStore error from which all other errors are inherited.
     *
     * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/checking-availability-work-payments
     */
    @Suppress("KDocUnresolvedReference")
    override fun checkPurchasesAvailabilityAsCompletable(): Completable {
        return Completable.create { emitter ->
            billingClient.purchases.checkPurchasesAvailability()
                .addOnCompleteListener(object : OnCompleteListener<FeatureAvailabilityResult> {
                    override fun onFailure(throwable: Throwable) {
                        emitter.onError(throwable)
                    }

                    override fun onSuccess(result: FeatureAvailabilityResult) {
                        when (result) {
                            is FeatureAvailabilityResult.Available -> emitter.onComplete()
                            is FeatureAvailabilityResult.Unavailable -> emitter.onError(result.cause)
                        }
                    }
                })
        }
    }

    /**
     * Cancellation of purchase.
     *
     * @exception BillingResultException
     * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/%20Cancellation-purchase
     */
    override fun deletePurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return Completable.create { emitter ->
            billingClient.purchases.deletePurchase(purchaseId)
                .addOnCompleteListener(object : OnCompleteListener<DeletePurchaseResponse> {
                    override fun onFailure(throwable: Throwable) {
                        emitter.onError(throwable)
                    }

                    override fun onSuccess(result: DeletePurchaseResponse) {
                        if (isOkResponse.invoke(result)) emitter.onComplete()
                        else emitter.onError(result.mapToException())
                    }
                })
        }
    }

    /**
     * Purchase consumption.
     *
     * @exception BillingResultException
     * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/Purchase-confirmation
     */
    override fun confirmPurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return Completable.create { emitter ->
            billingClient.purchases.confirmPurchase(purchaseId)
                .addOnCompleteListener(object : OnCompleteListener<ConfirmPurchaseResponse> {
                    override fun onFailure(throwable: Throwable) {
                        emitter.onError(throwable)
                    }

                    override fun onSuccess(result: ConfirmPurchaseResponse) {
                        if (isOkResponse.invoke(result)) emitter.onComplete()
                        else emitter.onError(result.mapToException())
                    }
                })
        }
    }

    /**
     * Buying a product.
     *
     * @exception BillingResultException
     * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/Buying-product
     */
    override fun purchaseProductAsCompletable(
        productId: String,
        isOkPaymentResult: IsOkPaymentResult
    ): Completable {
        return Completable.create { emitter ->
            billingClient.purchases.purchaseProduct(productId)
                .addOnCompleteListener(object : OnCompleteListener<PaymentResult> {
                    override fun onFailure(throwable: Throwable) {
                        emitter.onError(throwable)
                    }

                    override fun onSuccess(result: PaymentResult) {
                        if (isOkPaymentResult.invoke(result)) emitter.onComplete()
                        else emitter.onError(result.mapToException())
                    }
                })
        }
    }

    /**
     * Getting a user's shopping list.
     *
     * @exception BillingResultException
     * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/Getting-user-shopping-list
     */
    override fun getPurchasesAsSingle(isOkResponse: IsOkResponse): Single<List<Purchase>> {
        return Single.create { emitter ->
            billingClient.purchases.getPurchases()
                .addOnCompleteListener(object : OnCompleteListener<PurchasesResponse> {
                    override fun onFailure(throwable: Throwable) {
                        emitter.onError(throwable)
                    }

                    override fun onSuccess(result: PurchasesResponse) {
                        if (isOkResponse.invoke(result)) emitter.onSuccess(result.purchases.orEmpty())
                        else emitter.onError(result.mapToException())
                    }
                })
        }
    }

    /**
     * Getting up-to-date information on the list of products.
     *
     * @exception BillingResultException
     * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/Getting-information-list-products
     */
    override fun getProductsAsSingle(
        productIds: List<String>,
        isOkResponse: IsOkResponse
    ): Single<List<Product>> {
        return Single.create { emitter ->
            when (productIds.isEmpty()) {
                true -> emitter.onSuccess(emptyList())
                false -> billingClient.products.getProducts(productIds)
                    .addOnCompleteListener(object : OnCompleteListener<ProductsResponse> {
                        override fun onFailure(throwable: Throwable) {
                            emitter.onError(throwable)
                        }

                        override fun onSuccess(result: ProductsResponse) {
                            if (isOkResponse.invoke(result)) emitter.onSuccess(result.products.orEmpty())
                            else emitter.onError(result.mapToException())
                        }
                    })
            }
        }
    }

    interface GoogleBillingFactory {
        fun buildBillingClient(): RuStoreBillingClient
    }
}