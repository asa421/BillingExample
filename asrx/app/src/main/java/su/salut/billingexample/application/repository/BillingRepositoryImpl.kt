package su.salut.billingexample.application.repository

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import su.salut.billingexample.application.repository.storage.BillingStorage
import su.salut.billingexample.application.domain.models.BillingResult
import su.salut.billingexample.application.domain.models.Product
import su.salut.billingexample.application.domain.models.Purchase
import su.salut.billingexample.application.domain.repository.BillingRepository

class BillingRepositoryImpl(private val storage: BillingStorage): BillingRepository {

    private val purchasesListBySubject = BehaviorSubject.create<List<Purchase>>()
    private val responseBySubject = PublishSubject.create<BillingResult>()

    override fun getProductsAsSingle(productIds: List<String>): Single<List<Product>> {
        return storage.getProduct(productIds).doOnError(::sendResponse)
    }

    override fun getPurchasesMadeAsObservable(): Observable<List<Purchase>> {
        return storage.getBillingResultMade()
            .doAfterNext(responseBySubject::onNext)
            .filter { it is BillingResult.PurchaseResult }
            .map { (it as BillingResult.PurchaseResult).purchases  }
    }

    override fun getPurchasesAsObservable(): Observable<List<Purchase>> {
        return purchasesListBySubject.observeOn(AndroidSchedulers.mainThread())
    }

    override fun getResponseAsObservable(): Observable<BillingResult> {
        return responseBySubject.observeOn(AndroidSchedulers.mainThread())
    }

    override fun purchaseProductAsCompletable(product: Product): Completable {
        return storage.purchaseProduct(product).doOnError(::sendResponse)
    }

    override fun acknowledgePurchaseAsCompletable(purchase: Purchase): Completable {
        return storage.acknowledgePurchase(purchase).doOnError(::sendResponse)
    }

    override fun updateActivePurchasesAsCompletable(shouldUseCache: Boolean): Completable {
        val query = when (shouldUseCache) {
            true -> storage.getActivePurchases()
            false -> storage.syncedAndGetActivePurchases()
        }

        return Completable.fromSingle(query.doAfterSuccess(purchasesListBySubject::onNext))
            .doOnError(::sendResponse)
    }

    private fun sendResponse(t: Throwable) {
        if (t is BillingResult) responseBySubject.onNext(t)
    }
}