package com.example.astroseeing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    private boolean show_help = false;

    public Home() {
        // Required empty public constructor
    }
    public static Home newInstance() {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyViewModel model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton helpButton = view.findViewById(R.id.help);
        TextView help = view.findViewById(R.id.helper);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(!show_help){
                    help.setText(R.string.help_str);
                    show_help = true;
                    help.setVisibility(View.VISIBLE);
                }
                else{
                    help.setText("");
                    show_help = false;
                    help.setVisibility(View.GONE);
                }
            }
        });
        return view;
    }
}