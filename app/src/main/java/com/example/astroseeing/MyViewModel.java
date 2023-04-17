package com.example.astroseeing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.security.auth.callback.Callback;

public class MyViewModel extends ViewModel {
    private final MutableLiveData<Table> selected = new MutableLiveData<Table>();
    private final MutableLiveData<String> place = new MutableLiveData<String>();
    public void select(Table item) {
//        selected.setValue(item);
        selected.postValue(item);
    }

    public void setPlace(String item){
        this.place.postValue(item);
    }

    public MutableLiveData<String> getPlace() {
        return place;
    }

    public MutableLiveData<Table> getSelected() {
        return selected;
    }
}
