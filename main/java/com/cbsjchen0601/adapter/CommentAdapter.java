package com.cbsjchen0601.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbsjchen0601.R;
import com.cbsjchen0601.models.Comments;

import java.util.ArrayList;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private ArrayList<Comments> cData;

    public CommentAdapter(Context context, ArrayList<Comments> cData) {
        this.context = context;
        this.cData = cData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View commentRow = LayoutInflater.from(context).inflate(R.layout.yak_comment, parent, false);
        return new CommentViewHolder(commentRow);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        if (holder.tvComment == null)
            hideCommentView(holder);
        else
            displayCommentView(holder, position);
    }

    public void hideCommentView(CommentViewHolder holder) {
        holder.tvDate.setVisibility(View.GONE);
        holder.tvComment.setVisibility(View.GONE);
    }

    public void displayCommentView(CommentViewHolder holder, int position) {
        holder.tvDate.setVisibility(View.VISIBLE);
        holder.tvComment.setVisibility(View.VISIBLE);

        holder.tvComment.setText(cData.get(position).getContent());
        holder.tvDate.setText(cData.get(position).getDate());
    }


    @Override
    public int getItemCount() {
        if (cData == null)
            return 0;
        return cData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView ivUser;
        TextView tvComment, tvDate;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            ivUser = itemView.findViewById(R.id.ivUser);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
