package com.app.kagada.planb.ui.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.RestaurantResponse;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
  private List<RestaurantResponse.NearbyRestaurant> mRestaurantList;
  private View v;
  private OnRstClicked onRstClicked;

  public interface OnRstClicked {
    void onRstClicked(int position);
  }

  PlanAdapter(List<RestaurantResponse.NearbyRestaurant> list, OnRstClicked onRstClicked) {
    this.mRestaurantList = list;
    this.onRstClicked = onRstClicked;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_row_item, parent, false);
    return new ViewHolder(v, onRstClicked);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    final RestaurantResponse.NearbyRestaurant item = mRestaurantList.get(position);
    if(!item.getImgUrl().equals("")) {
      Glide
              .with(v)
              .load(item.getImgUrl())
              .centerCrop()
              .into(holder.mImage);
    }
    else
      holder.mImage.setImageResource(R.drawable.ic_restaurant_black_24dp);
    holder.mName.setText(item.getName());
    holder.mCost.setText(item.getAvgCost().toString());
    holder.mLocality.setText(item.getLocality());
    holder.mCuisines.setText(item.getCuisines());
    holder.rstRating.setRating(item.getRating());
  }

  @Override
  public int getItemCount() {
    return mRestaurantList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView mImage;
    TextView mName;
    TextView mCost;
    TextView mLocality;
    TextView mCuisines;
    OnRstClicked onRstClicked;
    RatingBar rstRating;

    ViewHolder(@NonNull final View itemView, OnRstClicked onRstClicked) {
      super(itemView);

      mImage = itemView.findViewById(R.id.image_rst);
      mName = itemView.findViewById(R.id.name_rst);
      mCost = itemView.findViewById(R.id.cost_avg);
      mLocality = itemView.findViewById(R.id.locality);
      mCuisines = itemView.findViewById(R.id.rst_cuisines);
      rstRating = itemView.findViewById(R.id.rst_rating);
      this.onRstClicked = onRstClicked;
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      onRstClicked.onRstClicked(getAdapterPosition());
    }
  }
}
