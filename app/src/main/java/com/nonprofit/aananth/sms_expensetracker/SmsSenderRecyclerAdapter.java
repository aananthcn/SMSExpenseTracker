package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SmsSenderRecyclerAdapter extends RecyclerView.Adapter<SmsSenderRecyclerAdapter.ViewHolder> {
    private List<SmsSender> mSmsSenderList;

    // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView smsSender, expCat, money;

        private ViewHolder(View view) {
            super(view);
            smsSender = (TextView) view.findViewById(R.id.sender_name);
            expCat = (TextView) view.findViewById(R.id.exp_category);
            money = (TextView) view.findViewById(R.id.sender_money);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SmsSenderRecyclerAdapter(List<SmsSender> senders) {
        this.mSmsSenderList = senders;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SmsSenderRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_sender_item_layout,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...


        return new ViewHolder(v);
    }

    //@Override
    public void onBindViewHolder(SmsSenderRecyclerAdapter holder, int position) {

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
        SmsSender sender = mSmsSenderList.get(position);
        holder.smsSender.setText(sender.name);
        holder.expCat.setText(sender.expCategory.expCatName);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("ta", "IN"));
        holder.money.setText(formatter.format(sender.money));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSmsSenderList.size();
    }
}
