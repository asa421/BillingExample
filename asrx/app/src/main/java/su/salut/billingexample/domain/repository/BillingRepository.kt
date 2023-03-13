package su.salut.billingexample.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import su.salut.billingexample.domain.models.BillingResult
import su.salut.billingexample.domain.models.Product
import su.salut.billingexample.domain.models.Purchase

interface BillingRepository {

    fun getProductsAsSingle(productIds: List<String>): Single<List<Product>>

    fun getPurchasesMadeAsObservable(): Observable<List<Purchase>>

    fun getPurchasesAsObservable(): Observable<List<Purchase>>

    fun getResponseAsObservable(): Observable<BillingResult>

    fun purchaseProductAsCompletable(product: Product): Completable

    fun acknowledgePurchaseAsCompletable(purchase: Purchase): Completable

    fun updateActivePurchasesAsCompletable(shouldUseCache: Boolean): Completable
}