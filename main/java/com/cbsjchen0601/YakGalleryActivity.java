package com.cbsjchen0601;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbsjchen0601.adapter.CommentAdapter;
import com.cbsjchen0601.models.Comments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class YakGalleryActivity extends AppCompatActivity {

    private static final String TAG = "YakGalleryActivity";
    private FirebaseDatabase firebaseDatabase;
    private ImageButton btnComment;
    private TextView etComment, numberOfReplies;
    private RecyclerView rvComment;
    private CommentAdapter commentAdapter;
    private ArrayList<Comments> commentList;
    private MaterialToolbar mTopToolbar;
    private ProgressBar progressBar;
    private final static String DB_Comment_Key = "Comments";
    private String key = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yak_content);
        Log.d(TAG, " onCreate: started.");

        mTopToolbar = findViewById(R.id.mToolBar);
        rvComment = findViewById(R.id.rvComments);
        btnComment = findViewById(R.id.btnComment);
        etComment = findViewById(R.id.etComment);
        numberOfReplies = findViewById(R.id.numberOfReplies);
        progressBar = findViewById(R.id.pBar);
        setSupportActionBar(mTopToolbar);
        firebaseDatabase = FirebaseDatabase.getInstance();

        key = getIntent().getStringExtra("yID");

        initRvComment();

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference commentRef = firebaseDatabase.getReference(DB_Comment_Key).child(key).push();
                String content = etComment.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Comments comment = new Comments(content, dateFormat.format(new Date()));

                if (content.length() > 200) {
                    Toast.makeText(YakGalleryActivity.this, "Exceeded word limit: 200 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(YakGalleryActivity.this, "Commented!", Toast.LENGTH_SHORT).show();
                        etComment.setText("");
                        rvComment.smoothScrollToPosition(rvComment.getAdapter().getItemCount());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(YakGalleryActivity.this, " Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initRvComment() {

        progressBar.setVisibility(View.VISIBLE);
        getYakIntent();
        rvComment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference(DB_Comment_Key).child(key);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                commentList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Comments comments = new Comments();
                    comments.setContent((String) snap.child("content").getValue());
                    comments.setDate((String) snap.child("date").getValue());
                    commentList.add(comments);
                }
                setReplyCount();
                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                rvComment.setAdapter(commentAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getYakIntent() {
        String desc = "";
        if (getIntent().hasExtra("postDescription")) {
            desc = getIntent().getStringExtra("postDescription");
            setIntentPost(desc);
        }
    }

    private void setIntentPost(String desc) {
        TextView tvYakContentDescription = findViewById(R.id.tvYakContentDescription);
        tvYakContentDescription.setText(desc);
    }

    private void setReplyCount() {
        String totalReplies = commentList.size() + " REPLIES";
        numberOfReplies.setText(totalReplies);
        numberOfReplies.setVisibility(View.VISIBLE);
    }
}
