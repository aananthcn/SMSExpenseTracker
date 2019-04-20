package com.nonprofit.aananth.sms_expensetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpCatRecyclerAdapter extends RecyclerView.Adapter<ExpCatRecyclerAdapter.ViewHolder> {
    private List<ExpCategory> categories;

    // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView exp_category, exp_cat_money;

        private ViewHolder(View view) {
            super(view);
            exp_category = (TextView) view.findViewById(R.id.exp_cat_item);
            exp_cat_money = (TextView) view.findViewById(R.id.exp_cat_money);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ExpCatRecyclerAdapter(List<ExpCategory> categoryList) {
        this.categories = categoryList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ExpCatRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.exp_cat_item_layout,
                parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...


        return new ViewHolder(v);
    }

    //@Override
    public void onBindViewHolder(ExpCatRecyclerAdapter holder, int position) {

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // http://www.androidhive.info/2016/01/android-working-with-recycler-view/
        ExpCategory expCategory = categories.get(position);
        holder.exp_category.setText(expCategory.expCatName);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("ta", "IN"));
        holder.exp_cat_money.setText(formatter.format(expCategory.money));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return categories.size();
    }
}
