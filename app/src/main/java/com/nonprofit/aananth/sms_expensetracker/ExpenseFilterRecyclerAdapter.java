package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ExpenseFilterRecyclerAdapter extends RecyclerView.Adapter<ExpenseFilterRecyclerAdapter.ViewHolder> {
    private List<ExpenseFilter> mExpenseFilterList;

    // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView filter, sender_start, sender_end, money_start, money_end;

        private ViewHolder(View view) {
            super(view);
            filter = (TextView) view.findViewById(R.id.filter);
            sender_start = (TextView) view.findViewById(R.id.sender_start);
            sender_end = (TextView) view.findViewById(R.id.sender_end);
            money_start = (TextView) view.findViewById(R.id.money_start);
            money_end = (TextView) view.findViewById(R.id.money_end);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExpenseFilterRecyclerAdapter(List<ExpenseFilter> ef) {
        this.mExpenseFilterList = ef;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ExpenseFilterRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exp_filt_item_layout,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...


        return new ViewHolder(v);
    }

    //@Override
    public void onBindViewHolder(ExpenseFilterRecyclerAdapter holder, int position) {

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
        ExpenseFilter ef = mExpenseFilterList.get(position);
        holder.filter.setText(ef.filter);
        holder.sender_start.setText(ef.sender_start);
        holder.sender_end.setText(ef.sender_end);
        holder.money_start.setText(ef.money_start);
        holder.money_end.setText(ef.money_end);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int count = 0;

        if (mExpenseFilterList != null) {
            count = mExpenseFilterList.size();
        }

        return count;
    }
}
