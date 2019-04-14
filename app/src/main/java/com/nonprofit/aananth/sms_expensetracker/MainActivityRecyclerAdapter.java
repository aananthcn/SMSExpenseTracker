package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivityRecyclerAdapter extends RecyclerView.Adapter<MainActivityRecyclerAdapter.ViewHolder> {
    private List<Expense> mExpenseList;

    // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView smsSender, money, date, address;

        private ViewHolder(View view) {
            super(view);
            smsSender = (TextView) view.findViewById(R.id.sender_name);
            money = (TextView) view.findViewById(R.id.money);
            date = (TextView) view.findViewById(R.id.sms_date);
            address = (TextView) view.findViewById(R.id.sms_addr_num);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityRecyclerAdapter(List<Expense> expenses) {
        this.mExpenseList = expenses;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainActivityRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_main_item_layout,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...


        return new ViewHolder(v);
    }

    //@Override
    public void onBindViewHolder(MainActivityRecyclerAdapter holder, int position) {

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
        Expense expense = mExpenseList.get(position);
        holder.smsSender.setText(expense.mSender.name);
        holder.money.setText(Double.toString(expense.mMoney));
        holder.date.setText(expense.mDate);
        holder.address.setText(expense.mAddrNum);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int count = 0;

        if (mExpenseList != null) {
            count = mExpenseList.size();
        }

        return count;
    }
}
