package com.ankur.journal;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.VIBRATOR_SERVICE;

public class JournalRecyclerViewAdapter extends RecyclerView.Adapter<JournalRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Journal> journalArrayList;
    private LayoutInflater inflater;
    private Vibrator vibrator;
    private Journal journal;

    public JournalRecyclerViewAdapter(Context context, ArrayList<Journal> journalArrayList) {
        this.context = context;
        this.journalArrayList = journalArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.journal_row,viewGroup,false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        journal = journalArrayList.get(position);

        Log.d("image ","Onclick " + journal.getImageURL());

        String imageUrl = journal.getImageURL();

        if(imageUrl != null) {
            holder.jImage.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).resize(150,150).into(holder.jImage);
        }

        holder.jTitle.setText(journal.getjTitle());
        holder.jText.setText(journal.getjText());
        holder.jDate.setText(journal.getDate());
        holder.jMonth.setText(journal.getMonth());
        holder.jYear.setText(journal.getYear());
        holder.jDay.setText(journal.getDay());
        holder.jTime.setText(journal.getTime());

    }

    @Override
    public int getItemCount() {
        return journalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView jImage;
        private TextView jTitle;
        private TextView jText;
        private TextView jDate;
        private TextView jDay;
        private TextView jMonth;
        private TextView jYear;
        private TextView jTime;
        private CardView cardView;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;

            jImage = view.findViewById(R.id.j_image);
            jTitle = view.findViewById(R.id.j_title);
            jText = view.findViewById(R.id.j_text);
            jDate = view.findViewById(R.id.j_date);
            jMonth = view.findViewById(R.id.j_month);
            jYear = view.findViewById(R.id.j_year);
            jDay = view.findViewById(R.id.j_day);
            jTime = view.findViewById(R.id.j_time);
            cardView = view.findViewById(R.id.row_cardView);
            vibrator = (Vibrator)context.getSystemService(VIBRATOR_SERVICE);

            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            vibrator.vibrate(100);
            Journal journal1 = journalArrayList.get(getAdapterPosition());

            if(v.getId() == R.id.row_cardView) {


                Intent intent = new Intent(context,JournalDetail.class);
                intent.putExtra("userid",journal1.getUserId());
                intent.putExtra("username",journal1.getUserName());
                intent.putExtra("title",journal1.getjTitle());
                intent.putExtra("text",journal1.getjText());
                intent.putExtra("date",journal1.getDate());
                intent.putExtra("imageurl",journal1.getImageURL());
                intent.putExtra("day",journal1.getDay());
                intent.putExtra("month",journal1.getMonth());
                intent.putExtra("year",journal1.getYear());
                intent.putExtra("time",journal1.getTime());
                intent.putExtra("docId",journal1.getDocId());
                intent.putExtra("position",getAdapterPosition());
                //String timeAdded = String.valueOf(journal1.getDt());
                //intent.putExtra("object",journal1);
                context.startActivity(intent);
            }

        }
    }
}
