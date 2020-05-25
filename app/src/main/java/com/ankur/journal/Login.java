package com.ankur.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
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

public class Login extends AppCompatActivity {

    private ImageView backToWelcome;
    private AutoCompleteTextView userEmailId;
    private EditText userPassword;
    private Button login;
    private ProgressBar progressBar;
    private Vibrator vibrator;
    private LinearLayout linearLayout;
    private CardView cardView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private StorageReference storageReference;
    private CollectionReference collRef = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        firebaseAuth = FirebaseAuth.getInstance();

        backToWelcome = findViewById(R.id.back_to_welcome);
        userEmailId = findViewById(R.id.login_email_id);
        userPassword = findViewById(R.id.login_password);
        login = findViewById(R.id.login_account_button);
        progressBar = findViewById(R.id.login_progressbar);
        linearLayout = findViewById(R.id.loginpage_linear);
        cardView = findViewById(R.id.login_page_cardview);

        backToWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(100);
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(100);
                loginEmailPasswordUser(userEmailId.getText().toString().trim(),
                        userPassword.getText().toString().trim());

            }
        });
    }

    private void loginEmailPasswordUser(String emailId, String password) {

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(emailId) && !TextUtils.isEmpty(password)) {

            firebaseAuth.signInWithEmailAndPassword(emailId, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();

                                collRef.whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                                                @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                    return;
                                                }

                                                assert queryDocumentSnapshots != null;
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                                                        JournalApi journalApi = JournalApi.getInstance();
                                                        journalApi.setUserId(snapshot.getString("userId"));
                                                        journalApi.setUserName(snapshot.getString("userName"));

                                                        startActivity(new Intent(Login.this, JournalList.class));
                                                        finish();
                                                    }
                                                }

                                            }
                                        });
                            } else {
                                Log.d("fail", "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }


                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("failure","onClick " + e.getMessage());
                            progressBar.setVisibility(View.INVISIBLE);
                            Snackbar.make(cardView, R.string.login_fail, Snackbar.LENGTH_LONG).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(cardView, R.string.empty_values, Snackbar.LENGTH_LONG).show();
        }
    }
}
