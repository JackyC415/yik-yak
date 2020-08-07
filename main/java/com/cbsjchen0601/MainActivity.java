package com.cbsjchen0601;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.cbsjchen0601.adapter.YakAdapter;
import com.cbsjchen0601.models.Yaks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    private ProgressBar progressBar;
    private YakAdapter yakAdapter;
    private MaterialToolbar mTopToolbar;
    private final static String DB_Post_Key = "Posts";
    private final static String DB_Updated_Post_Key = "UpdatedPosts";
    private ArrayList<Yaks> postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, " onCreate: started.");

        mTopToolbar = findViewById(R.id.mToolBar);
        setSupportActionBar(mTopToolbar);

        recyclerView = findViewById(R.id.yakRecyclerView);
        progressBar = findViewById(R.id.pBar);
        progressBar.setVisibility(View.VISIBLE);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);

        postList = new ArrayList<>();

        if (getIntent().hasExtra("updatedData")) {
            fetchPostFromDB(true);
            yakAdapter = new YakAdapter(getApplicationContext(), postList, DB_Updated_Post_Key);
        } else {
            fetchPostFromDB(false);
            yakAdapter = new YakAdapter(getApplicationContext(), postList, DB_Post_Key);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(yakAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.material_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.addPost) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.YakPost:
                            break;
                        case R.id.GPS:
                            startActivity(new Intent(MainActivity.this, LocationActivity.class));
                            return true;
                        case R.id.Notifcation:
                            startActivity(new Intent(MainActivity.this, MiscActivity.class));
                            return true;
                    }
                    return false;
                }
            };


    private void fetchPostFromDB(boolean status) {

        String key = DB_Post_Key;
        if(status) {
            key = DB_Updated_Post_Key;
        }

        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference(key);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String yakPost = snap.child("ydescription").getValue().toString();
                    String yakDate = snap.child("date").getValue().toString();
                    int yakVotes = Integer.parseInt(snap.child("votes").getValue().toString());
                    String yID = snap.child("yID").getValue().toString();

                    String imageUrl = "";
                    if(snap.hasChild("yimage")) {
                        imageUrl = snap.child("yimage").getValue().toString();
                    }

                    Yaks yaks = new Yaks(yakPost, imageUrl, yakVotes, yakDate, yID);
                    postList.add(yaks);
                }
                sortPostByVotesDescending();
                yakAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sortPostByVotesDescending() {
        Collections.sort(postList, new Comparator<Yaks>() {
            @Override
            public int compare(Yaks y1, Yaks y2) {
                return y2.getVotes() - y1.getVotes();
            }
        });
    }

}