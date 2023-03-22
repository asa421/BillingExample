package su.salut.billingexample.features.main.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.reactivex.rxjava3.internal.observers.EmptyCompletableObserver
import io.reactivex.rxjava3.subjects.BehaviorSubject
import su.salut.billingexample.App
import su.salut.billingexample.domain.BillingInteractor
import su.salut.billingexample.lib.viewmodel.BaseViewModel
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
class MainViewModel(private val billingInteractor: BillingInteractor): BaseViewModel() {

    private val updatePurchasesAsSubject = BehaviorSubject.create<Unit>()

    init {
        subscribeToUpdatePurchases()
    }

    /** Too frequent access does not require updating the data, we pause between requests! */
    private fun subscribeToUpdatePurchases() {
        updatePurchasesAsSubject
            .throttleFirst(TIME_TO_WAIT_BEFORE_EMITTING_ANOTHER_ITEM, TimeUnit.MILLISECONDS)
            .flatMapCompletable { billingInteractor.updateActivePurchasesAsCompletable() }
            .subscribeByDefault(EmptyCompletableObserver())
    }

    fun onUpdateActivePurchases() {
        updatePurchasesAsSubject.onNext(Unit)
    }

    companion object {
        private const val TIME_TO_WAIT_BEFORE_EMITTING_ANOTHER_ITEM = 5_000L

        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
                return@initializer MainViewModel(billingInteractor = app.factoryBillingInteractor())
            }
        }
    }
}