package com.ankur.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity {

    private EditText newUserName;
    private AutoCompleteTextView newEmailId;
    private EditText newPassword;
    private ImageView backImage;
    private Button signup;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private Vibrator vibrator;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CollectionReference collref = db.collection("Users");

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();
        vibrator = (Vibrator)getApplicationContext().getSystemService(VIBRATOR_SERVICE);

        newUserName = findViewById(R.id.new_username);
        newEmailId = findViewById(R.id.new_email_id);
        newPassword = findViewById(R.id.new_password);
        progressBar = findViewById(R.id.create_account_progressbar);
        linearLayout = findViewById(R.id.inner_linearLayout);
        backImage = findViewById(R.id.backtologin);
        signup = findViewById(R.id.signup);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is logged in
                } else {
                    //no user yet
                }
            }
        };

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(100);
                startActivity(new Intent(CreateAccount.this,MainActivity.class));
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(100);
                String userName = newUserName.getText().toString().trim();
                String emailId = newEmailId.getText().toString().trim();
                String password = newPassword.getText().toString().trim();

                if (!userName.isEmpty() && !emailId.isEmpty() && !password.isEmpty())
                    newUserWithEmailAndPassword(userName, emailId, password);
                else
                    Snackbar.make(linearLayout, "Empty Values Not Allowed", Snackbar.LENGTH_SHORT).show();

            }
        });
    }

    private void newUserWithEmailAndPassword(final String userName, String emailId, String password) {

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        String currentUserID = currentUser.getUid();

                        final Map<String, String> data = new HashMap<>();
                        data.put("userId", currentUserID);
                        data.put("userName", userName);
                        addUserToCollection(data);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.VISIBLE);
                Snackbar.make(linearLayout,"Could Not Create User",Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void addUserToCollection(Object data) {

        collref.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (Objects.requireNonNull(task.getResult()).exists()) {

                            progressBar.setVisibility(View.INVISIBLE);
                            String userName = task.getResult().getString("userName");
                            String userId = task.getResult().getString("userId");

                            //setting userid and username in API class
                            JournalApi journalApi = JournalApi.getInstance();
                            journalApi.setUserId(userId);
                            journalApi.setUserName(userName);

//                            intent.putExtra("UserId", userId);
//                            intent.putExtra("UserName", userName);
                            if (currentUser != null && firebaseAuth != null) {

                                Intent intent = new Intent(CreateAccount.this, JournalList.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            }

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Snackbar.make(linearLayout,"Could Not Retrieve Data",Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(linearLayout,"Could Not Create User",Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
