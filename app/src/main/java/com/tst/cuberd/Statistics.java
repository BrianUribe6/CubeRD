package com.tst.cuberd;


import com.tst.cuberd.ui.main.TimerFragment;

import java.security.InvalidParameterException;
import java.util.List;

public class Statistics {
    private static final String TAG = "Statistics";
    private List<SolveEntry> mData;
    private long mean, currAo12, currAo5, currAo3;
    private long best, bestAo12, bestAo5;
    private int size;

    public Statistics(List<SolveEntry> mData) {
        this.mData = mData;
        size = mData.size();
        if (size > 0) {
            mean = calcMean();
            currAo3 = calcCurrAo3();
            currAo5 = calcCurrAo5();
            currAo12 = calcCurrAo12();
            best = findBest();
            bestAo12 = findBestAo(12);
            bestAo5 = findBestAo(5);
        }
    }

    private long calcMean() {
        long total = sum(0, size);
        return Math.round(total / (double) size);
    }

    private long calcCurrAo12() {
        long total = sum(size - 12, size);
        return Math.round(total / 12.0);
    }

    private long calcCurrAo3() {
        long total = sum(size - 3, size);
        return Math.round(total / 3.0);
    }

    private long calcCurrAo5() {
        long total = sum(size - 5, size);
        return Math.round(total / 5.0);
    }

    private long findBest() {
        long min = Long.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            long time = mData.get(i).getTimeMillis();
            min = time < min ? time : min;
        }
        return min;
    }

    /**
     * finds the best average of x consecutive solves.
     *
     * @param x : the number of solves
     * @return -1 if x is negative or equal to 0
     */
    private long findBestAo(int x) {
        if (x <= 0)
            throw new InvalidParameterException("x must be a positive integer");

        long minSum = Long.MAX_VALUE;
        for (int i = 0; i < size - (size % x); i += x) {
            long currSum = sum(i, i + x);
            minSum = currSum < minSum ? currSum : minSum;
        }
        if (minSum == Long.MAX_VALUE) {       // not enough solves to compute average
            return 0;
        }
        return Math.round(minSum / (double) x);
    }

    /**
     * Sums the time in milliseconds from solve(i) to solve (j).
     *
     * @param start: beginning of the sum
     * @param end:   last element of the sum(inclusive)'
     * @return 0 if end is greater than mData.size()
     **/
    private long sum(int start, int end) {
        long total = 0;
        if (end > mData.size() || start > end || start < 0)
            return 0;
        for (int i = start; i < end; i++) {
            total += mData.get(i).getTimeMillis();
        }
        return total;
    }

    public String getBestAo5() {
        return TimerFragment.formatTime(bestAo5);
    }

    public String getBestAo12() {
        return TimerFragment.formatTime(bestAo12);
    }

    public String getCurrAo3() {
        return TimerFragment.formatTime(currAo3);
    }

    public String getCurrAo5() {
        return TimerFragment.formatTime(currAo5);
    }

    public String getCurrAo12() {
        return TimerFragment.formatTime(currAo12);
    }

    public String getBest() {
        return TimerFragment.formatTime(best);
    }

    public String getMean() {
        return TimerFragment.formatTime(mean);
    }
}
