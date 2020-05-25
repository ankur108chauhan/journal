package com.ankur.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private CollectionReference collRef = db.collection("Users");

    private Button welcomeLogin;
    private Button welcomeCreateAccount;
    private Vibrator vibrator;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        welcomeLogin = findViewById(R.id.welcome_login_button);
        welcomeCreateAccount = findViewById(R.id.welcome_create_button);

        welcomeLogin.setOnClickListener(this);
        welcomeCreateAccount.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        checkUserIsLoggedIn();

    }

    private void checkUserIsLoggedIn() {
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {

                    currentUser = firebaseAuth.getCurrentUser();

                    final String currentUserId = currentUser.getUid();

                    collRef.whereEqualTo("userId", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        return;
                                    }

                                    String name;

                                    assert queryDocumentSnapshots != null;
                                    if (!queryDocumentSnapshots.isEmpty()) {

                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUserId(snapshot.getString("userId"));
                                            journalApi.setUserName(snapshot.getString("userName"));

                                            startActivity(new Intent(MainActivity.this,
                                                    JournalList.class));
                                            finish();
                                        }
                                    }

                                }
                            });

                } else {

                }
            }
        };
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.welcome_login_button) {
            vibrator.vibrate(100);
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        } else if (v.getId() == R.id.welcome_create_button) {
            vibrator.vibrate(100);
            Intent intent = new Intent(MainActivity.this, CreateAccount.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
