package com.tst.cuberd.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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



public class SolverFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    int currPieceId = R.id.preview_U0;               //Default pieceID is the first piece of the cube
    private static final String TAG = "SolverFragment";
    StringBuilder cubeState = new StringBuilder("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB");
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
        ImageView solveScramble = view.findViewById(R.id.solve_scramble_img);
        solutionTxt = view.findViewById(R.id.solution_txtview);

        btnCamera.setOnClickListener(this);
        btnSolver.setOnClickListener(this);
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
                break;
            case R.id.pick_yellow_btn:
                currPiece.setBackgroundResource(R.drawable.square_yellow);
                cubeState.setCharAt(index, 'D');
                break;
            case R.id.pick_blue_btn:
                currPiece.setBackgroundResource(R.drawable.square_blue);
                cubeState.setCharAt(index, 'B');
                break;
            case R.id.pick_green_btn:
                currPiece.setBackgroundResource(R.drawable.square_green);
                cubeState.setCharAt(index, 'F');
                break;
            case R.id.pick_orange_btn:
                currPiece.setBackgroundResource(R.drawable.square_orange);
                cubeState.setCharAt(index, 'L');
                break;
            case R.id.pick_red_btn:
                currPiece.setBackgroundResource(R.drawable.square_red);
                cubeState.setCharAt(index, 'R');
                break;
            case R.id.solve_scramble_img:
                showSolveScrambleDialog();
                break;

            case R.id.btn_solver:
                String state = cubeState.toString();
                    String solution = solveCube(state);
                    if (solution.contains("Error")) {
                        //Error messages are enumerated like ERROR 1, ERROR 2,etc
                        //so we can determine the error  by checking the last char
                        char errorCode = solution.charAt(solution.length() - 1);
                        showError(errorCode, view);
                        solution = "Invalid input";
                    }else {
                        //TODO check if the user has finished the template before setting the cube to a solved state
                        setCubeState(new StringBuilder("UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB"));
                    }
                    solutionTxt.setText(solution);
                break;
        }
    }

    private void showError(char errorCode, View view) {
        //convert char in int
        int idxError = (int)errorCode;
        String[] errorList = {
                "Invalid Cube: There are not exactly nine facelets of each color!", // 1
                "Invalid Cube: Not all 12 edges exist exactly once!",
                "Flip error: One edge has to be flipped!",
                "Invalid Cube: Not all 8 corners exist exactly once!",
                "Twist error: One corner has to be twisted!",
                "Parity error: Two corners or two edges have to be exchanged!",
                "No solution exists for the given maximum move number!",
                "Timeout, no solution found within given maximum time!" // 8
        };
        // errorCode - 1 by index array
        Snackbar.make(view, errorList[idxError -1],Snackbar.LENGTH_LONG).show();
    }

    //initializes onCLickListener method for every piece of the cube
    private void initPieces(View view) {
        String[] piecesNames = {"U", "R", "F", "D", "L", "B"};

        int idx = 0;
        boolean isSorted = false;
        int prev = 0;
        for (String pieceName : piecesNames) {
            for (int j = 0; j < 9; j++) {
                String name = "preview_" + pieceName + j;
                int id = getResources().getIdentifier(name, "id", mContext.getPackageName());
                piecesHolder[idx] = view.findViewById(id);
                if (j != 4) {             //Getting the id of the current piece onClick (excluding center pieces)
                    piecesHolder[idx].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currPieceId = view.getId();
                            Log.d(TAG, "onClick: id " + currPieceId);
                        }
                    });
                }
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