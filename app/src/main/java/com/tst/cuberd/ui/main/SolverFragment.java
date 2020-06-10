package com.tst.cuberd.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tst.cuberd.Face;
import com.tst.cuberd.FacesPageAdapter;
import com.tst.cuberd.FacesViewPagerFragment;
import com.tst.cuberd.R;
import com.tst.cuberd.min2phase.src.Search;
import com.tst.cuberd.min2phase.src.Tools;
import com.tst.cuberd.util.CubeSolverUtil;

import java.util.ArrayList;
import java.util.List;

public class SolverFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private TextView solutionTxt;

    private static final String TAG = "SolverFragment";
    //Color picker variables
    private ViewPager mFacesViewPager;
    private TabLayout mFacesTabLayout;
    private CubeSolverUtil cube;

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
        mFacesViewPager = view.findViewById(R.id.view_pager_faces);
        mFacesTabLayout = view.findViewById(R.id.tab_layout_faces);

        //Buttons
        Button btnSolver = view.findViewById(R.id.btn_solver);
        FloatingActionButton btnCamera = view.findViewById(R.id.fab_camera);
        //ImageViews
        ImageView btnReset = view.findViewById(R.id.btn_reset);
        ImageView solveScramble = view.findViewById(R.id.solve_scramble_img);
        //Layouts
        ConstraintLayout parentLayout = view.findViewById(R.id.parent_layout_solver);
        //OtherViews
        solutionTxt = view.findViewById(R.id.solution_txtview);
        btnCamera.setOnClickListener(this);
        btnSolver.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        solveScramble.setOnClickListener(this);
        parentLayout.setOnClickListener(this);
        cube = CubeSolverUtil.getInstance();
        Search.init();
        initFacesPagerAdapter();
    }

    private void initFacesPagerAdapter() {
        final int FACES_NUMBER = CubeSolverUtil.NUMBER_OF_FACES;
        final int[] faceToColor = {R.drawable.square_white, R.drawable.square_red,
                R.drawable.square_green, R.drawable.square_yellow,
                R.drawable.square_orange, R.drawable.square_blue};

        //contains the color that must be above each face
        final int[] colorAbove = new int[FACES_NUMBER];
        //top face of white = blue
        colorAbove[0] = R.color.blue;
        //top face of red, green, orange, blue = white
        colorAbove[1] = colorAbove[2] = colorAbove[4] = colorAbove[5] = R.color.white;
        //top face of yellow = green
        colorAbove[3] = R.color.green;

        List<Fragment> faces = new ArrayList<>();
        for (int j = 0; j < FACES_NUMBER; j++) {
            Face face = new Face(j, faceToColor[j], colorAbove[j]);
            FacesViewPagerFragment fragment = FacesViewPagerFragment.newInstance(face);
            faces.add(fragment);
        }
        FacesPageAdapter facesPageAdapter = new FacesPageAdapter(getChildFragmentManager(), faces);
        mFacesViewPager.setOffscreenPageLimit(FACES_NUMBER);       //prevents fragments from being destroyed
        mFacesViewPager.setAdapter(facesPageAdapter);
        mFacesTabLayout.setupWithViewPager(mFacesViewPager, true);
        mFacesTabLayout.setSelectedTabIndicator(null);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab_camera:
                break;

            case R.id.solve_scramble_img:
                showSolveScrambleDialog();
                break;
            case R.id.btn_reset:
                cube.clear();
                updateFaces();
                break;
            case R.id.btn_solver:
                final String SOLVED_STATE = "UUUUUUUUURRRRRRRRRFFFFFFFFFDDDDDDDDDLLLLLLLLLBBBBBBBBB";
                if (cube.isIncomplete()) {
                    Snackbar.make(view, "There are missing pieces", Snackbar.LENGTH_LONG).show();
                    break;
                }
                String solution = solveCube(cube.getCubeState());
                if (solution.contains("Error")) {
                    //Error messages are enumerated like ERROR 1, ERROR 2,etc
                    //so we can determine the error  by checking the last char
                    char errorCode = solution.charAt(solution.length() - 1);
                    showError(errorCode, view);
                    solution = "Invalid input";
                } else {
                    setCubeState(SOLVED_STATE);
                }
                Log.d(TAG, "onClick: " + solution);
                solutionTxt.setText(solution);
                break;
        }
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
                        String scrambleText = scramble.getText().toString().toUpperCase();
                        String cubeState = Tools.fromScramble(scrambleText);
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

    @SuppressWarnings("ConstantConditions")
    private void updateFaces() {
        for (int i = 0; i < CubeSolverUtil.NUMBER_OF_FACES; i++) {
            FacesViewPagerFragment face = (FacesViewPagerFragment) mFacesViewPager.getAdapter()
                    .instantiateItem(mFacesViewPager, i);
            face.notifyCubeStateChanged();
        }
    }

    private void setCubeState(String cubeState) {
        for (int j = 0; j < cubeState.length(); j++) {
            cube.setPiece(j, cubeState.charAt(j));
        }
        updateFaces();
    }
}