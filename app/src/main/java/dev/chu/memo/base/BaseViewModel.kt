package dev.chu.memo.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel: ViewModel() {
    protected val disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.dispose()
        disposable.clear()
        super.onCleared()
    }
}