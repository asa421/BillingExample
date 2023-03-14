package su.salut.billingexample.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.observers.EmptyCompletableObserver
import su.salut.billingexample.App
import su.salut.billingexample.domain.BillingInteractor
import su.salut.billingexample.domain.models.PurchasesProduct
import su.salut.billingexample.domain.usecase.GetProductIdsUseCase
import su.salut.billingexample.lib.viewmodel.BaseViewModel
import su.salut.billingexample.ui.products.ProductItemAdapterBinder.ProductItemDiffListUpdates

class ProductsViewModel(
    private val billingInteractor: BillingInteractor,
    getProductIdsUseCase: GetProductIdsUseCase
) : BaseViewModel() {

    private val productIds = getProductIdsUseCase.execute()
    private val _purchasesProduct = MutableLiveData<ProductItemDiffListUpdates>()

    val purchasesProduct: LiveData<ProductItemDiffListUpdates> = _purchasesProduct

    init {
        downloadProducts()
    }

    private fun downloadProducts() {
        val observer = object : Observer<ProductItemDiffListUpdates> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {

            }

            override fun onNext(t: ProductItemDiffListUpdates) {
                _purchasesProduct.value = t
            }
        }

        billingInteractor.getPurchasesProductsAsObservable(productIds = productIds)
            .map(::ProductItemDiffListUpdates)
            .subscribeByDefault(observer)
    }

    fun onSubscription(purchasesProduct: PurchasesProduct) {
        billingInteractor.purchaseProductAsCompletable(purchasesProduct.product)
            .subscribeByDefault(EmptyCompletableObserver())
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)

                return@initializer ProductsViewModel(
                    billingInteractor = app.factoryBillingInteractor(),
                    getProductIdsUseCase = app.factoryGetProductIdsUseCase()
                )
            }
        }
    }
}