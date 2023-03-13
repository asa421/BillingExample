package su.salut.billingexample.lib.googleplay

import android.app.Activity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/**
 * Let's convert the Google Play billing library through RxJava methods.
 *
 * @date 31.01.2023
 * @author asa421
 * @link https://developer.android.com/google/play/billing/integrate
 * @link https://support.google.com/googleplay/android-developer/answer/9900533
 */
class BillingClientWrapperImpl(private val factory: GoogleBillingFactory) : BillingClientWrapper {

    private val billingClient: BillingClient by lazy {
        return@lazy factory.buildBillingClient()
    }

    /** Purchase change listener. */
    override fun getPurchasesUpdatedAsObservable(): Observable<PurchasesUpdated> {
        return factory.getPurchasesUpdatedAsObservable()
    }

    /**
     * Starts the BillingClient installation process.
     *
     * @throws BillingResultException
     */
    override fun startConnectionAsCompletable(isOkResponse: IsOkResponse): Completable {
        return Completable.create { emitter ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (isOkResponse(billingResult)) emitter.onComplete()
                    else emitter.onError(billingResult.mapToException())
                }

                override fun onBillingServiceDisconnected() {
                    val disconnectedException = BillingResultException(
                        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                        "Billing disconnected"
                    )
                    emitter.onError(disconnectedException)
                }
            })
        }
    }

    /**
     * Confirm purchase
     *
     * @throws BillingResultException
     */
    override fun acknowledgePurchaseAsCompletable(
        purchaseToken: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return Completable.create { emitter ->
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()

            billingClient.acknowledgePurchase(params) { billingResult ->
                if (isOkResponse(billingResult)) emitter.onComplete()
                else emitter.onError(billingResult.mapToException())
            }
        }
    }

    /**
     * Returns the most recent purchase made by the user for each product,
     * even if that purchase is expired, canceled, or used.
     *
     * https://stackoverflow.com/questions/60948539/how-to-restore-purchases-on-a-second-device-inapp-and-subs/60991072#60991072
     *
     * @throws BillingResultException
     */
    override fun resetCacheAsCompletable(
        @BillingClient.SkuType type: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return Completable.create { emitter ->
            billingClient.queryPurchaseHistoryAsync(type) { billingResult, _ ->
                if (isOkResponse(billingResult)) emitter.onComplete()
                else emitter.onError(billingResult.mapToException())
            }
        }
    }

    /**
     * Returns purchase information for currently owned items purchased from your app.
     *
     * @throws BillingResultException
     */
    override fun getPurchasesAsSingle(
        @BillingClient.SkuType type: String,
        isOkResponse: IsOkResponse
    ): Single<List<Purchase>> {
        return Single.create { emitter ->
            billingClient.queryPurchasesAsync(type) { billingResult, list ->
                if (isOkResponse(billingResult)) emitter.onSuccess(list)
                else emitter.onError(billingResult.mapToException())
            }
        }
    }

    /**
     *
     * @throws BillingResultException
     */
    override fun getSkuDetailsAsSingle(
        @BillingClient.SkuType type: String,
        skuList: List<String>,
        isOkResponse: IsOkResponse
    ): Single<List<SkuDetails>> {
        return Single.create { emitter ->
            val params = SkuDetailsParams.newBuilder()
            params.setType(type)
            params.setSkusList(skuList)

            billingClient.querySkuDetailsAsync(params.build()) { billingResult, list ->
                if (isOkResponse(billingResult)) emitter.onSuccess(list.orEmpty())
                else emitter.onError(billingResult.mapToException())
            }
        }
    }

    /**
     * Initiates the billing flow for an in-app purchase or subscription.
     */
    override fun launchBillingFlowAsCompletable(
        activity: Activity,
        billingFlowParams: BillingFlowParams,
        isOkResponse: IsOkResponse
    ): Completable {
        return Completable.create { emitter ->
            val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

            if (isOkResponse(billingResult)) emitter.onComplete()
            else emitter.onError(billingResult.mapToException())
        }
    }

    interface GoogleBillingFactory {
        fun buildBillingClient(): BillingClient
        fun getPurchasesUpdatedAsObservable(): Observable<PurchasesUpdated>
    }
}