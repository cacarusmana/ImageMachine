package com.caca.imagemachine.ui.base;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author caca rusmana on 21/03/22
 */
public abstract class BaseViewModel extends ViewModel {

    protected final CompositeDisposable disposable = new CompositeDisposable();



    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }
}
