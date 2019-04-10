package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivityRecyclerAdapter extends RecyclerView.Adapter<MainActivityRecyclerAdapter.ViewHolder> {
    private List<SmsSender> mSmsSenderList;

    // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView smsSender;
        private TextView totalMoney;

        private ViewHolder(View view) {
            super(view);
            smsSender = (TextView) view.findViewById(R.id.sender_name);
            totalMoney = (TextView) view.findViewById(R.id.total_money);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MainActivityRecyclerAdapter(List<SmsSender> senders) {
        this.mSmsSenderList = senders;
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
        SmsSender sender = mSmsSenderList.get(position);
        holder.smsSender.setText(sender.name);
        holder.totalMoney.setText(sender.expCategory.expCatName);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSmsSenderList.size();
    }
}
