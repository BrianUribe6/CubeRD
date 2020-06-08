package com.tst.cuberd.util;

/**
 * Singleton utility class that manages the current state of the cube across
 * all activities and fragments
 */
public class CubeSolverUtil {
    public static final int NUMBER_OF_PIECES = 9;
    public static final int NUMBER_OF_FACES = 6;
    private static final CubeSolverUtil ourInstance = new CubeSolverUtil();
    private char[] cubeState = new char[NUMBER_OF_PIECES * NUMBER_OF_FACES];

    public static CubeSolverUtil getInstance() {
        return ourInstance;
    }

    private CubeSolverUtil() {
    }

    public String getCubeState() {
        return new String(cubeState);
    }

    public void setPiece(int i, char value) {
        cubeState[i] = value;
    }

    /**
     * Converts the index (0 - 8) of a piece within a cube's face to its correct position in the
     * cubeState string.
     *
     * @param faceId     : Index (0 - 5) of the face that contains the piece
     * @param pieceIndex : Index (0 - 8) of the piece.
     * @return : index of the given piece with respect to {@link CubeSolverUtil#cubeState}
     */
    public int getPiecePosition(int faceId, int pieceIndex) {
        return faceId * NUMBER_OF_PIECES + pieceIndex;
    }

    public boolean isIncomplete() {
        for (char c : cubeState) {
            if (c == 0)
                return true;
        }
        return false;
    }

    /**
     * Empties out every piece of the cube excluding center pieces
     */
    public void clear() {
        int center = NUMBER_OF_PIECES / 2;              // first center piece index
        for (int i = 0; i < cubeState.length; i++) {
            if (i != center)
                cubeState[i] = 0;
            else
                center += NUMBER_OF_PIECES;             // move center to the next face
        }
    }
}
