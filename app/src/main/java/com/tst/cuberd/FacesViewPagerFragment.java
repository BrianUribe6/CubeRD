package com.tst.cuberd;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private int faceId;
    private int faceColor;
    private boolean initialized = false;
    private TextView[] piecesHolder;
    private Context mContext;
    //Color selection
    private RelativeLayout faceLayout;
    private int currPieceIndex;
    private CardView colorPicker;
    private View activeColor;

    final int PIECES_NUMBER = 9;
    final CubeSolverUtil cube = CubeSolverUtil.getInstance();
    final static String ID_KEY = "face_id";
    final static String COLOR_KEY = "face_color";
    private static final String TAG = "FacesViewPagerFragment";


    public static FacesViewPagerFragment newInstance(Face face) {
        FacesViewPagerFragment fragment = new FacesViewPagerFragment();
        if (face != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(ID_KEY, face.getFaceId());
            bundle.putInt(COLOR_KEY, face.getFaceColor());
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
            faceId = getArguments().getInt(ID_KEY);
            faceColor = getArguments().getInt(COLOR_KEY);
            initialized = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewpager_faces, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (initialized) {
            initFace(view);
            Button pickWhiteBtn = view.findViewById(R.id.pick_white_btn);
            Button pickYellowBtn = view.findViewById(R.id.pick_yellow_btn);
            Button pickBlueBtn = view.findViewById(R.id.pick_blue_btn);
            Button pickGreenBtn = view.findViewById(R.id.pick_green_btn);
            Button pickOrangeBtn = view.findViewById(R.id.pick_orange_btn);
            Button pickRedBtn = view.findViewById(R.id.pick_red_btn);
            faceLayout = view.findViewById(R.id.cube_face);
            colorPicker = view.findViewById(R.id.color_picker);

            pickWhiteBtn.setOnClickListener(this);
            pickYellowBtn.setOnClickListener(this);
            pickBlueBtn.setOnClickListener(this);
            pickGreenBtn.setOnClickListener(this);
            pickOrangeBtn.setOnClickListener(this);
            pickRedBtn.setOnClickListener(this);
            faceLayout.setOnClickListener(this);
        }
    }

    /**
     * Initializes onClick for each of the current face pieces according to
     *
     * @param view: Current view associated with the fragment's instance. (onViewCreated)
     */
    private void initFace(View view) {
        final int CENTER_PIECE = PIECES_NUMBER / 2;
        char[] faceToChar = {'U', 'R', 'F', 'D', 'L', 'B'};
        piecesHolder = new TextView[PIECES_NUMBER];
        for (int i = 0; i < PIECES_NUMBER; i++) {
            TextView piece = view.findViewWithTag("p" + i);
            piecesHolder[i] = piece;
            final int currPos = i;
            if (i != CENTER_PIECE) {
                piece.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currPieceIndex = currPos;
                        showColorPicker();
                    }
                });
            } else {
                piece.setBackgroundResource(faceColor);
                cube.setPiece(cube.getPiecePosition(faceId, i), faceToChar[faceId]);
            }
        }
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
                hideColorPicker();
                break;
        }
    }

    private void setCurrPieceColor(View view, int drawableId) {
        TextView currPiece = piecesHolder[currPieceIndex];
        int i = cube.getPiecePosition(faceId, currPieceIndex);
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
        scaleViewAnimation(view);
        hideColorPicker();
    }

    private void showColorPicker() {
        int rightMostPiece = piecesHolder[2].getId();
        int leftMostPiece = piecesHolder[0].getId();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.ABOVE, piecesHolder[currPieceIndex].getId());
        if (currPieceIndex == 2 || currPieceIndex == 5 || currPieceIndex == 8) {
            //The current piece is on the right side
            layoutParams.addRule(RelativeLayout.END_OF, leftMostPiece);
        } else if (currPieceIndex == 1 || currPieceIndex == 7) {
            //piece is in the middle
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else {
            layoutParams.addRule(RelativeLayout.START_OF, rightMostPiece);
        }
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
     * Scaling animation for color picker buttons. If there is an active color
     * such button would be scaled up, and the current button would be scaled down.
     *
     * @param view : the color picker's button to animate.
     */
    private void scaleViewAnimation(View view) {
        final float scaleFactor = 0.75f;
        if (activeColor != null) {
            activeColor.animate().scaleX(1f).scaleY(1f); // returning to original size
            Log.d(TAG, "scaleViewAnimation: scaled Up");
        }
        view.animate().scaleX(scaleFactor).scaleY(scaleFactor);
        activeColor = view;
    }

    /**
     * Updates colors of this fragment instance to match the current cube state.
     * After a change on {@link CubeSolverUtil}
     */
    public void notifyCubeStateChanged() {
        int start = cube.getPiecePosition(faceId, 0);
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
