package com.app.kagada.planb.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.MyVisitsResponse;
import com.app.kagada.planb.ui.plan.PlanAdapter;
import com.bumptech.glide.Glide;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<MyVisitsResponse.Visit> visitList;
    private View v;
    private OnRestClicked onRestClicked;

    public interface OnRestClicked {
        void OnRestClicked(int position);
    }

    public HistoryAdapter(List<MyVisitsResponse.Visit> visitList, OnRestClicked onRestClicked) {
        this.visitList = visitList;
        this.onRestClicked = onRestClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_row_item, parent, false);
        return new ViewHolder(v, onRestClicked);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        MyVisitsResponse.Visit item = visitList.get(position);
        if(!item.getRest().getImgUrl().equals("")) {
            Glide
                    .with(v)
                    .load(item.getRest().getImgUrl())
                    .centerCrop()
                    .into(holder.mImage);
        }
        else
            holder.mImage.setImageResource(R.drawable.ic_restaurant_black_24dp);
        holder.mName.setText(item.getRest().getName());
        holder.mCost.setText(item.getRest().getAvgCost().toString());
        holder.mLocality.setText(item.getRest().getLocality());
        holder.mDate.setText(item.getDayofvisit().split("T")[0]);
    }

    @Override
    public int getItemCount() {
        return visitList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mName;
        TextView mCost;
        TextView mLocality;
        TextView mDate;
        OnRestClicked onRestClicked;

        public ViewHolder(@NonNull View itemView, OnRestClicked onRestClicked) {
            super(itemView);
            mImage = itemView.findViewById(R.id.image_rst);
            mName = itemView.findViewById(R.id.name_rst);
            mCost = itemView.findViewById(R.id.cost_avg);
            mCost.setVisibility(View.GONE);
            mLocality = itemView.findViewById(R.id.locality);
            mDate = itemView.findViewById(R.id.rst_cuisines);
            this.onRestClicked = onRestClicked;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRestClicked.OnRestClicked(getAdapterPosition());
        }
    }
}
