package com.jj.timecapsule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeCapsuleAdapter extends RecyclerView.Adapter<TimeCapsuleAdapter.TimeCapsuleViewHolder> {

    private List<TimeCapsule> timeCapsuleList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd", Locale.getDefault());

    public TimeCapsuleAdapter(List<TimeCapsule> timeCapsuleList) {
        this.timeCapsuleList = timeCapsuleList;
    }

    @NonNull
    @Override
    public TimeCapsuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_capsule_item, parent, false);
        return new TimeCapsuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeCapsuleViewHolder holder, int position) {
        TimeCapsule timeCapsule = timeCapsuleList.get(position);
        holder.textViewTitle.setText(timeCapsule.getTitle());
        holder.textViewDate.setText(timeCapsule.getOpenDate());

        boolean isLocked = isDateLocked(timeCapsule.getOpenDate());
        if (isLocked) {
            holder.itemView.setEnabled(false);
            holder.imageViewLock.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setEnabled(true);
            holder.imageViewLock.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return timeCapsuleList.size();
    }

    private boolean isDateLocked(String openDate) {
        try {
            Date date = dateFormat.parse(openDate);
            return date != null && date.after(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
    }

    static class TimeCapsuleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDate;
        ImageView imageViewLock;

        public TimeCapsuleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewLock = itemView.findViewById(R.id.imageViewLock);
        }
    }
}
