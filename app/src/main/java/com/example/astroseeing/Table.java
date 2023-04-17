package com.example.astroseeing;

import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable {
    public ArrayList<ArrayList<String>> data;

    public ArrayList<ArrayList<String>> colors;

    public Table(){
        data = new ArrayList<ArrayList<String>>();
        colors = new ArrayList<ArrayList<String>>();
    }

    public Table(ArrayList<ArrayList<String>> table) {
        this.data = table;
    }
};
