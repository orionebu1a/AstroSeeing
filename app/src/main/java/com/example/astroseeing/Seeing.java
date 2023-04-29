package com.example.astroseeing;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Seeing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Seeing extends Fragment {
    private Button updateButton;

    private Getting parser;
    private Table table;

    public Seeing() {
        // Required empty public constructor
    }

    public static Seeing newInstance() {
        Seeing fragment = new Seeing();
        return fragment;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            table = (Table)getArguments().getSerializable("table");
        };
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyViewModel model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        this.parser = new Getting(model);
        View view = inflater.inflate(R.layout.fragment_seeing, container, false);
        updateButton = view.findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyViewModel model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
                EditText place = getActivity().findViewById(R.id.place_input);
                model.setPlace(place.getText().toString());
                model.setPlaces(place.getText().toString());
            }
        });

        model.getSelected().observe(getViewLifecycleOwner(), item -> {
            setTable(item);
            TableLayout tableLayout = getActivity().findViewById(R.id.tableLayout1);
            tableLayout.removeAllViews();
            if(tableLayout.getChildCount() > 2){
                for(int i = 2; i < tableLayout.getChildCount(); i++)
                {
                    tableLayout.removeViewAt(i);
                }
            }
            for(int j = 0; j < 10; j++) {
                TableRow row = new TableRow(getContext());
                TextView column = new TextView(getContext());
                String number = String.valueOf(j);
                column.setText(number);
                row.addView(column);
                for (int i = 0; i < 12; i++) {
                    int color1;
                    try {
                        column = new TextView(getContext());
                        column.setText(table.data.get(j).get(i));
                        String color = table.colors.get(j).get(i);
                        color1 = Color.parseColor(color);
                    } catch (Exception e) {
                        color1 = 0;
                    }
                    column.setBackgroundColor(color1);
                    row.addView(column);
                }
                tableLayout.addView(row);
            }
        });

        return view;
    }
}