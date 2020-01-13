package com.tst.cuberd;

public class SolveEntry {
    private int index;
    private long timeMillis;
    private String scramble, time;

    public SolveEntry(int index, String scramble, String time, long timeMillis) {
        this.index = index;
        this.scramble = scramble;
        this.time = time;
        this.timeMillis = timeMillis;
    }

    int getIndex() {
        return index;
    }

    String getScramble() {
        return scramble;
    }

    String getTime() {
        return time;
    }

    public long getTimeMillis() {
        return timeMillis;
    }
}
