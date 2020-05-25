package com.ankur.journal;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
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

public class JournalDetailRecyclerViewAdapter extends RecyclerView.Adapter<JournalDetailRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private Journal journal;
    private LayoutInflater inflater;
    private Vibrator vibrator;

    public JournalDetailRecyclerViewAdapter(Context context, Journal journal) {
        this.context = context;
        this.journal = journal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.journal_detail_row,viewGroup,false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.d("image ","Onclick " + journal.getImageURL());

        String imageUrl = journal.getImageURL();

        if(imageUrl != null) {
            holder.jImage.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).fit().into(holder.jImage);
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
        return 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView jImage;
        private TextView jTitle;
        private TextView jText;
        private TextView jDate;
        private TextView jMonth;
        private TextView jYear;
        private TextView jDay;
        private TextView jTime;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);
            context = ctx;

            jImage = view.findViewById(R.id.d_journal_image);
            jTitle = view.findViewById(R.id.d_journal_title);
            jText = view.findViewById(R.id.d_journal_text);
            jDate = view.findViewById(R.id.d_date_show);
            jMonth = view.findViewById(R.id.d_month_show);
            jYear = view.findViewById(R.id.d_year_show);
            jDay = view.findViewById(R.id.d_day_show);
            jTime = view.findViewById(R.id.d_time_show);

        }

    }
}
