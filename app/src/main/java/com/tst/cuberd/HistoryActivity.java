package com.tst.cuberd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<SolveEntry> mData;
    private HistoryDB historyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
        historyDB = new HistoryDB();
        mData = historyDB.loadData(this);
        initRecyclerView();
    }
    private void initRecyclerView(){
        recyclerView = findViewById(R.id.recycler_history);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new HistoryAdapter(this, mData);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.history_clear){
            //TODO add warning pop up
            clearHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearHistory() {
        mData.clear();
        historyDB.saveData(this, mData);
        mAdapter.notifyDataSetChanged();
    }

}
