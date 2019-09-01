package com.tst.cuberd;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{
    private Context mContext;
    private List<SolveEntry> mData;
    private static final String TAG = "HistoryAdapter";

    private SparseBooleanArray expandedItems = new SparseBooleanArray();
    private ActionMode mActionMode;
    private List<SolveEntry> selected = new ArrayList<>();
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.action_menu_history, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_history_share:
                    Toast.makeText(mContext, "Share Button Clicked", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                case R.id.action_history_remove_selected:
                    //Separating a single delete from multiple delete to animate deletion
                    if (selected.size() == 1) {
                        SolveEntry selectedSolve = selected.get(0);
                        int position = selectedSolve.getIndex();
                        mData.remove(selectedSolve);
                        notifyItemRemoved(position);    //animate and update recyclerView
                        selected.clear();
                    } else {
                        mData.removeAll(selected);
                        notifyDataSetChanged();
                        selected.clear();
                    }
                    HistoryDB.saveData(mContext, mData);   //Saving current history state
                    Toast.makeText(mContext, "Delete Button Clicked", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
        }
    };


    public HistoryAdapter(Context mContext, List<SolveEntry> mData) {
        this.mContext = mContext;
        this.mData = mData;

        //initializing all cards as minimized
        for (int i = 0; i < mData.size(); i++) {
            expandedItems.append(i, false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_entry,
                viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final SolveEntry mSelectedItem = mData.get(i);
        String index = String.format(Locale.getDefault(),"%03d", mData.get(i).getIndex() + 1);
        String time = mData.get(i).getTime();
        String scramble = mData.get(i).getScramble();

        viewHolder.index.setText(index);
        viewHolder.time.setText(time);
        viewHolder.scramble.setText(scramble);
        //Trigger Action mode
        viewHolder.solveCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mActionMode != null) {
                    return false;
                }
                //entering action mode
                mActionMode = ((AppCompatActivity) view.getContext()).startSupportActionMode(mActionModeCallback);
                //selecting current item
                selected.add(mSelectedItem);
                highlight(viewHolder.solveCard);
                mActionMode.setTitle(1 + " " + mContext.getText(R.string.action_mode_select).toString());
                return true;
            }
        });
        viewHolder.solveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActionMode != null) {    //Action mode is active
                    if (selected.contains(mSelectedItem)) {
                        selected.remove(mSelectedItem);
                        unHighlight(viewHolder.solveCard);
                    } else {
                        selected.add(mSelectedItem);
                        highlight(viewHolder.solveCard);
                    }
                    mActionMode.setTitle(selected.size() + " " + mContext.getText(R.string.action_mode_select).toString());
                } else {     //Activity not in action mode so expand/collapse interaction is enabled
                    int currPos = viewHolder.getAdapterPosition();
                    boolean isExpanded = expandedItems.get(currPos);
                    if (isExpanded) {
                        viewHolder.scramble.setLines(1);    // collapse
                        //animate collapse
                        TransitionManager.beginDelayedTransition(viewHolder.solveCard, new AutoTransition());
                        //rotate arrow
                        createRotateAnimator(viewHolder.expandCollapse, 180f, 0f).start();
                        expandedItems.put(currPos, false);
                    } else {
                        viewHolder.scramble.setLines(2);    // expand
                        createRotateAnimator(viewHolder.expandCollapse, 0f, 180f).start();
                        TransitionManager.beginDelayedTransition(viewHolder.solveCard, new AutoTransition());
                        expandedItems.put(currPos, true);
                    }
                }
            }
        });
    }

    private void unHighlight(CardView card) {
        card.setCardBackgroundColor(mContext.getColor(R.color.white));
    }

    private void highlight(CardView card) {
        card.setCardBackgroundColor(mContext.getColor(R.color.grayLight));
    }

    //Code to rotate expand/collapse arrow
    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    //TODO private void selectAll(){ } might be better placed on Activity instead of Adapter

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView index;
        TextView time;
        TextView scramble;
        ImageView expandCollapse;
        CardView solveCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.solve_id);
            time = itemView.findViewById(R.id.solve_time);
            scramble = itemView.findViewById(R.id.solve_scramble);
            solveCard = itemView.findViewById(R.id.cardview_solve);
            expandCollapse = itemView.findViewById(R.id.img_expand_collapse);
        }
    }
}
