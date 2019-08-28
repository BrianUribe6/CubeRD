package com.tst.cuberd.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.tst.cuberd.HistoryActivity;
import com.tst.cuberd.HistoryDB;
import com.tst.cuberd.R;
import com.tst.cuberd.SolveEntry;
import com.tst.cuberd.min2phase.src.Search;
import com.tst.cuberd.min2phase.src.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TimerFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TimerFragment";
    private Context mContext;

    private TextView scrambleText;
    private TextView[] piecesHolder = new TextView[54];
    private ImageView startStop;
    private Chronometer timer;

    private boolean timerRunning = false;
    private String randomCube;
    private List<SolveEntry> mData;
    private HistoryDB historyDB;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Reloading DB with the changes made on the History Activity Eg. solve deleted or list cleared
        mData = historyDB.loadData(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        scrambleText = view.findViewById(R.id.txt_scramble);
        timer = view.findViewById(R.id.chronometer);
        startStop = view.findViewById(R.id.start_stop);
        FloatingActionButton historyFab = view.findViewById(R.id.history_fab);

        startStop.setOnClickListener(this);
        historyFab.setOnClickListener(this);

        //Loading History Data
        historyDB = new HistoryDB();
        mData = historyDB.loadData(mContext);
        //Initializing scramble
        String scramble = getScramble();
        scrambleText.setText(scramble);
        //Initializing preview
        initPieces(view);
        setPreviewState(randomCube);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start_stop:
                if(!timerRunning) {
                    //TODO make this its own method
                    timerRunning = true;
                    timer.setBase(SystemClock.elapsedRealtime());
//                timer.setFormat("MM:SS");
                    timer.start();
                    startStop.setImageResource(R.drawable.stop_button);

                }else{
                    //TODO make this its own method
                    timerRunning = false;
                    timer.stop();
                    startStop.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    //Get new scramble and preview
                    String scramble = getScramble();
                    scrambleText.setText(scramble);
                    setPreviewState(randomCube);
                    //TODO Recompute statistics
                    //Save Data
                    //TODO format index with leading zeroes and scramble shortened with ellipsis
                    mData.add(new SolveEntry(mData.size(), scramble, timer.getText().toString()));
                    historyDB.saveData(mContext, mData);
                }
                break;
            case R.id.history_fab:
                Intent intent = new Intent(mContext, HistoryActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setPreviewState(String cubeState) {
        Map<Character, Integer> colors = new HashMap<>();
        colors.put('U', R.color.white);
        colors.put('R', R.color.red);
        colors.put('F', R.color.green);
        colors.put('D', R.color.yellow);
        colors.put('L', R.color.orange);
        colors.put('B', R.color.blue);

        for (int i = 0; i < piecesHolder.length; i++) {
            char piece = cubeState.charAt(i);
            piecesHolder[i].setBackgroundResource(colors.get(piece));
        }
    }

    private void initPieces(View view) {
        String[] piecesNames = {"U", "R", "F", "D", "L", "B"};

        int idx = 0;
        for (String pieceName : piecesNames) {
            for (int j = 0; j < 9; j++) {
                String name = "timer_preview_" + pieceName + j;
                int id = getResources().getIdentifier(name, "id", mContext.getPackageName());
                piecesHolder[idx] = view.findViewById(id);
                idx++;
            }
        }
    }

    private String getScramble(){
        //1. Getting random cube state
        randomCube = Tools.randomCube();
        int noOfMoves = new Random().nextInt(5) + 21;

        //2. Solving Random Cube
        String solvedCube = new Search().solution(randomCube, noOfMoves, 100000000, 0, 0);

        //3. Reversing solved cube
        String[] solvedCubeMoves = solvedCube.split(" ");
        StringBuilder scramble = new StringBuilder();
        for (int i = solvedCubeMoves.length - 1; i >=0 ;i--) {
            String currMove = solvedCubeMoves[i];
            switch (currMove){
                case "U" : scramble.append("U' "); break;
                case "U'" : scramble.append("U "); break;
                case "R" : scramble.append("R' "); break;
                case "R'" : scramble.append("R "); break;
                case "F" : scramble.append("F' "); break;
                case "F'" : scramble.append("F "); break;
                case "D" : scramble.append("D' "); break;
                case "D'" : scramble.append("D "); break;
                case "L" : scramble.append("L' "); break;
                case "L'" : scramble.append("L "); break;
                case "B" : scramble.append("B' "); break;
                case "B'" : scramble.append("B "); break;
                default:        //Double moves
                    String temp = currMove + " ";
                    scramble.append(temp); break;
            }
        }
        return scramble.toString();
    }
}
