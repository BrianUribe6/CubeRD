package com.tst.cuberd;

public class SolveEntry {
    private int index;
    private String scramble, time;

    public SolveEntry(int index, String scramble, String time) {
        this.index = index;
        this.scramble = scramble;
        this.time = time;
    }

    public int getIndex() {
        return index;
    }
    public String getScramble() {
        return scramble;
    }

    public String getTime() {
        return time;
    }

}
