package com.tst.cuberd;

public class Face {
    private int faceId;
    private int faceColor;

    public Face(int faceId, int faceColor) {
        this.faceId = faceId;
        this.faceColor = faceColor;
    }

    public int getFaceId() {
        return faceId;
    }

    public int getFaceColor() {
        return faceColor;
    }
}
