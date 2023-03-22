@file:Suppress("UNCHECKED_CAST")

package su.salut.billingexample.lib.rustore.fortesting

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import su.salut.billingexample.extensions.lib.rustore.BillingClientWrapper
import su.salut.billingexample.extensions.lib.rustore.IsOkPaymentResult
import su.salut.billingexample.extensions.lib.rustore.IsOkResponse

class BillingClientWrapperImplForTesting: BillingClientWrapper {

    private var returnPurchasesIterator: Iterator<Any> = emptyList<Any>().iterator()
    private var returnProductsIterator: Iterator<Any> = emptyList<Any>().iterator()
    private var returnCompletable: Completable = Completable.complete()

    fun reset(
        returnPurchases: List<Any>? = null,
        returnProducts: List<Any>? = null,
        returnCompletable: Completable? = null
    ) {
        returnPurchases?.let {
            this.returnPurchasesIterator = returnPurchases.iterator()
        }

        returnProducts?.let {
            this.returnProductsIterator = returnProducts.iterator()
        }

        returnCompletable?.let {
            this.returnCompletable = returnCompletable
        }
    }

    override fun checkPurchasesAvailabilityAsCompletable(): Completable {
        return returnCompletable
    }

    override fun deletePurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return returnCompletable
    }

    override fun confirmPurchaseAsCompletable(
        purchaseId: String,
        isOkResponse: IsOkResponse
    ): Completable {
        return returnCompletable
    }

    override fun purchaseProductAsCompletable(
        productId: String,
        isOkPaymentResult: IsOkPaymentResult
    ): Completable {
        return returnCompletable
    }

    override fun getPurchasesAsSingle(isOkResponse: IsOkResponse): Single<List<Purchase>> {
        val nextValue = when (returnPurchasesIterator.hasNext()) {
            true -> returnPurchasesIterator.next()
            false -> null
        }

        return when (nextValue) {
            is Throwable -> Single.error(nextValue)
            null -> Single.error(ArrayIndexOutOfBoundsException("Empty array!"))
            else -> Single.just(nextValue as List<Purchase>)
        }
    }

    override fun getProductsAsSingle(
        productIds: List<String>,
        isOkResponse: IsOkResponse
    ): Single<List<Product>> {
        val nextValue = when (returnProductsIterator.hasNext()) {
            true -> returnProductsIterator.next()
            false -> null
        }

        return when (nextValue) {
            is Throwable -> Single.error(nextValue)
            null -> Single.error(ArrayIndexOutOfBoundsException("Empty array!"))
            else -> Single.just(nextValue as List<Product>)
        }
    }
}