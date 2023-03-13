package su.salut.billingexample.lib.googleplay

import android.app.Activity
import com.android.billingclient.api.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/**
 * Let's convert the Google Play billing library through RxJava methods.
 *
 * @date 31.01.2023
 * @author asa421
 */
interface BillingClientWrapper {

    /** Check the answer that there are no errors. */
    fun isOkResponse(billingResult: BillingResult): Boolean {
        return billingResult.responseCode == BillingClient.BillingResponseCode.OK
    }

    /** Purchase change listener. */
    fun getPurchasesUpdatedAsObservable(): Observable<PurchasesUpdated>

    /**
     * Starts the BillingClient installation process.
     *
     * @throws BillingResultException
     */
    fun startConnectionAsCompletable(isOkResponse: IsOkResponse = ::isOkResponse): Completable

    /**
     * Confirm purchase
     *
     * @throws BillingResultException
     */
    fun acknowledgePurchaseAsCompletable(
        purchaseToken: String,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Completable

    /**
     * Returns the most recent purchase made by the user for each product,
     * even if that purchase is expired, canceled, or used.
     *
     * @throws BillingResultException
     */
    fun resetCacheAsCompletable(
        @BillingClient.SkuType type: String,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Completable

    /**
     * Initiates the billing flow for an in-app purchase or subscription.
     */
    fun launchBillingFlowAsCompletable(
        activity: Activity,
        billingFlowParams: BillingFlowParams,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Completable

    /**
     * Returns purchase information for currently owned items purchased from your app.
     *
     * @throws BillingResultException
     */
    fun getPurchasesAsSingle(
        @BillingClient.SkuType type: String,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Single<List<Purchase>>

    /**
     * @throws BillingResultException
     */
    fun getSkuDetailsAsSingle(
        @BillingClient.SkuType type: String,
        skuList: List<String>,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Single<List<SkuDetails>>

    /**
     * Returns the all purchase information for currently owned items purchased from your app.
     *
     * @throws BillingResultException
     */
    fun getPurchasesAsSingle(
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Single<List<Purchase>> {
        return Single.zip(
            getPurchasesAsSingle(BillingClient.SkuType.SUBS, isOkResponse),
            getPurchasesAsSingle(BillingClient.SkuType.INAPP, isOkResponse)
        ) { purchaseINAPP: List<Purchase>, purchaseSUBS: List<Purchase> ->
            purchaseINAPP + purchaseSUBS
        }
    }

    /**
     * Returns the most recent purchase made by the user for each product,
     * even if that purchase is expired, canceled, or used.
     *
     * @throws BillingResultException
     */
    fun resetCacheAsCompletable(isOkResponse: IsOkResponse = ::isOkResponse): Completable {
        return resetCacheAsCompletable(BillingClient.SkuType.SUBS)
            .mergeWith(resetCacheAsCompletable(BillingClient.SkuType.INAPP))
    }

    /**
     * @throws BillingResultException
     */
    fun getSkuDetailsAsSingle(
        skuList: List<String>,
        isOkResponse: IsOkResponse = ::isOkResponse
    ): Single<List<SkuDetails>> {
        return Single.zip(
            getSkuDetailsAsSingle(BillingClient.SkuType.SUBS, skuList, isOkResponse),
            getSkuDetailsAsSingle(BillingClient.SkuType.INAPP, skuList, isOkResponse)
        ) { skuDetailsINAPP: List<SkuDetails>, skuDetailsSUBS: List<SkuDetails> ->
            skuDetailsINAPP + skuDetailsSUBS
        }
    }

    /**
     * Initiates the billing flow for an in-app purchase or subscription.
     */
    fun launchBillingFlowAsCompletable(
        activity: Activity,
        skuDetails: SkuDetails
    ): Completable {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        return launchBillingFlowAsCompletable(activity, billingFlowParams)
    }
}