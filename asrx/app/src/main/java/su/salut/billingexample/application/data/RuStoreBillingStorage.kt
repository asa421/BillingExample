package su.salut.billingexample.application.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import su.salut.billingexample.application.data.extensions.mapProductsToAppProducts
import su.salut.billingexample.application.data.extensions.mapRuStoreToBillingException
import su.salut.billingexample.application.data.extensions.mapToAppPurchases
import su.salut.billingexample.application.data.extensions.toProductIds
import su.salut.billingexample.application.data.models.RuStoreProduct
import su.salut.billingexample.application.data.models.RuStorePurchase
import su.salut.billingexample.application.domain.models.BillingException
import su.salut.billingexample.application.domain.models.BillingResult
import su.salut.billingexample.application.domain.models.Product
import su.salut.billingexample.application.domain.models.Purchase
import su.salut.billingexample.application.repository.storage.BillingStorage
import su.salut.billingexample.lib.rustore.BillingClientWrapper
import su.salut.billingexample.lib.rustore.BillingClientWrapperWithCaching
import su.salut.billingexample.lib.rustore.BillingClientWrapperWithPurchaseListener

/**
 * We work with the RuStore billing system.
 *
 * The keystore signature must match the signature that was used to sign the application
 * published in the RuStore Console system. Make sure the buildType being used (ex. debug)
 * uses the same signature as the published application (ex. release).
 *
 * Unfortunately, the system does not support query caching, which leads to errors.
 * To solve the problem, we first use the [BillingClientWrapperWithCaching] wrapper.
 * And then we use the [BillingClientWrapperWithPurchaseListener] wrapper to track purchases,
 * because there is no standard listener either.
 *
 * The feature, besides the lack of caching and the purchase listener,
 * is the creation of a purchase even when simply invoiced. And if the user canceled the purchase,
 * you need to delete she, otherwise she hangs pending. To do this,
 * the purchase is marked as requiring confirmation and at the right time,
 * she is either deleted or confirmed.
 *
 * Also, for a successful return to the application,
 * you need to return the intent to the billing system!
 *
 * @date 03.03.2023
 * @author asa421
 * @link https://help.rustore.ru/rustore/for_developers/developer-documentation/sdk_payments/SDK-connecting-payments/quick_start
 */
class RuStoreBillingStorage(
    billingClientWrapper: BillingClientWrapper,
    cacheLifetime: Long = CACHE_LIFETIME,
    override val counterReloadTime: Long = COUNTER_RELOAD_TIME,
    override val totalCounterReloadTime: Long = TOTAL_COUNT_QUERY_TIME
) : RequestReload(), BillingStorage {

    /**
     * The RuStore system unfortunately does not cache requests,
     * and frequent requests lead to errors. We eliminate errors of frequent requests
     * by caching responses from the server.
     */
    private val cachingWrapper = BillingClientWrapperWithCaching(
        billingClientWrapper = billingClientWrapper,
        lifetimeMillis = cacheLifetime
    )

    /**
     * The RuStore system does not allow you to separately track changes in purchases,
     * the billing client wrapper will help us, which implements a listener for purchase changes.
     */
    private val billingClientWrapper = BillingClientWrapperWithPurchaseListener(
        billingClientWrapper = cachingWrapper
    )

    override fun getProduct(productIds: List<String>): Single<List<Product>> {
        return billingClientWrapper.checkPurchasesAvailabilityAsCompletable()
            .onErrorResumeNext { Completable.error(it.mapRuStoreToBillingException()) }
            .andThen(billingClientWrapper.getProductsAsSingle(productIds).wrapRequest())
            .map(::mapProductsToAppProducts)
    }

    override fun getBillingResultMade(): Observable<BillingResult> {
        return billingClientWrapper.getPurchasesUpdatedAsObservable()
            .flatMap { mapPurchasesToAppPurchases(it).toObservable() }
            .map(BillingResult::PurchaseResult)
    }

    override fun syncedAndGetActivePurchases(): Single<List<Purchase>> {
        return cachingWrapper.resetCacheAsCompletable().andThen(getActivePurchases())
    }

    override fun getActivePurchases(): Single<List<Purchase>> {
        return billingClientWrapper.getPurchasesAsSingle()
            .flatMap(::mapPurchasesToAppPurchases)
            .wrapRequest()
    }

    override fun acknowledgePurchase(purchase: Purchase): Completable {
        return Single.fromCallable { purchase as RuStorePurchase }
            .onErrorReturn { throw BillingException.getFeatureNotSupportedException() }
            .flatMapCompletable(::confirmPurchaseAsCompletable)
    }

    override fun purchaseProduct(product: Product): Completable {
        return Single.fromCallable { product as RuStoreProduct }
            .onErrorReturn { throw BillingException.getFeatureNotSupportedException() }
            .flatMapCompletable(::purchaseProductAsCompletable)
    }

    private fun confirmPurchaseAsCompletable(purchase: RuStorePurchase): Completable {
        return when (purchase.isCancelled) {
            true -> billingClientWrapper.deletePurchaseAsCompletable(purchase.purchaseToken)
            false -> billingClientWrapper.confirmPurchaseAsCompletable(purchase.purchaseToken)
        }.wrapRequest()
    }

    private fun purchaseProductAsCompletable(product: RuStoreProduct): Completable {
        return billingClientWrapper.purchaseProductAsCompletable(product.productId)
            .onErrorResumeNext { Completable.error(it.mapRuStoreToBillingException()) }
            .subscribeOn(Schedulers.io())
    }

    private fun <T : Any> Single<T>.wrapRequest(): Single<T> {
        return this.requestReload()
            .onErrorResumeNext { Single.error(it.mapRuStoreToBillingException()) }
            .subscribeOn(Schedulers.io())
    }

    private fun Completable.wrapRequest(): Completable {
        return this.requestReload()
            .onErrorResumeNext { Completable.error(it.mapRuStoreToBillingException()) }
            .subscribeOn(Schedulers.io())
    }

    private fun mapPurchasesToAppPurchases(
        purchases: List<ru.rustore.sdk.billingclient.model.purchase.Purchase>
    ): Single<List<Purchase>> {
        return billingClientWrapper.getProductsAsSingle(purchases.toProductIds())
            .map(purchases::mapToAppPurchases)
    }

    companion object {
        private const val COUNTER_RELOAD_TIME = 4_500L
        private const val TOTAL_COUNT_QUERY_TIME = 15_000L
        private const val CACHE_LIFETIME = 120_000L
    }
}