package com.tst.cuberd.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tst.cuberd.R;
import com.tst.cuberd.min2phase.src.Search;
import com.tst.cuberd.min2phase.src.Tools;

import org.opencv.core.Mat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SolverFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    private static final String TAG = "SolverFragment";

    final String DEFAULT_STATE = "****U********R********F********D********L********B****";
    final String SOLVED_STATE = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";

    int currPieceId = R.id.preview_U0;               //Default pieceID is the first piece of the cube
    Drawable prevPieceBg;                            //Default PieceBg
    boolean colorChange = false;                     //Default state
    boolean firstU0 = true;                          //If first piece click is U0
    StringBuilder cubeState = new StringBuilder(DEFAULT_STATE);
    TextView solutionTxt;
    TextView[] piecesHolder = new TextView[54];     //to store the string representation of a piece given an index

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solver, container, false);
        FloatingActionButton btnCamera = view.findViewById(R.id.fab_camera);
        Button btnSolver = view.findViewById(R.id.btn_solver);
        Button pickWhiteBtn = view.findViewById(R.id.pick_white_btn);
        Button pickYellowBtn= view.findViewById(R.id.pick_yellow_btn);
        Button pickBlueBtn = view.findViewById(R.id.pick_blue_btn);
        Button pickGreenBtn = view.findViewById(R.id.pick_green_btn);
        Button pickOrangeBtn = view.findViewById(R.id.pick_orange_btn);
        Button pickRedBtn = view.findViewById(R.id.pick_red_btn);
        ImageView btnReset = view.findViewById(R.id.btn_reset);
        ImageView solveScramble = view.findViewById(R.id.solve_scramble_img);
        solutionTxt = view.findViewById(R.id.solution_txtview);

        btnCamera.setOnClickListener(this);
        btnSolver.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        pickWhiteBtn.setOnClickListener(this);
        pickYellowBtn.setOnClickListener(this);
        pickBlueBtn.setOnClickListener(this);
        pickGreenBtn.setOnClickListener(this);
        pickOrangeBtn.setOnClickListener(this);
        pickRedBtn.setOnClickListener(this);
        solveScramble.setOnClickListener(this);

        initPieces(view);
        Search.init();
        return view;
    }

    @Override
    public void onClick(View view) {
        int index = getIndex(currPieceId);       //searching for the object that contains the current id
        TextView currPiece = piecesHolder[index];

        switch (view.getId()){
            case R.id.fab_camera:
                break;
            case R.id.pick_white_btn:
                currPiece.setBackgroundResource(R.drawable.square_white);
                cubeState.setCharAt(index, 'U');
                colorChange = true;
                break;
            case R.id.pick_yellow_btn:
                currPiece.setBackgroundResource(R.drawable.square_yellow);
                cubeState.setCharAt(index, 'D');
                colorChange = true;
                break;
            case R.id.pick_blue_btn:
                currPiece.setBackgroundResource(R.drawable.square_blue);
                cubeState.setCharAt(index, 'B');
                colorChange = true;
                break;
            case R.id.pick_green_btn:
                currPiece.setBackgroundResource(R.drawable.square_green);
                cubeState.setCharAt(index, 'F');
                colorChange = true;
                break;
            case R.id.pick_orange_btn:
                currPiece.setBackgroundResource(R.drawable.square_orange);
                cubeState.setCharAt(index, 'L');
                colorChange = true;
                break;
            case R.id.pick_red_btn:
                currPiece.setBackgroundResource(R.drawable.square_red);
                cubeState.setCharAt(index, 'R');
                colorChange = true;
                break;
            case R.id.solve_scramble_img:
                showSolveScrambleDialog();
                break;
            case R.id.btn_reset:
                resetCube();
                break;
            case R.id.btn_solver:
                String state = cubeState.toString();
                    if(state.contains("*")){
                        Snackbar.make(view, "There are missing pieces", Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    String solution = solveCube(state);
                    if (solution.contains("Error")) {
                        //Error messages are enumerated like ERROR 1, ERROR 2,etc
                        //so we can determine the error  by checking the last char
                        char errorCode = solution.charAt(solution.length() - 1);
                        showError(errorCode, view);
                        solution = "Invalid input";
                    }else {
                        setCubeState(new StringBuilder(SOLVED_STATE));
                    }
                    solutionTxt.setText(solution);
                break;
        }
    }

    private void resetCube() {
        cubeState = new StringBuilder(DEFAULT_STATE);
        Integer[] indices = {4, 13, 22,31, 40, 49};
        Set<Integer> centerPiecesIndex= new HashSet<>(Arrays.asList(indices));

        for (int i = 0; i < piecesHolder.length; i++) {
            if (centerPiecesIndex.contains(i)){
                //Skip center pieces
                continue;
            }
            piecesHolder[i].setBackgroundResource(R.drawable.square_gray);
        }
        firstU0 = true;
    }

   /* private void resetCube(String face) { //Reset a face
        cubeState = new StringBuilder(DEFAULT_STATE);
        Integer[] indices = {4, 13, 22,31, 40, 49};
        Set<Integer> centerPiecesIndex= new HashSet<>(Arrays.asList(indices));
        int strt = 0;
        int end = piecesHolder.length;
        switch (face){
            case "U": strt = 0; end = 9; firstU0 = true; break;
            case "R": strt = 9; end = 18; break;
            case "F": strt = 18; end = 27; break;
            case "L": strt = 27; end = 36; break;
            case "D": strt = 36; end = 45; break;
            case "B": strt = 45; end = 54; break;
        }
        for (int i = strt; i < end; i++) {
            if (centerPiecesIndex.contains(i)){
                //Skip center pieces
                continue;
            }
            piecesHolder[i].setBackgroundResource(R.drawable.square_gray);
        }
    }*/

    private void showError(char errorCode, View view) {
        //convert char in int
        int idxError = (int)errorCode;
        int[] errorList = {
                R.string.error_code_1,
                R.string.error_code_2,
                R.string.error_code_3,
                R.string.error_code_4,
                R.string.error_code_5,
                R.string.error_code_6,
                R.string.error_code_7,
                R.string.error_code_8
        };
        // errorCode - 1 by index array
        Snackbar.make(view, errorList[idxError -1],Snackbar.LENGTH_LONG).show();
    }
    //initializes onCLickListener method for every piece of the cube
    private void initPieces(View view) {
        String[] piecesNames = {"U", "R", "F", "D", "L", "B"};

        int idx = 0;
        for (String pieceName : piecesNames) {
            for (int j = 0; j < 9; j++) {
                String name = "preview_" + pieceName + j;
                int id = getResources().getIdentifier(name, "id", mContext.getPackageName());
                piecesHolder[idx] = view.findViewById(id);
                if (j != 4) {             //Getting the id of the current piece onClick (excluding center pieces)
                    piecesHolder[idx].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int id = view.getId();

                            if(prevPieceBg == null) // because prevPieceBg is empty
                                prevPieceBg = view.getBackground();

                            if (currPieceId != id || firstU0) { // this validation (firstU0) is in case clicked del U0 in first time
                                firstU0 = false; // only if restart the cubestate is true again
                                if (!colorChange) // search the previous piece and recover background
                                {
                                    int index = getIndex(currPieceId);       //searching for the object that contains the current id
                                    TextView prevPiece = piecesHolder[index];
                                    prevPiece.setBackground(prevPieceBg);
                                }
                                colorChange = false;
                                prevPieceBg = view.getBackground();
                                currPieceId = id;
                                view.setBackgroundResource(R.drawable.square_select);

                            } else {
                                view.setBackground(prevPieceBg);
                                currPieceId = R.id.preview_U0; // reset id
                            }
                           //view.getId();
                            Log.d(TAG, "onClick: id " + currPieceId);

                        }
                    });
                } /*else{  // if clicked the centerpiece, reset this face
                    resetCube("U");}*/
                idx++;
            }
        }
    }
    private int getIndex(int id) {
        for (int i = 0; i < piecesHolder.length; i++) {
            if(piecesHolder[i].getId() == id){
                return i;
            }
        }
        return -1;
    }

    private String solveCube(String cubeState) {
        return  new Search().solution(cubeState, 21, 100000000, 10000, 0);
    }

    private void showSolveScrambleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_solve_scramble, null);

        final EditText scramble = dialogView.findViewById(R.id.solve_scramble_editText);

        builder.setView(dialogView)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Getting cube state from scramble on editText
                        String scrambleText = scramble.getText().toString();
                        cubeState = new StringBuilder(Tools.fromScramble(scrambleText));
                        setCubeState(cubeState);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //Sets the color of each facelet on the template to correspond with cubeState string
    private void setCubeState(StringBuilder cubeState) {
        this.cubeState = cubeState; //updating global variable
        // changing colors
        for (int j = 0; j < cubeState.length(); j++) {
            char color = cubeState.charAt(j);
            switch (color){
                case 'U': piecesHolder[j].setBackgroundResource(R.drawable.square_white); break;
                case 'R': piecesHolder[j].setBackgroundResource(R.drawable.square_red); break;
                case 'F': piecesHolder[j].setBackgroundResource(R.drawable.square_green); break;
                case 'L': piecesHolder[j].setBackgroundResource(R.drawable.square_orange); break;
                case 'D': piecesHolder[j].setBackgroundResource(R.drawable.square_yellow); break;
                case 'B': piecesHolder[j].setBackgroundResource(R.drawable.square_blue); break;
            }
        }
    }
}