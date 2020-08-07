package com.cbsjchen0601.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cbsjchen0601.R;
import com.cbsjchen0601.YakGalleryActivity;
import com.cbsjchen0601.models.Yaks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class YakAdapter extends RecyclerView.Adapter<YakAdapter.YakHolder> {

    private static final String TAG = "YakAdapter";
    private List<Yaks> descriptionData;
    private Context context;
    private DatabaseReference databaseReference;
    private String dbKey = "";

    public YakAdapter(Context ct, ArrayList<Yaks> descriptionData, String key) {
        context = ct;
        this.descriptionData = descriptionData;
        this.dbKey = key;
    }

    @NonNull
    @Override
    public YakHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View yakView = layoutInflater.inflate(R.layout.yak_item, parent, false);
        Log.d(TAG, " onCreateViewHolder: started.");
        final YakHolder holder = new YakHolder(yakView);

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = descriptionData.get(holder.getAdapterPosition()).getyID();
                databaseReference = FirebaseDatabase.getInstance().getReference(dbKey).child(key).child("votes");
                int incrementedVote = descriptionData.get(holder.getAdapterPosition()).getVotes() + 1;
                databaseReference.setValue(incrementedVote);
                descriptionData.get(holder.getAdapterPosition()).setVotes(incrementedVote);
                notifyDataSetChanged();
            }
        });

        holder.ibDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = descriptionData.get(holder.getAdapterPosition()).getyID();
                databaseReference = FirebaseDatabase.getInstance().getReference(dbKey).child(key).child("votes");
                int decrementedVote = descriptionData.get(holder.getAdapterPosition()).getVotes() - 1;
                databaseReference.setValue(decrementedVote);
                descriptionData.get(holder.getAdapterPosition()).setVotes(decrementedVote);
                notifyDataSetChanged();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final YakHolder holder, final int position) {

        holder.tvDescription.setTypeface(holder.tvDescription.getTypeface(), Typeface.BOLD);
        holder.tvDate.setTypeface(holder.tvDescription.getTypeface(), Typeface.BOLD);
        holder.tvVotes.setTypeface(holder.tvDescription.getTypeface(), Typeface.BOLD);

        holder.tvDescription.setText(descriptionData.get(position).getYdescription());
        holder.tvDescription.setTextColor(-1);

        holder.tvDate.setText(descriptionData.get(position).getDate());
        holder.tvDate.setTextColor(-1);

        int currentVotes = descriptionData.get(position).getVotes();
        holder.tvVotes.setText(Integer.toString(currentVotes));

        if (!descriptionData.get(position).getYimage().equalsIgnoreCase("")) {
            Picasso.get().load(descriptionData.get(position).getYimage())
                    .resize(370, 170)
                    .error(R.drawable.yikyak)
                    .into(holder.ivYak);
        } else {
            holder.ivYak.setImageResource(R.drawable.yikyak);
        }

        makeViewGone(holder);
        setViewBasedOnVotes(holder, currentVotes);

        holder.yakLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent yakIntent = new Intent(context, YakGalleryActivity.class);
                yakIntent.putExtra("postDescription", descriptionData.get(position).getYdescription());
                yakIntent.putExtra("yID", descriptionData.get(position).getyID());
                yakIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(yakIntent);
            }
        });

    }

    private void makeViewGone(YakHolder holder) {

        holder.ivHot.setVisibility(View.GONE);
        holder.ivPrettyHot.setVisibility(View.GONE);
        holder.ivVeryHot.setVisibility(View.GONE);
        holder.ivAlert.setVisibility(View.GONE);
    }

    private void setViewBasedOnVotes(YakHolder holder, int currentVotes) {

        if (currentVotes < 0) {
            holder.tvVotes.setTextColor(Color.RED);
            holder.ivAlert.setVisibility(View.VISIBLE);
        } else {
            holder.tvVotes.setTextColor(Color.GREEN);
            if (currentVotes >= 100) {
                holder.ivHot.setVisibility(View.VISIBLE);
                holder.ivPrettyHot.setVisibility(View.VISIBLE);
                holder.ivVeryHot.setVisibility(View.VISIBLE);
            } else if (currentVotes > 50) {
                holder.ivHot.setVisibility(View.VISIBLE);
                holder.ivPrettyHot.setVisibility(View.VISIBLE);
            } else if (currentVotes > 10) {
                holder.ivHot.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return descriptionData.size();
    }

    public static class YakHolder extends RecyclerView.ViewHolder {

        TextView tvDescription, tvVotes, tvDate;
        ImageView ivYak, ivHot, ivPrettyHot, ivVeryHot, ivAlert;
        CardView yakLayout;
        ImageButton ibLike, ibDislike;

        public YakHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvVotes = itemView.findViewById(R.id.tvVotes);
            ivYak = itemView.findViewById(R.id.ivYak);
            yakLayout = itemView.findViewById(R.id.cvYak);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibDislike = itemView.findViewById(R.id.ibDislike);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivHot = itemView.findViewById(R.id.ivHot);
            ivPrettyHot = itemView.findViewById(R.id.ivPrettyHot);
            ivVeryHot = itemView.findViewById(R.id.ivVeryHot);
            ivAlert = itemView.findViewById(R.id.ivAlert);
        }
    }

}
