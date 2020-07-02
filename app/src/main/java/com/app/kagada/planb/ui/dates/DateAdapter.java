package com.app.kagada.planb.ui.dates;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.DateResponse;
import com.bumptech.glide.Glide;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private List<DateResponse.Date> dateList;
    private DateClick dateClick;
    private View v;
    private boolean is_female,is_phone;

    public DateAdapter(List<DateResponse.Date> dateList, DateClick dateClick, boolean is_female, boolean is_phone) {
        this.dateList = dateList;
        this.dateClick = dateClick;
        this.is_female = is_female;
        this.is_phone = is_phone;
    }

    public interface DateClick {
        void onDateClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_row_item, parent, false);
        return new ViewHolder(v, dateClick);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final DateResponse.Date item = dateList.get(position);

        if (is_female) {
            holder.mName.setText(item.getMale().getEmail());
            if(!item.getMale().getImgUrl().equals("")) {
                Glide
                        .with(v)
                        .load(item.getMale().getImgUrl())
                        .centerCrop()
                        .into(holder.mImage);
            }
            else
                holder.mImage.setImageResource(R.drawable.ic_account);
            holder.mRating.setRating(item.getMale().getAvgRating());
            if (is_phone) {
                holder.mPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+item.getMale().getPhone()));
                        v.getContext().startActivity(intent);
                    }
                });
            }
            else
            {
                holder.mPhone.setVisibility(View.GONE);
                holder.phoneText.setVisibility(View.GONE);
            }
        } else {
            holder.mName.setText(item.getFemale().getEmail());
            if(!item.getFemale().getImgUrl().equals("")) {
                Glide
                        .with(v)
                        .load(item.getFemale().getImgUrl())
                        .centerCrop()
                        .into(holder.mImage);
            }
            else
                holder.mImage.setImageResource(R.drawable.ic_account);
            holder.mRating.setRating(item.getFemale().getAvgRating());
            if(is_phone)  {
                holder.mPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+item.getFemale().getPhone()));
                        v.getContext().startActivity(intent);
                    }
                });
            }
            else {
                holder.mPhone.setVisibility(View.GONE);
                holder.phoneText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mName, phoneText;
        RatingBar mRating;
        DateClick dateClick;
        ImageView mImage;
        ImageButton mPhone;

        ViewHolder(@NonNull View itemView, DateClick dateClick) {
            super(itemView);
            this.dateClick = dateClick;
            mName = itemView.findViewById(R.id.name_date);
            mImage = itemView.findViewById(R.id.image_date);
            mRating = itemView.findViewById(R.id.ratingBar);
            mPhone = itemView.findViewById(R.id.date_phone);
            phoneText = itemView.findViewById(R.id.call_date);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            dateClick.onDateClick(getAdapterPosition());
        }
    }
}
