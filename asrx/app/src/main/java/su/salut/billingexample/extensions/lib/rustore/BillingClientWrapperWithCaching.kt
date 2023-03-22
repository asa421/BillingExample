package su.salut.billingexample.extensions.lib.rustore

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.rustore.sdk.billingclient.model.common.ResponseWithCode
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import java.util.*

/**
 * Let's convert the RuStore billing library through RxJava methods.
 *
 * Some of the results do not change over time,
 * and therefore it is not worth making extra requests to the server.
 *
 * @date 02.03.2023
 * @author asa421
 */
class BillingClientWrapperWithCaching(
    private val billingClientWrapper: BillingClientWrapper,
    private val lifetimeMillis: Long
) : BillingClientWrapper {

    private var purchasesCache: PurchasesCache = PurchasesCache.Empty
    private val productsCache = mutableMapOf<String, Product>()

    /**
     * Returns the most recent purchase made by the user for each product,
     * even if that purchase is expired, canceled, or used.
     */
    fun resetCacheAsCompletable(): Completable {
        return Completable.fromAction { purchasesCache = PurchasesCache.Empty }
    }

    /** Check the answer that there are no errors. */
    override fun isOkResponse(responseWithCode: ResponseWithCode): Boolean {
        return billingClientWrapper.isOkResponse(responseWithCode)
    }

    /** Check the answer that there are no errors. */
    override fun isOkPaymentResult(paymentResult: PaymentResult): Boolean {
        return billingClientWrapper.isOkPaymentResult(paymentResult)
    }

    /**
     * Checking the availability of working with payments.
     *
     * @exception RuStoreException(message: String)
     */
    @Suppress("KDocUnresolvedReference")
    override fun checkPurchasesAvailabilityAsCompletable(): Completable {
        return billingClientWrapper.checkPurchasesAvailabilityAsCompletable()
    }

    /**
     * Cancellation of purchase.
     *
     * @exception BillingResultException
     */
    override fun deletePurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return billingClientWrapper.deletePurchaseAsCompletable(purchaseId, isOkResponse)
            .doOnComplete { purchasesCache.remove(purchaseId) }
    }

    /**
     * Purchase consumption.
     *
     * @exception BillingResultException
     */
    override fun confirmPurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return billingClientWrapper.confirmPurchaseAsCompletable(purchaseId, isOkResponse)
            .doOnComplete { purchasesCache = PurchasesCache.Empty }
    }

    /**
     * Buying a product.
     *
     * @exception BillingResultException
     */
    override fun purchaseProductAsCompletable(
        productId: String,
        isOkPaymentResult: IsOkPaymentResult
    ): Completable {
        return billingClientWrapper.purchaseProductAsCompletable(productId, isOkPaymentResult)
            // We reset the purchase cache on any result or error.
            .doOnComplete { purchasesCache = PurchasesCache.Empty }
            .doOnError { purchasesCache = PurchasesCache.Empty }
    }

    /**
     * Getting a user's shopping list.
     *
     * @exception BillingResultException
     */
    override fun getPurchasesAsSingle(isOkResponse: IsOkResponse): Single<List<Purchase>> {
        return Single.fromCallable { purchasesCache }
            .flatMap {
                when (it.isActive()) {
                    true -> Single.just(it.getPurchases())
                    false -> billingClientWrapper.getPurchasesAsSingle(isOkResponse)
                        .doAfterSuccess { listPurchases ->
                            purchasesCache = PurchasesCache.CacheWithLifetime(
                                lifetimeMillis = lifetimeMillis,
                                purchases = listPurchases
                            )
                        }
                }
            }
    }

    /**
     * Getting up-to-date information on the list of products.
     *
     * @exception BillingResultException
     */
    override fun getProductsAsSingle(
        productIds: List<String>,
        isOkResponse: IsOkResponse
    ): Single<List<Product>> {
        return Single.fromCallable { getProducts(productIds) }
            .onErrorResumeNext {
                when (it) {
                    is IllegalArgumentException -> {
                        billingClientWrapper.getProductsAsSingle(productIds, isOkResponse)
                            .doAfterSuccess(::setProducts)
                    }
                    else -> Single.error(it)
                }
            }
    }

    /** @exception  IllegalArgumentException */
    private fun getProducts(productIds: List<String>): List<Product> {
        return productIds.map(productsCache::get).requireNoNulls()
    }

    private fun setProducts(products: List<Product>) {
        products.forEach(::set)
    }

    private fun set(product: Product) {
        productsCache[product.productId] = product
    }

    /** Save and cache purchases. */
    private sealed interface PurchasesCache {
        fun isActive(): Boolean
        fun getPurchases(): List<Purchase>
        fun remove(purchaseId: String)

        object Empty : PurchasesCache {
            override fun isActive(): Boolean = false
            override fun getPurchases(): List<Purchase> = emptyList()
            override fun remove(purchaseId: String) {} // Empty
        }

        class CacheWithLifetime(
            private val lifetimeMillis: Long,
            purchases: List<Purchase>
        ) : PurchasesCache {
            private val timeOfCreationMillis: Long = System.currentTimeMillis()
            private val purchases: MutableList<Purchase> = purchases.toMutableList()

            override fun isActive(): Boolean {
                return lifetimeMillis > (System.currentTimeMillis() - timeOfCreationMillis)
            }

            override fun getPurchases(): List<Purchase> = purchases.toList()

            override fun remove(purchaseId: String) {
                purchases.removeIf { it.purchaseId == purchaseId }
            }
        }
    }
}