package com.ankur.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class JournalDetail extends AppCompatActivity {

    private Vibrator vibrator;

    public static final int REQUEST_CODE = 1;
    private JournalDetailRecyclerViewAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private LayoutInflater layoutInflater;
    private CollectionReference collRef = db.collection("Journal");
    private StorageReference storageReference;
    private Journal journal;
    private String userId;
    private String userName;
    private String imageUrl;
    private String time;
    private String title;
    private String text;
    private String date;
    private String month;
    private String year;
    //private String timeAdded;
    private String day;
    private int position;
    private String docId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        vibrator = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

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
            //Journal journal1 = (Journal) i.getSerializableExtra("object");

            if (imageUrl != null) {
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                journal = new Journal(docId, userId, userName, title, text, imageUrl, date, day, month, year, time);
            } else {
                journal = new Journal(docId, userId, userName, title, text, date, day, month, year, time);
            }
        }

        recyclerView = findViewById(R.id.detail_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new JournalDetailRecyclerViewAdapter(JournalDetail.this, journal);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit_journal) {
            vibrator.vibrate(100);

            editJournal();

            return true;
        } else if (id == R.id.delete_journal) {

            vibrator.vibrate(100);
            deleteJournal();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void editJournal() {

        Intent intent = new Intent(JournalDetail.this, EditJournal.class);

        intent.putExtra("userid", userId);
        intent.putExtra("username", userName);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        intent.putExtra("imageurl", imageUrl);
        intent.putExtra("date", date);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("time", time);
        intent.putExtra("position", position);
        intent.putExtra("docId", docId);
        //intent.putExtra("object", journal);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void deleteJournal() {

        collRef.whereEqualTo("userId", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("day", day)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year)
                .whereEqualTo("time", time)
                .whereEqualTo("jTitle", title)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                docId = snapshot.getId();
                            }

                            if (imageUrl != null) {
                                storageReference.delete();
                            }
                            Log.d("Document", "onClick " + docId);
                            collRef.document(docId).delete();
                            adapter.notifyItemRemoved(position);

                            startActivity(new Intent(JournalDetail.this, JournalList.class));
                            finish();
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("Document", "onClick" + docId);
                Log.d("Document", "onClick" + e.getMessage());
                Toast.makeText(JournalDetail.this, "Could Not Find Document", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
