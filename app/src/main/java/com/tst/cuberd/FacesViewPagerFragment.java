package com.tst.cuberd;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tst.cuberd.util.CubeSolverUtil;

public class FacesViewPagerFragment extends Fragment implements View.OnClickListener {
    private Face mFace;
    private Context mContext;
    private TextView[] piecesHolder;
    private boolean pieceSelected = false;
    //Color selection
    private RelativeLayout faceLayout;
    private int currPieceIndex;
    private CardView colorPicker;
    private View activeColor;

    private final int PIECES_NUMBER = CubeSolverUtil.NUMBER_OF_PIECES;
    private final CubeSolverUtil cube = CubeSolverUtil.getInstance();
    private static final String KEY = "cube_face";
    private static final String TAG = "FacesViewPagerFragment";


    public static FacesViewPagerFragment newInstance(Face face) {
        FacesViewPagerFragment fragment = new FacesViewPagerFragment();
        if (face != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(KEY, face);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFace = getArguments().getParcelable(KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewpager_faces, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button pickWhiteBtn = view.findViewById(R.id.pick_white_btn);
        Button pickYellowBtn = view.findViewById(R.id.pick_yellow_btn);
        Button pickBlueBtn = view.findViewById(R.id.pick_blue_btn);
        Button pickGreenBtn = view.findViewById(R.id.pick_green_btn);
        Button pickOrangeBtn = view.findViewById(R.id.pick_orange_btn);
        Button pickRedBtn = view.findViewById(R.id.pick_red_btn);
        CardView faceAboveColor = view.findViewById(R.id.top_face);
        faceLayout = view.findViewById(R.id.cube_face);
        colorPicker = view.findViewById(R.id.color_picker);

        pickWhiteBtn.setOnClickListener(this);
        pickYellowBtn.setOnClickListener(this);
        pickBlueBtn.setOnClickListener(this);
        pickGreenBtn.setOnClickListener(this);
        pickOrangeBtn.setOnClickListener(this);
        pickRedBtn.setOnClickListener(this);
        faceLayout.setOnClickListener(this);

        initFace(view, faceAboveColor);
    }

    private void initFace(View view, CardView faceAboveColor) {
        if (mFace == null) {
            return;
        }
        currPieceIndex = -1;
        final int CENTER_PIECE = PIECES_NUMBER / 2;
        char[] faceToChar = {'U', 'R', 'F', 'D', 'L', 'B'};
        piecesHolder = new TextView[PIECES_NUMBER];
        for (int i = 0; i < PIECES_NUMBER; i++) {
            final TextView piece = view.findViewWithTag("p" + i);
            piecesHolder[i] = piece;
            final int currPos = i;
            if (i != CENTER_PIECE) {
                piece.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currPieceIndex != -1 && pieceSelected) {
                            //return previous piece to its original size
                            scaleViewAnimation(piecesHolder[currPieceIndex], true);
                        }
                        currPieceIndex = currPos;
                        showColorPicker();
                        scaleViewAnimation(view, false);
                        pieceSelected = true;
                    }
                });
            } else {
                piece.setBackgroundResource(mFace.getColor());
                cube.setPiece(cube.getPiecePosition(mFace.getPos(), i), faceToChar[mFace.getPos()]);
            }
        }
        int color = ContextCompat.getColor(mContext, mFace.getColorAbove());
        faceAboveColor.setCardBackgroundColor(color);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            default:
                if (colorPicker.getVisibility() == View.VISIBLE) {
                    hideColorPicker();
                }
                if (pieceSelected) {
                    scaleViewAnimation(piecesHolder[currPieceIndex], true);
                    pieceSelected = false;
                }
                break;
        }
    }

    private void setCurrPieceColor(View view, int drawableId) {
        TextView currPiece = piecesHolder[currPieceIndex];
        int i = cube.getPiecePosition(mFace.getPos(), currPieceIndex);
        currPiece.setBackgroundResource(drawableId);
        switch (drawableId) {
            case R.drawable.square_white:
                cube.setPiece(i, 'U');
                break;
            case R.drawable.square_yellow:
                cube.setPiece(i, 'D');
                break;
            case R.drawable.square_blue:
                cube.setPiece(i, 'B');
                break;
            case R.drawable.square_green:
                cube.setPiece(i, 'F');
                break;
            case R.drawable.square_red:
                cube.setPiece(i, 'R');
                break;
            case R.drawable.square_orange:
                cube.setPiece(i, 'L');
                break;
        }
        scaleViewAnimation(currPiece, true);
        hideColorPicker();
    }

    private void showColorPicker() {
        int verticalPosition;
        int rightMostPiece = piecesHolder[2].getId();
        int leftMostPiece = piecesHolder[0].getId();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (currPieceIndex == 0 || currPieceIndex == 1 || currPieceIndex == 2) {
            //Piece is in the top layer, so we place the color picker under it
            verticalPosition = RelativeLayout.BELOW;
        } else {
            verticalPosition = RelativeLayout.ABOVE;
        }

        if (currPieceIndex == 2 || currPieceIndex == 5 || currPieceIndex == 8) {
            //The current piece is on the right side
            layoutParams.addRule(RelativeLayout.END_OF, leftMostPiece);
        } else if (currPieceIndex == 1 || currPieceIndex == 7) {
            //piece is in the middle
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else {
            layoutParams.addRule(RelativeLayout.START_OF, rightMostPiece);
        }
        layoutParams.addRule(verticalPosition, piecesHolder[currPieceIndex].getId());
        //moving colorPicker to the new position
        faceLayout.removeView(colorPicker);
        faceLayout.addView(colorPicker, layoutParams);
        //animate
        TransitionManager.beginDelayedTransition(colorPicker, new AutoTransition());
        colorPicker.setVisibility(View.VISIBLE);
    }

    private void hideColorPicker() {
        TransitionManager.beginDelayedTransition(colorPicker, new AutoTransition());
        colorPicker.setVisibility(View.INVISIBLE);
    }

    /**
     * Scales any view by a fixed 15% of its original size.
     *
     * @param view    : the view to be scaled.
     * @param expand: restores original size if true else scales.
     */
    private void scaleViewAnimation(View view, boolean expand) {
        final float scaleFactor = 0.85f;
        if (expand)
            view.animate().scaleX(1f).scaleY(1f);
        else
            view.animate().scaleX(scaleFactor).scaleY(scaleFactor);
    }

    /**
     * Updates colors of this fragment instance to match the current cube state.
     * After a change on {@link CubeSolverUtil}
     */
    public void notifyCubeStateChanged() {
        int start = cube.getPiecePosition(mFace.getPos(), 0);
        int end = start + PIECES_NUMBER;
        int j = 0;
        String cubeState = cube.getCubeState();
        for (int i = start; i < end; i++) {
            TextView piece = piecesHolder[j++];
            char color = cubeState.charAt(i);
            switch (color) {
                case 'U':
                    piece.setBackgroundResource(R.drawable.square_white);
                    break;
                case 'R':
                    piece.setBackgroundResource(R.drawable.square_red);
                    break;
                case 'F':
                    piece.setBackgroundResource(R.drawable.square_green);
                    break;
                case 'D':
                    piece.setBackgroundResource(R.drawable.square_yellow);
                    break;
                case 'L':
                    piece.setBackgroundResource(R.drawable.square_orange);
                    break;
                case 'B':
                    piece.setBackgroundResource(R.drawable.square_blue);
                    break;
                default:
                    piece.setBackgroundResource(R.drawable.square_gray);
                    break;
            }
        }
    }
}
