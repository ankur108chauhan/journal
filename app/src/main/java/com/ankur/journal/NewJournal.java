package com.ankur.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Calendar;
import java.util.Locale;
import static java.util.Calendar.MONTH;

public class NewJournal extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 1;
    private ImageView backImage;
    private ImageView saveImage;
    private ImageView cameraImage;
    private ImageView journalImage;
    private TextView dateShow;
    private TextView dayShow;
    private TextView monthShow;
    private TextView yearShow;
    private TextView timeShow;
    private RelativeLayout relativeLayout;
    private EditText journalTitle;
    private EditText journalText;
    private ProgressBar progressBar;
    private String currentUserId;
    private String currentUserName;
    private Uri imageUri;
    private Vibrator vibrator;
    private Calendar calendar;

    private String date;
    private String day;
    private String month;
    private String year;
    private String time;
    private Timestamp timestamp;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private CollectionReference collRef = db.collection("Journal");

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal);

        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        backImage = findViewById(R.id.back_to_journallist);
        saveImage = findViewById(R.id.save_journal);
        cameraImage = findViewById(R.id.image_chooser);
        journalImage = findViewById(R.id.journal_image);
        dateShow = findViewById(R.id.date_show);
        dayShow = findViewById(R.id.day_show);
        monthShow = findViewById(R.id.month_show);
        yearShow = findViewById(R.id.year_show);
        timeShow = findViewById(R.id.time_show);
        journalTitle = findViewById(R.id.journal_title);
        journalText = findViewById(R.id.journal_text);
        progressBar = findViewById(R.id.save_journal_progressbar);
        relativeLayout = findViewById(R.id.journal_relative);

        backImage.setOnClickListener(this);
        saveImage.setOnClickListener(this);
        cameraImage.setOnClickListener(this);
        dateShow.setOnClickListener(this);
        timeShow.setOnClickListener(this);

        setDateTime();

        if (JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUserName();
            journalText.setHint("Hi " + currentUserName + " " + "Write Here...");
        }

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

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setDateTime() {
        calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        time = dateFormat.format(calendar.getTime());
        timeShow.setText(time);

        date = String.valueOf(calendar.get(Calendar.DATE));
        day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        month = calendar.getDisplayName(MONTH, Calendar.LONG, Locale.getDefault());
        year = String.valueOf(calendar.get(Calendar.YEAR));

        dateShow.setText(date);
        dayShow.setText(day);
        monthShow.setText(month);
        yearShow.setText(year);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back_to_journallist:
                startActivity(new Intent(NewJournal.this, JournalList.class));
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            case R.id.save_journal:
                vibrator.vibrate(100);
                saveJournal();
                break;

            case R.id.image_chooser:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }
    }


    private void saveJournal() {

        progressBar.setVisibility(View.VISIBLE);

        final String title = journalTitle.getText().toString().trim();
        final String text = journalText.getText().toString().trim();

        if (!title.isEmpty() && !text.isEmpty() && imageUri != null) {

            saveJournalWithImage(title, text);
        } else if (!title.isEmpty() && !text.isEmpty()) {

            saveJournalWithoutImage(title, text);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(relativeLayout, R.string.empty_values, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveJournalWithoutImage(String title, String text) {

        String docId = collRef.document().getId();
        Log.d("documentid","OnCreate " + docId);

        final Journal journal = new Journal(docId,currentUserId, currentUserName, title, text,date, day,
                month, year, time, Timestamp.now());

        collRef.document(docId).set(journal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(NewJournal.this, JournalList.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(relativeLayout, R.string.save_fail, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void saveJournalWithImage(final String title, final String text) {

        final StorageReference storeRef = storageReference.child("journal_images").child("image_" + Timestamp.now().getSeconds());

        storeRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String imageUrl = uri.toString();
                        String docId = collRef.document().getId();

                        final Journal journal = new Journal(docId,currentUserId, currentUserName, title, text, imageUrl, date, day, month, year,
                                time, Timestamp.now());

                        collRef.document(docId).set(journal).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(NewJournal.this, JournalList.class);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressBar.setVisibility(View.INVISIBLE);
                                Snackbar.make(relativeLayout, R.string.save_fail, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar.setVisibility(View.INVISIBLE);
                        Snackbar.make(relativeLayout, R.string.save_fail, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.INVISIBLE);
                Snackbar.make(relativeLayout, R.string.save_fail, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                journalImage.setVisibility(View.VISIBLE);
                journalImage.setImageURI(imageUri);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }

}
