package com.example.astroseeing;

import java.util.HashSet;
import java.util.Set;

public class Places {
    private Set<String> placeUrls = new HashSet<String>();

    public Set<String> getPlaceUrls() {
        return placeUrls;
    }

    public void setPlaceUrls(Set<String> placeUrls) {
        this.placeUrls = placeUrls;
    }

    public Places(Set<String> placeUrls) {
        this.placeUrls = placeUrls;
    }

    public Places(Places oldPlaces){
        if(oldPlaces != null) {
            this.placeUrls = new HashSet<String>(oldPlaces.placeUrls);
        }
        else{
            this.placeUrls = new HashSet<String>();
        }
    }
}
