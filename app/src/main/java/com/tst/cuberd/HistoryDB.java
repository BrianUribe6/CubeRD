package com.tst.cuberd;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryDB {

    public static List<SolveEntry> loadData(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                mContext.getString(R.string.PREFERENCE_HISTORY_DB), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String solveDataJson = sharedPreferences.getString(mContext.getText(R.string.PREFERENCE_SOLVE_HISTORY).toString(), null);
        Type solveEntryListType = new TypeToken<ArrayList<SolveEntry>>(){}.getType();
        List<SolveEntry> mData = gson.fromJson(solveDataJson, solveEntryListType);

        if(mData == null){
            mData = new ArrayList<>();
        }
        return mData;
    }

    public static void saveData(Context mContext, List<SolveEntry> mData) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                mContext.getString(R.string.PREFERENCE_HISTORY_DB), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String solveDataJson = gson.toJson(mData);
        editor.putString(mContext.getText(R.string.PREFERENCE_SOLVE_HISTORY).toString(), solveDataJson);
        editor.apply();
    }
}
