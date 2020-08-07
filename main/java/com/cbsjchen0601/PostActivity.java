package com.cbsjchen0601;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cbsjchen0601.models.Yaks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PostActivity extends AppCompatActivity {

    private EditText etPost;
    private Button btnPost;
    private FirebaseDatabase firebaseDatabase;
    private final static String DB_Post_Key = "Posts";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etPost = findViewById(R.id.etPost);
        btnPost = findViewById(R.id.btnPost);
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {

        String postContent = etPost.getText().toString().trim();

        if (!TextUtils.isEmpty(postContent) && postContent.length() <= 200) {

            DatabaseReference postContentRef = firebaseDatabase.getReference(DB_Post_Key).push();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Yaks yaks = new Yaks(postContent, "", 0, dateFormat.format(new Date()), postContentRef.getKey());

            postContentRef.setValue(yaks).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(PostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                    etPost.setText("");
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, " Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (!TextUtils.isEmpty(postContent)) {
            Toast.makeText(PostActivity.this, "Please write something!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PostActivity.this, "Exceeded word limit: 200 characters!", Toast.LENGTH_SHORT).show();
        }

    }

}
