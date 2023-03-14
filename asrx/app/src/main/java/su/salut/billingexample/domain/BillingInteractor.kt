package su.salut.billingexample.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import su.salut.billingexample.domain.models.BillingResult
import su.salut.billingexample.domain.models.Product
import su.salut.billingexample.domain.models.Purchase
import su.salut.billingexample.domain.models.PurchasesProduct
import su.salut.billingexample.domain.repository.BillingRepository

class BillingInteractor(private val billingRepository: BillingRepository) {

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

    fun getResponseAsObservable(): Observable<BillingResult> {
        return billingRepository.getResponseAsObservable()
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

            return@map PurchasesProduct(
                isPurchased = isPurchased,
                isPending = isPending,
                product = product,
                purchases = purchasesProduct
            )
        }
    }
}