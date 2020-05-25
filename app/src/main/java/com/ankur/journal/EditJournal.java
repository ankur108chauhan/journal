package com.ankur.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class EditJournal extends AppCompatActivity implements View.OnClickListener {

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
    private JournalDetailRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private String userId;
    private String userName;
    private String imageUrl;
    private String time;
    private String title;
    private String text;
    private String date;
    private String month;
    private String year;
    private String day;
    private int position;
    private String newImageURL;
    //private Timestamp timestamp;
    private String docId = "";
    public static final String KEY_TITLE = "jTitle";
    public static final String KEY_TEXT = "jText";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private CollectionReference collRef = db.collection("Journal");
    private DocumentReference docRef;

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
        setContentView(R.layout.activity_edit_journal);

        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        backImage = findViewById(R.id.e_back_to_journallist);
        saveImage = findViewById(R.id.e_save_journal);
        cameraImage = findViewById(R.id.e_image_chooser);
        journalImage = findViewById(R.id.e_journal_image);
        dateShow = findViewById(R.id.e_date_show);
        dayShow = findViewById(R.id.e_day_show);
        monthShow = findViewById(R.id.e_month_show);
        yearShow = findViewById(R.id.e_year_show);
        timeShow = findViewById(R.id.e_time_show);
        journalTitle = findViewById(R.id.e_journal_title);
        journalText = findViewById(R.id.e_journal_text);
        progressBar = findViewById(R.id.e_journal_progressbar);
        relativeLayout = findViewById(R.id.e_journal_relative);


        backImage.setOnClickListener(this);
        saveImage.setOnClickListener(this);
        cameraImage.setOnClickListener(this);
        dateShow.setOnClickListener(this);
        timeShow.setOnClickListener(this);

        setFieldValues();
        setUserIdAndName();

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

    private void setUserIdAndName() {
        if (JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUserName();
            journalText.setHint("Hi " + currentUserName + " " + "Write Here...");
        }
    }

    private void setFieldValues() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            userId = bundle.getString("userid");
            userName = bundle.getString("username");
            title = bundle.getString("title");
            text = bundle.getString("text");
            imageUrl = bundle.getString("imageurl");
            date = bundle.getString("date");
            month = bundle.getString("month");
            year = bundle.getString("year");
            day = bundle.getString("day");
            time = bundle.getString("time");
            position = bundle.getInt("position");
            docId = bundle.getString("docId");

            //Intent i = getIntent();
            //Journal j = (Journal) i.getSerializableExtra("object");

            //assert j != null;
            //timestamp = j.getTimestamp();

            if (imageUrl != null) {
                journalImage.setVisibility(View.VISIBLE);
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                Picasso.get().load(imageUrl).fit().into(journalImage);
            } else
                storageReference = FirebaseStorage.getInstance().getReference();
        }

        timeShow.setText(time);
        dateShow.setText(date);
        monthShow.setText(month);
        yearShow.setText(month);
        dayShow.setText(day);
        journalTitle.setText(title);
        journalText.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.e_back_to_journallist:
                startActivity(new Intent(EditJournal.this, JournalList.class));
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            case R.id.e_save_journal:
                vibrator.vibrate(100);
                saveJournal();
                break;

            case R.id.e_image_chooser:

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                if (galleryIntent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

        }
    }

    private void saveJournal() {

        progressBar.setVisibility(View.VISIBLE);

        final String jTitle = journalTitle.getText().toString().trim();
        final String jText = journalText.getText().toString().trim();


        if (!title.isEmpty() && !text.isEmpty() && imageUri != null) {

            saveJournalWithImage(jTitle, jText);

        }

        else if(!title.isEmpty() && !text.isEmpty() && imageUri == null && imageUrl != null) {
            saveWithPreviousFields(jTitle,jText);
        }

        else if (!title.isEmpty() && !text.isEmpty()) {

            saveJournalWithoutImage(jTitle, jText);

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(relativeLayout, R.string.empty_values, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveWithPreviousFields(String jTitle, String jText) {



        final Journal journal = new Journal(docId, userId, userName, jTitle, jText, imageUrl, date, day, month, year, time);

        docRef = collRef.document(docId);

        docRef.set(journal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(EditJournal.this, JournalList.class);
                //intent.putExtra("object",journal);
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

    private void saveJournalWithImage(final String jTitle, final String jText) {

        Log.d("imageuri","OnClick " + imageUri);
        Log.d("imageuri","OnClick " + imageUrl);


        final StorageReference storeRef = storageReference.child("image_" + Timestamp.now().getSeconds());

        storeRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String newImageUrl = uri.toString();

                        final Journal journal = new Journal(docId, userId, userName, jTitle, jText, newImageUrl, date, day, month, year, time);

                        docRef = collRef.document(docId);

                        docRef.set(journal).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressBar.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(EditJournal.this, JournalList.class);
                                //intent.putExtra("object",journal);
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

    private void saveJournalWithoutImage(String jTitle, String jText) {

        final Journal journal = new Journal(docId, currentUserId, currentUserName, jTitle, jText, date, day, month, year, time);

        docRef = collRef.document(docId);

        docRef.set(journal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(EditJournal.this, JournalList.class);
                //intent.putExtra("object",journal);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                journalImage.setVisibility(View.VISIBLE);
                journalImage.setImageURI(imageUri);
                storageReference.delete();

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
