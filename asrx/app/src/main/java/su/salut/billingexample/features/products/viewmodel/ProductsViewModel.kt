package su.salut.billingexample.features.products.viewmodel

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
import su.salut.billingexample.features.products.view.adapter.ProductItemAdapterBinder.ProductItemDiffListUpdates

class ProductsViewModel(
    private val billingInteractor: BillingInteractor,
    getProductIdsUseCase: GetProductIdsUseCase
) : BaseViewModel() {

    private val productIds = getProductIdsUseCase.execute()
    private val _products = MutableLiveData<ProductItemDiffListUpdates>()

    val products: LiveData<ProductItemDiffListUpdates> = _products

    init {
        subscribeToProductsAsObservable()
    }

    private fun subscribeToProductsAsObservable() {
        val observer = object : Observer<ProductItemDiffListUpdates> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {

            }

            override fun onNext(t: ProductItemDiffListUpdates) {
                _products.value = t
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