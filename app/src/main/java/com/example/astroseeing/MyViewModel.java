package com.example.astroseeing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.security.auth.callback.Callback;

public class MyViewModel extends ViewModel {
    private final MutableLiveData<Table> selected = new MutableLiveData<Table>();

    public void select(Table item) {
//        selected.setValue(item);
        selected.postValue(item);
    }

    public LiveData<Table> getSelected() {
        return selected;
    }
}
