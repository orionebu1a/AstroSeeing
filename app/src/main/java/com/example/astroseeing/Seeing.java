package com.example.astroseeing;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
        View view = inflater.inflate(R.layout.fragment_seeing, container, false);
        updateButton = view.findViewById(R.id.update_button);

        MutableLiveData<String> location = model.getLocation();
        location.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String value) {
                TextView text = view.findViewById(R.id.textView2);
                String loc = "location: " + model.getLocation().getValue();
                text.setText(loc);
            }
        });

        TextView text = view.findViewById(R.id.textView2);
        String loc = "location: " + model.getLocation().getValue();
        text.setText(loc);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButton.setText(getResources().getString(R.string.loading_text));
                updateButton.setBackgroundColor(getResources().getColor(R.color.loadingColor));
                MyViewModel model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
                parser = new Getting(model);
                parser.execute();
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
            for(int j = 0; j < table.data.size(); j++) {
                TableRow row = new TableRow(getContext());
                TextView column = new TextView(getContext());
                //String number = String.valueOf(j);
                //column.setText(number);
                //row.addView(column);
                for(int i = 0; i < table.data.get(j).size(); i++) {
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
                updateButton.setBackgroundColor(getResources().getColor(R.color.down_choser));
                updateButton.setText(getResources().getString(R.string.update_data));
            }
        });

        return view;
    }
}