package su.salut.billingexample.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.observers.EmptyCompletableObserver
import su.salut.billingexample.domain.models.BillingResult
import su.salut.billingexample.domain.models.Product
import su.salut.billingexample.domain.models.Purchase
import su.salut.billingexample.domain.models.PurchasesProduct
import su.salut.billingexample.domain.repository.BillingRepository
import java.util.concurrent.TimeUnit

class BillingInteractor(
    private val billingRepository: BillingRepository,
    private val debounceTime: Long = DEBOUNCE_EMPTY_LIST_PURCHASES_MADE_TIME
) {

    init {
        subscribePurchasesMadeByBilling()
    }

    private fun subscribePurchasesMadeByBilling() {
        billingRepository.getPurchasesMadeAsObservable()
            .map { purchases -> purchases.filter { it.isPurchased } }
            .debounce {
                // Empty values are sent with a delay,
                // if the array is not empty, there is no delay.
                when (it.isEmpty()) {
                    true -> Observable.timer(debounceTime, TimeUnit.MILLISECONDS)
                    false -> Observable.empty()
                }
            }
            .flatMapCompletable {
                // Reload purchase data
                billingRepository.updateActivePurchasesAsCompletable(shouldUseCache = true)
            }
            .subscribe(EmptyCompletableObserver())
    }

    fun getPurchasesProductsAsObservable(
        productIds: List<String>
    ): Observable<List<PurchasesProduct>> {
        val emptyPurchases = Single.fromCallable { emptyList<Purchase>() }

        return Observable.combineLatest(
            billingRepository.getProductsAsSingle(productIds).toObservable(),
            billingRepository.getPurchasesAsObservable().startWith(emptyPurchases),
            ::mapProductsToPurchasesProduct
        )
    }

    fun getPurchasesAsObservable(): Observable<List<Purchase>> {
        return billingRepository.getPurchasesAsObservable()
    }

    fun getResponseAsObservable(): Observable<BillingResult> {
        return billingRepository.getResponseAsObservable()
    }

    fun acknowledgePurchaseAsCompletable(purchase: Purchase): Completable {
        return billingRepository.acknowledgePurchaseAsCompletable(purchase)
    }

    fun purchaseProductAsCompletable(product: Product): Completable {
        return billingRepository.purchaseProductAsCompletable(product)
    }

    fun updateActivePurchasesAsCompletable(): Completable {
        return billingRepository.updateActivePurchasesAsCompletable(shouldUseCache = false)
    }

    private fun mapProductsToPurchasesProduct(
        products: List<Product>,
        purchases: List<Purchase>
    ): List<PurchasesProduct> {
        return products.map { product ->
            val purchasesProduct = purchases.filter { it.productIds.contains(product.productId) }
            val isPurchased = purchasesProduct.any { it.isPurchased }
            val isPending = !isPurchased && purchasesProduct.any { it.isPending }
            val isAcknowledged =
                purchasesProduct.isEmpty() || purchasesProduct.any { it.isAcknowledged }

            return@map PurchasesProduct(
                isPurchased = isPurchased,
                isPending = isPending,
                isAcknowledged = isAcknowledged,
                product = product,
                purchases = purchasesProduct
            )
        }
    }

    companion object {
        private const val DEBOUNCE_EMPTY_LIST_PURCHASES_MADE_TIME = 1_000L
    }
}