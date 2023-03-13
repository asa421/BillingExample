package su.salut.billingexample.lib.googleplay

import android.content.Context
import com.android.billingclient.api.BillingClient
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

class GoogleBillingFactoryImpl(
    private val context: Context
) : BillingClientWrapperImpl.GoogleBillingFactory {

    private val purchasesUpdatedBySubject = PublishSubject.create<PurchasesUpdated>()

    override fun buildBillingClient(): BillingClient {
        return BillingClient.newBuilder(context)
            .setListener { billingResult, purchasesMade ->
                val purchasesUpdated = PurchasesUpdated(
                    billingResult = billingResult,
                    purchasesMade = purchasesMade
                )
                purchasesUpdatedBySubject.onNext(purchasesUpdated)
            }
            .enablePendingPurchases()
            .build()
    }

    override fun getPurchasesUpdatedAsObservable(): Observable<PurchasesUpdated> {
        return purchasesUpdatedBySubject
    }
}