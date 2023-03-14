package su.salut.billingexample.lib.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun <T: Any> Observable<T>.subscribeByDefault(observer: Observer<T>) {
        this.observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(compositeDisposable::add)
            .subscribe(observer)
    }

    fun Completable.subscribeByDefault(observer: CompletableObserver) {
        this.observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(compositeDisposable::add)
            .subscribe(observer)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}