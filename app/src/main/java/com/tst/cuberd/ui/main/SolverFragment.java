package com.tst.cuberd.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tst.cuberd.R;


public class SolverFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SolverFragment";
    int colorChanger;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solver, container, false);
        FloatingActionButton btnCamera = view.findViewById(R.id.fab_camera);
        Button pickWhiteBtn = view.findViewById(R.id.pick_white_btn);
        Button pickYellowBtn= view.findViewById(R.id.pick_yellow_btn);
        Button pickBlueBtn = view.findViewById(R.id.pick_blue_btn);
        Button pickGreenBtn = view.findViewById(R.id.pick_green_btn);
        Button pickOrangeBtn = view.findViewById(R.id.pick_orange_btn);
        Button pickRedBtn = view.findViewById(R.id.pick_red_btn);

        btnCamera.setOnClickListener(this);
        pickWhiteBtn.setOnClickListener(this);
        pickYellowBtn.setOnClickListener(this);
        pickBlueBtn.setOnClickListener(this);
        pickGreenBtn.setOnClickListener(this);
        pickOrangeBtn.setOnClickListener(this);
        pickRedBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_camera:
                break;
            case R.id.pick_white_btn:
                break;
            case R.id.pick_yellow_btn:
                break;
            case R.id.pick_blue_btn:
                break;
            case R.id.pick_green_btn:
                break;
            case R.id.pick_orange_btn:
                break;
            case R.id.pick_red_btn:
                break;
        }
    }
}