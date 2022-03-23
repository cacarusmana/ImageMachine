package com.caca.imagemachine.ui.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * @author caca rusmana on 21/03/22
 */
public abstract class BaseViewModel extends ViewModel {

    protected final CompositeDisposable disposable = new CompositeDisposable();

    public MutableLiveData<Boolean> errorState = new MutableLiveData<>();


    protected void setError(Throwable throwable) {
        throwable.printStackTrace();
        errorState.postValue(true);
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }
}
