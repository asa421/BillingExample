package su.salut.billingexample.application.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import su.salut.billingexample.application.data.extensions.*
import su.salut.billingexample.application.data.models.GoogleProduct
import su.salut.billingexample.application.data.models.GooglePurchase
import su.salut.billingexample.application.domain.models.BillingException
import su.salut.billingexample.application.domain.models.BillingResult
import su.salut.billingexample.application.domain.models.Product
import su.salut.billingexample.application.domain.models.Purchase
import su.salut.billingexample.application.repository.storage.BillingStorage
import su.salut.billingexample.lib.googleplay.BillingClientWrapper
import su.salut.billingexample.lib.googleplay.mapToException

/**
 * We work with the Google billing system.
 *
 * The peculiarity is that for billing you need to pass Activity.
 * We get around this by initializing the lambda function for billing.
 *
 * Before a request, it is better to initialize and check the availability of the billing system,
 * so all requests start with [BillingClientWrapper::startConnectionAsCompletable].
 * The Google billing system caches the initialization and the request is executed instantly.
 *
 * @date 03.02.2023
 * @author asa421
 * @link https://developer.android.com/google/play/billing/integrate
 * @link https://support.google.com/googleplay/android-developer/answer/9900533
 */
class GoogleBillingStorage(
    private val billingClientWrapper: BillingClientWrapper,
    private val onLaunchBillingFlow: OnLaunchBillingFlow,
    override val counterReloadTime: Long = COUNTER_RELOAD_TIME,
    override val totalCounterReloadTime: Long = TOTAL_COUNT_QUERY_TIME
) : RequestReload(), BillingStorage {

    override fun getProduct(productIds: List<String>): Single<List<Product>> {
        return billingClientWrapper.startConnectionAsCompletable()
            .andThen(billingClientWrapper.getSkuDetailsAsSingle(productIds))
            .map(::mapSkuDetailsToProducts)
            .wrapRequest()
    }

    override fun getBillingResultMade(): Observable<BillingResult> {
        return billingClientWrapper.getPurchasesUpdatedAsObservable()
            .flatMap {
                when (billingClientWrapper.isOkResponse(it.billingResult)) {
                    true -> mapPurchasesToAppPurchases(it.purchasesMade.orEmpty())
                        .map(BillingResult::PurchaseResult)
                        .toObservable()
                    false -> Observable.fromCallable {
                        it.billingResult
                            .mapToException() // Convert to ...googleplay.billingclient.BillingResultException
                            .mapGoogleToBillingException() // Convert to App BillingException!
                    }
                }
            }
    }

    override fun syncedAndGetActivePurchases(): Single<List<Purchase>> {
        return billingClientWrapper.startConnectionAsCompletable()
            .andThen(billingClientWrapper.resetCacheAsCompletable())
            .andThen(billingClientWrapper.getPurchasesAsSingle())
            .flatMap(this::mapPurchasesToAppPurchases)
            .wrapRequest()
    }

    override fun getActivePurchases(): Single<List<Purchase>> {
        return billingClientWrapper.startConnectionAsCompletable()
            .andThen(billingClientWrapper.getPurchasesAsSingle())
            .flatMap(this::mapPurchasesToAppPurchases)
            .wrapRequest()
    }

    override fun acknowledgePurchase(purchase: Purchase): Completable {
        return Single.fromCallable { purchase as GooglePurchase }
            .onErrorReturn { throw BillingException.getFeatureNotSupportedException() }
            .flatMapCompletable(::acknowledgePurchaseAsCompletable)
    }

    override fun purchaseProduct(product: Product): Completable {
        return Single.fromCallable { product as GoogleProduct }
            .onErrorReturn { throw BillingException.getFeatureNotSupportedException() }
            .flatMapCompletable(::launchBillingFlowAsCompletable)
    }

    private fun acknowledgePurchaseAsCompletable(purchase: GooglePurchase): Completable {
        return billingClientWrapper.startConnectionAsCompletable()
            .andThen(billingClientWrapper.acknowledgePurchaseAsCompletable(purchase.purchaseToken))
            .wrapRequest()
    }

    private fun launchBillingFlowAsCompletable(product: GoogleProduct): Completable {
        val launchBillingFlow = onLaunchBillingFlow.invoke(billingClientWrapper, product.skuDetails)
        return billingClientWrapper.startConnectionAsCompletable()
            .andThen(launchBillingFlow)
            .wrapRequest()
    }

    private fun mapPurchasesToAppPurchases(
        purchases: List<com.android.billingclient.api.Purchase>
    ): Single<List<Purchase>> {
        return billingClientWrapper.getSkuDetailsAsSingle(purchases.toSkusList())
            .map(purchases::mapToAppPurchases)
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

    companion object {
        private const val COUNTER_RELOAD_TIME = 4_500L
        private const val TOTAL_COUNT_QUERY_TIME = 15_000L
    }
}