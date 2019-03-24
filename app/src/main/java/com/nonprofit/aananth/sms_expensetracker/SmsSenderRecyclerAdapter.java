package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SmsSenderRecyclerAdapter extends RecyclerView.Adapter<SmsSenderRecyclerAdapter.ViewHolder> {
    private List<SmsSender> smsSenderList;

    // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView smsSender;

        private ViewHolder(View view) {
            super(view);
            smsSender = (TextView) view.findViewById(R.id.sender_name);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SmsSenderRecyclerAdapter(List<SmsSender> senders) {
        this.smsSenderList = senders;
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
        SmsSender sender = smsSenderList.get(position);
        holder.smsSender.setText(sender.name);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return smsSenderList.size();
    }
}
