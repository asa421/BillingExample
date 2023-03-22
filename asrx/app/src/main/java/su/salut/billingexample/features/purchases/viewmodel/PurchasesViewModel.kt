package su.salut.billingexample.features.purchases.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.observers.EmptyCompletableObserver
import su.salut.billingexample.application.App
import su.salut.billingexample.application.domain.BillingInteractor
import su.salut.billingexample.application.domain.models.Purchase
import su.salut.billingexample.extensions.android.viewmodel.BaseViewModel
import su.salut.billingexample.features.purchases.view.adapter.PurchaseItemAdapterBinder.PurchaseItemDiffListUpdates

class PurchasesViewModel(
    private val billingInteractor: BillingInteractor
) : BaseViewModel() {

    private val _purchases = MutableLiveData<PurchaseItemDiffListUpdates>()
    val purchases: LiveData<PurchaseItemDiffListUpdates> = _purchases

    init {
        subscribeToPurchasesAsObservable()
    }

    private fun subscribeToPurchasesAsObservable() {
        val observer = object : Observer<PurchaseItemDiffListUpdates> {
            override fun onSubscribe(d: Disposable) { } // Empty

            override fun onError(e: Throwable) { } // Empty

            override fun onComplete() { } // Empty

            override fun onNext(t: PurchaseItemDiffListUpdates) {
                _purchases.value = t
            }
        }

        billingInteractor.getPurchasesAsObservable()
            .map(::PurchaseItemDiffListUpdates)
            .subscribeByDefault(observer)
    }

    fun onAcknowledge(purchase: Purchase) {
        billingInteractor.acknowledgePurchaseAsCompletable(purchase)
            .subscribeByDefault(EmptyCompletableObserver())
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)

                return@initializer PurchasesViewModel(
                    billingInteractor = app.factoryBillingInteractor()
                )
            }
        }
    }
}