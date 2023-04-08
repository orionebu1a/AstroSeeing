package com.example.astroseeing;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Seeing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Seeing extends Fragment {
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
//        this.setTable(model.getSelected().getValue());
        model.getSelected().observe(getViewLifecycleOwner(), item -> {
            setTable(item);
            TableLayout tableLayout = getActivity().findViewById(R.id.tableLayout1);
            if(tableLayout.getChildCount() > 2){
                for(int i = 2; i < tableLayout.getChildCount(); i++)
                {
                    tableLayout.removeViewAt(i);
                }
            }

            TableRow row = new TableRow(getContext());
            TextView column = new TextView(getContext());
            column.setText("Now");
            row.addView(column);

            for(int i = 0; i < 12; i++){
                column = new TextView(getContext());
                column.setText(table.data.get(0).get(i));
                row.addView(column);
            }
            tableLayout.addView(row);
        });
        return inflater.inflate(R.layout.fragment_seeing, container, false);
    }
}