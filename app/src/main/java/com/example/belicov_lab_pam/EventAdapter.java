package com.example.belicov_lab_pam;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;


    public EventAdapter(List<Event> events) {
        this.eventList = events;
    }

    public void updateEvents(List<Event> newEvents) {
        this.eventList = newEvents;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.title.setText(event.getTitle());
        holder.description.setText(event.getDescription());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeStr = sdf.format(new Date(event.getDatetime()));
        holder.time.setText(timeStr);

        long now = System.currentTimeMillis();
        if (event.getDatetime() < now) {
            holder.title.setTextColor(Color.GRAY);
            holder.description.setTextColor(Color.DKGRAY);
            holder.time.setTextColor(Color.GRAY);
            holder.itemView.setBackgroundColor(Color.parseColor("#2A2A2A"));
        } else {
            holder.title.setTextColor(Color.WHITE);
            holder.description.setTextColor(Color.LTGRAY);
            holder.time.setTextColor(Color.LTGRAY);
            holder.itemView.setBackgroundColor(Color.parseColor("#333333"));
        }
    }


    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, description,time;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            description = itemView.findViewById(R.id.eventDescription);
            time = itemView.findViewById(R.id.eventTime);
        }
    }
}
