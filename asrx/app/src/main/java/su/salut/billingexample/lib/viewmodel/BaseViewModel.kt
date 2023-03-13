package su.salut.billingexample.lib.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun Completable.subscribeByDefault(observer: CompletableObserver) {
        this.observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe(observer)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}