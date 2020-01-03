package com.tst.cuberd.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.SparseArray;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SolverFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    private static final String TAG = "SolverFragment";
    final String DEFAULT_STATE = "****U********R********F********D********L********B****";
    final String SOLVED_STATE = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";
    //Color picker variables
    int currPieceId;
    int currPieceIndex;
    TextView currPiece;
    SparseArray<TextView> piecesHolder = new SparseArray<>();     //container for all the pieces
    Drawable prevPieceBg;                                         //Default PieceBg
    boolean colorChange = false;                                  //Default state
    boolean firstU0 = true;                                       //If first piece click is U0
    boolean MultcolorSelected = false;                            //TODO implement multiple color selection
    ViewGroup colorPicker;
    View prevColor;

    StringBuilder cubeState = new StringBuilder(DEFAULT_STATE);
    TextView solutionTxt;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Buttons
        Button btnSolver = view.findViewById(R.id.btn_solver);
        Button pickWhiteBtn = view.findViewById(R.id.pick_white_btn);
        Button pickYellowBtn= view.findViewById(R.id.pick_yellow_btn);
        Button pickBlueBtn = view.findViewById(R.id.pick_blue_btn);
        Button pickGreenBtn = view.findViewById(R.id.pick_green_btn);
        Button pickOrangeBtn = view.findViewById(R.id.pick_orange_btn);
        Button pickRedBtn = view.findViewById(R.id.pick_red_btn);
        FloatingActionButton btnCamera = view.findViewById(R.id.fab_camera);
        //ImageViews
        ImageView btnReset = view.findViewById(R.id.btn_reset);
        ImageView solveScramble = view.findViewById(R.id.solve_scramble_img);
        //Layouts
        ConstraintLayout cubePreview = view.findViewById(R.id.cube_preview);
        ConstraintLayout parentLayout = view.findViewById(R.id.parent_layout_solver);
        //OtherViews
        solutionTxt = view.findViewById(R.id.solution_txtview);
        colorPicker = view.findViewById(R.id.color_picker);

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
        cubePreview.setOnClickListener(this);
        parentLayout.setOnClickListener(this);

        initPieces(view);
        Search.init();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fab_camera:
                break;
            case R.id.pick_white_btn:
                setCurrPieceColor(view, R.drawable.square_white);
                break;
            case R.id.pick_yellow_btn:
                setCurrPieceColor(view, R.drawable.square_yellow);
                break;
            case R.id.pick_blue_btn:
                setCurrPieceColor(view, R.drawable.square_blue);
                break;
            case R.id.pick_green_btn:
                setCurrPieceColor(view, R.drawable.square_green);
                break;
            case R.id.pick_orange_btn:
                setCurrPieceColor(view, R.drawable.square_orange);
                break;
            case R.id.pick_red_btn:
                setCurrPieceColor(view, R.drawable.square_red);
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
            default:
                if (colorPicker.getVisibility() == View.VISIBLE) {
                    hideColorPicker();
                }
                break;
        }
    }

    private void setCurrPieceColor(View view, int drawableId) {
        currPiece.setBackgroundResource(drawableId);
        switch (drawableId) {
            case R.drawable.square_white:
                cubeState.setCharAt(currPieceIndex, 'U');
                break;
            case R.drawable.square_yellow:
                cubeState.setCharAt(currPieceIndex, 'D');
                break;
            case R.drawable.square_blue:
                cubeState.setCharAt(currPieceIndex, 'B');
                break;
            case R.drawable.square_green:
                cubeState.setCharAt(currPieceIndex, 'F');
                break;
            case R.drawable.square_red:
                cubeState.setCharAt(currPieceIndex, 'R');
                break;
            case R.drawable.square_orange:
                cubeState.setCharAt(currPieceIndex, 'L');
                break;
        }
        scaleViewAnimation(view);
        colorChange = true;
        hideColorPicker();
    }

    /**
     * Scaling animation for color picker buttons. If a previous color had already been chosen
     * such button would be scaled up, and the current button would be scaled down.
     *
     * @param view : the color picker's button to animate.
     */
    private void scaleViewAnimation(View view) {
        final float scaleFactor = 0.75f;
        if (prevColor != null) {
            prevColor.animate().scaleX(1f).scaleY(1f); // returning to original size
            Log.d(TAG, "scaleViewAnimation: scaled Up");
        }
        view.animate().scaleX(scaleFactor).scaleY(scaleFactor);
        prevColor = view;
    }

    private void resetCube() {
        cubeState = new StringBuilder(DEFAULT_STATE);
        Integer[] indices = {4, 13, 22, 31, 40, 49};
        Set<Integer> centerPiecesIndex= new HashSet<>(Arrays.asList(indices));

        for (int i = 0; i < piecesHolder.size(); i++) {
            if (centerPiecesIndex.contains(i)){
                //Skip center pieces
                continue;
            }
            piecesHolder.valueAt(i).setBackgroundResource(R.drawable.square_gray);
        }
        firstU0 = true;
    }

    private void showError(char errorCode, View view) {
        int idxError = Character.getNumericValue(errorCode);
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
                final TextView piece = view.findViewById(id);
                final int position = idx;
                if (j != 4) {     // excluding center pieces
                    piece.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currPieceIndex = position;
                            int id = view.getId();
                            int[] location = new int[2]; // absolute location in screen (x,y)
                            if (prevPieceBg == null)     // because prevPieceBg is empty
                                prevPieceBg = view.getBackground();
                            if (prevColor != null) {     // reset to original size
                                prevColor.setScaleX(1f);
                                prevColor.setScaleY(1f);
                                prevColor = null;
                            }
                            pieceSelection(view, id, location);
                            currPiece = piece;

                            Log.d(TAG, "Screen pos_x: " + location[0] + " pos_y: " + location[1]);
                            Log.d(TAG, "ColorPicker pos_x: " + colorPicker.getX());
                            Log.d(TAG, "onClick: id " + currPieceIndex);
                        }
                    });
                }
                idx++;
                piecesHolder.put(id, piece);
            }
        }
    }

    private void showColorPicker() {
        TransitionManager.beginDelayedTransition(colorPicker, new AutoTransition());
        colorPicker.setVisibility(View.VISIBLE);
    }

    private void hideColorPicker() {
        TransitionManager.beginDelayedTransition(colorPicker, new AutoTransition());
        colorPicker.setVisibility(View.GONE);
    }

    private void pieceSelection(View view, int id, int[] location) {
        if (currPieceId != id || firstU0) { // this validation (firstU0) is in case clicked del U0 in first time
            firstU0 = false; // only if restart the cube state is "true" again
            colorRecovery(view, id);
            //Set position to color picker
            view.getLocationOnScreen(location);
            SetPosColorPicker(location,view);
            showColorPicker();
        } else {
            view.setBackground(prevPieceBg);
            currPieceId = R.id.preview_U0; // reset id
            hideColorPicker();
        }
    }

    private void colorRecovery(View view, int id) {
        if (!colorChange) // search the previous piece and recover background
        {
            TextView prevPiece = piecesHolder.get(id);
            prevPiece.setBackground(prevPieceBg);
        }
        // Pieces selection
        colorChange = false;
        prevPieceBg = view.getBackground(); // save bg
        currPieceId = id; // save id
        view.setBackgroundResource(R.drawable.square_select); // set selection background
    }

    private void SetPosColorPicker(int[] location, View view){
        int ColorPickerPos_x = location[0] - (colorPicker.getWidth()/2) + view.getWidth();

        int[] temp = new int[2];  // location on screen hor_limit
        piecesHolder.valueAt(47).getLocationOnScreen(temp); // 47 is blue right-top piece
        int HorizontalLimit = temp[0];

        if (ColorPickerPos_x <= 0 )
            colorPicker.setTranslationX(view.getX() + view.getWidth()/2);
        else if (ColorPickerPos_x < (HorizontalLimit - colorPicker.getWidth()))
            colorPicker.setTranslationX(ColorPickerPos_x); //60}
        else
            colorPicker.setTranslationX(HorizontalLimit + view.getWidth() - colorPicker.getWidth());

        colorPicker.setY( location[1] - view.getHeight() - colorPicker.getHeight()*2);
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
            int key = piecesHolder.keyAt(j);
            TextView piece = piecesHolder.valueAt(j);
            switch (color){
                case 'U':
                    piece.setBackgroundResource(R.drawable.square_white);
                    break;
                case 'R':
                    piece.setBackgroundResource(R.drawable.square_red);
                    break;
                case 'F':
                    piece.setBackgroundResource(R.drawable.square_green);
                    break;
                case 'L':
                    piece.setBackgroundResource(R.drawable.square_orange);
                    break;
                case 'D':
                    piece.setBackgroundResource(R.drawable.square_yellow);
                    break;
                case 'B':
                    piece.setBackgroundResource(R.drawable.square_blue);
                    break;
            }
            piecesHolder.put(key, piece);
        }
    }
}