package com.example.astroseeing;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.security.auth.callback.Callback;

public class MyViewModel extends ViewModel {
    private SQLiteDatabase db;

    private final MutableLiveData<Integer> day = new MutableLiveData<Integer>();
    private final MutableLiveData<String> location = new MutableLiveData<String>();
    private final MutableLiveData<Table> selected = new MutableLiveData<Table>();
    private final MutableLiveData<String> place = new MutableLiveData<String>();

    private final MutableLiveData<Places> places = new MutableLiveData<Places>();
    public void select(Table item) {
//        selected.setValue(item);
        selected.postValue(item);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public void setPlace(String item){
        this.place.postValue(item);
    }

    public MutableLiveData<String> getPlace() {
        return place;
    }

    public MutableLiveData<Places> getPlaces() {
        return places;
    }

    public void setPlaces(String item){
        Places newPlaces  = new Places(this.getPlaces().getValue());
        newPlaces.getPlaceUrls().add(item);
        this.places.postValue(newPlaces);
        this.db.execSQL("INSERT OR IGNORE INTO places VALUES ('item', NULL)");
        Cursor buf = this.db.rawQuery("SELECT * FROM places;", null);
        while(buf.moveToNext()) {
            System.out.println(buf.getString(0));
        }
    }

    public MutableLiveData<Integer> getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day.postValue(day);
    }

    public MutableLiveData<Table> getSelected() {
        return selected;
    }

    public MutableLiveData<String> getLocation() {
        return location;
    }

    public void setLocation(String location){
        this.location.postValue(location);
    }
}
