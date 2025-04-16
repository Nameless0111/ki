package com.example.films.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.films.R;
import com.example.films.model.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventAdapter extends ListAdapter<Event, EventAdapter.EventViewHolder> {
    
    private final OnEventClickListener listener;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    
    public EventAdapter(OnEventClickListener listener) {
        super(new EventDiffCallback());
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = getItem(position);
        holder.bind(event, listener);
    }
    
    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView dateTimeTextView;
        private final CardView cardView;
        private final View priorityIndicator;
        
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_event_title);
            descriptionTextView = itemView.findViewById(R.id.text_event_description);
            dateTimeTextView = itemView.findViewById(R.id.text_event_date_time);
            cardView = itemView.findViewById(R.id.card_event);
            priorityIndicator = itemView.findViewById(R.id.view_priority_indicator);
        }
        
        public void bind(Event event, OnEventClickListener listener) {
            titleTextView.setText(event.getTitle());
            descriptionTextView.setText(event.getDescription());
            dateTimeTextView.setText(DATE_FORMAT.format(new Date(event.getDateTime())));
            
            // Set priority color indicator
            Context context = itemView.getContext();
            int color;
            switch (event.getPriority()) {
                case 3: // High priority
                    color = Color.RED;
                    break;
                case 2: // Medium priority
                    color = Color.rgb(255, 165, 0); // Orange
                    break;
                case 1: // Low priority
                default:
                    color = Color.GREEN;
                    break;
            }
            priorityIndicator.setBackgroundColor(color);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }
    }
    
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }
    
    private static class EventDiffCallback extends DiffUtil.ItemCallback<Event> {
        @Override
        public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getId() == newItem.getId();
        }
        
        @Override
        public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) 
                    && oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getDateTime() == newItem.getDateTime()
                    && oldItem.getPriority() == newItem.getPriority()
                    && oldItem.isNotificationEnabled() == newItem.isNotificationEnabled();
        }
    }
} 