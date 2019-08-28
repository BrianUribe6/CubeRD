package com.tst.cuberd;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private Context mContext;
    private List<SolveEntry> mData;

    private static final String TAG = "HistoryAdapter";

    public HistoryAdapter(Context mContext, List<SolveEntry> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_entry,
                viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String index = String.format(Locale.getDefault(),"%03d", mData.get(i).getIndex() + 1);
        String time = mData.get(i).getTime();
        String scramble = formatScramble(mData.get(i).getScramble());

        viewHolder.index.setText(index);
        viewHolder.time.setText(time);
        viewHolder.scramble.setText(scramble);
    }

    private String formatScramble(String scramble) {
        final int MAX_CHAR = 35;
        return scramble.substring(0, MAX_CHAR) + "...";
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView index;
        TextView time;
        TextView scramble;
        ImageButton more;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.solve_id);
            time = itemView.findViewById(R.id.solve_time);
            scramble = itemView.findViewById(R.id.solve_scramble);
            more = itemView.findViewById(R.id.imgbtn_more);
        }
    }
}
