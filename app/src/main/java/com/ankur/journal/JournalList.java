package com.ankur.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class JournalList extends AppCompatActivity {


    private Vibrator vibrator;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference storageReference;

    private JournalRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Journal> journalArrayList;
    private LayoutInflater layoutInflater;
    private CollectionReference collRef = db.collection("Journal");
    private TextView noJournal;

    @Override
    protected void onStart() {
        super.onStart();

        collRef.whereEqualTo("userId", JournalApi.getInstance().getUserId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                                Journal journal = snapshot.toObject(Journal.class);
                                journalArrayList.add(journal);
                            }

                            adapter = new JournalRecyclerViewAdapter(JournalList.this,journalArrayList);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        else {
                            noJournal.setVisibility(View.VISIBLE);
                            noJournal.setText("Please add a journal");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                noJournal.setVisibility(View.VISIBLE);
                noJournal.setText("Please add a journal");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);

        noJournal = findViewById(R.id.add_journal_text);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        journalArrayList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                currentUser = firebaseAuth.getCurrentUser();
//                if (currentUser != null) {
//                    //user is logged in
//                } else {
//                    //no user yet
//                }
//            }
//        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_new_journal) {
            vibrator.vibrate(100);

            if (currentUser != null && firebaseAuth != null) {
                startActivity(new Intent(JournalList.this, NewJournal.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            return true;
        } else if (id == R.id.sign_out) {
            vibrator.vibrate(100);

            if (currentUser != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                startActivity(new Intent(JournalList.this, MainActivity.class));
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
            journalArrayList.clear();

        }
    }

}

