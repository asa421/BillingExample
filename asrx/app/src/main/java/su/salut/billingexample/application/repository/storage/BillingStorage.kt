package su.salut.billingexample.application.repository.storage

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import su.salut.billingexample.application.domain.models.BillingResult
import su.salut.billingexample.application.domain.models.Product
import su.salut.billingexample.application.domain.models.Purchase

interface BillingStorage {
    fun getProduct(productIds: List<String>): Single<List<Product>>
    fun getBillingResultMade(): Observable<BillingResult>
    fun syncedAndGetActivePurchases(): Single<List<Purchase>>
    fun getActivePurchases(): Single<List<Purchase>>
    fun acknowledgePurchase(purchase: Purchase): Completable
    fun purchaseProduct(product: Product): Completable
}