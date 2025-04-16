package com.example.films;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.films.adapter.EventAdapter;
import com.example.films.model.Event;
import com.example.films.notification.NotificationHelper;
import com.example.films.viewmodel.EventViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private EventViewModel eventViewModel;
    private EventAdapter adapter;
    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TabLayout tabLayout;
    private Chip chipSortDate, chipSortPriority;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create notification channel for Android 8.0+
        NotificationHelper.createNotificationChannel(this);

        initViews();
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        calendarView = findViewById(R.id.calendar_view);
        recyclerView = findViewById(R.id.recycler_events);
        emptyView = findViewById(R.id.text_empty_view);
        tabLayout = findViewById(R.id.tab_layout);
        chipSortDate = findViewById(R.id.chip_sort_date);
        chipSortPriority = findViewById(R.id.chip_sort_priority);
        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add_event);

        fabAddEvent.setOnClickListener(v -> showEventDialog(null));
    }

    private void setupRecyclerView() {
        adapter = new EventAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        
        // Observe filtered events based on the selected date range
        eventViewModel.getFilteredEvents().observe(this, events -> {
            adapter.submitList(events);
            updateEmptyView(events);
        });
    }

    private void setupListeners() {
        // Set up date selection listener
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                long startOfDay = calendar.getTimeInMillis();
                
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                long endOfDay = calendar.getTimeInMillis();
                
                eventViewModel.setDateFilter(startOfDay, endOfDay);
            }
        });

        // Set up tab selection listener for day/week/month views
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Day
                        eventViewModel.filterToday();
                        break;
                    case 1: // Week
                        eventViewModel.filterThisWeek();
                        break;
                    case 2: // Month
                        eventViewModel.filterThisMonth();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Set up sort options
        chipSortDate.setOnClickListener(v -> eventViewModel.setSortType(EventViewModel.SORT_BY_DATE));
        chipSortPriority.setOnClickListener(v -> eventViewModel.setSortType(EventViewModel.SORT_BY_PRIORITY));
    }

    private void updateEmptyView(List<Event> events) {
        if (events == null || events.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEventClick(Event event) {
        showEventDialog(event);
    }

    private void showEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_event, null);
        builder.setView(dialogView);

        // Initialize dialog views
        TextInputEditText titleEditText = dialogView.findViewById(R.id.edit_event_title);
        TextInputEditText descriptionEditText = dialogView.findViewById(R.id.edit_event_description);
        TextInputEditText dateEditText = dialogView.findViewById(R.id.edit_event_date);
        TextInputEditText timeEditText = dialogView.findViewById(R.id.edit_event_time);
        RadioButton radioLow = dialogView.findViewById(R.id.radio_low_priority);
        RadioButton radioMedium = dialogView.findViewById(R.id.radio_medium_priority);
        RadioButton radioHigh = dialogView.findViewById(R.id.radio_high_priority);
        SwitchMaterial notificationSwitch = dialogView.findViewById(R.id.switch_notification);

        // Set dialog title based on whether we're adding or editing
        builder.setTitle(event == null ? R.string.add_event : R.string.edit_event);

        // Set up date picker
        Calendar calendar = Calendar.getInstance();
        if (event != null) {
            calendar.setTimeInMillis(event.getDateTime());
        }

        // Populate fields if editing an existing event
        if (event != null) {
            titleEditText.setText(event.getTitle());
            descriptionEditText.setText(event.getDescription());
            dateEditText.setText(dateFormat.format(calendar.getTime()));
            timeEditText.setText(timeFormat.format(calendar.getTime()));
            
            switch (event.getPriority()) {
                case 3:
                    radioHigh.setChecked(true);
                    break;
                case 2:
                    radioMedium.setChecked(true);
                    break;
                case 1:
                default:
                    radioLow.setChecked(true);
                    break;
            }
            
            notificationSwitch.setChecked(event.isNotificationEnabled());
        } else {
            // Set current date and time for new events
            dateEditText.setText(dateFormat.format(calendar.getTime()));
            timeEditText.setText(timeFormat.format(calendar.getTime()));
        }

        // Date picker dialog
        dateEditText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        dateEditText.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Time picker dialog
        timeEditText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        timeEditText.setText(timeFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true);
            timePickerDialog.show();
        });

        // Add Save and Cancel buttons
        builder.setPositiveButton(R.string.save, null); // We'll override this to prevent dialog dismissal on validation failure
        
        if (event != null) {
            // Add Delete button for existing events
            /*builder.setNeutralButton(R.string.delete, (dialog, which) -> {
                // Cancel any existing notification
                if (event.isNotificationEnabled()) {
                    NotificationHelper.cancelNotification(MainActivity.this, event.getId());
                }
                eventViewModel.delete(event);
            });*/
        }
        
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        
        // Override the click listener for the positive button to validate input
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                
                if (title.isEmpty()) {
                    titleEditText.setError(getString(R.string.event_title) + " " + getString(R.string.no_events));
                    return;
                }
                
                // Get priority value
                int priority;
                if (radioHigh.isChecked()) {
                    priority = 3;
                } else if (radioMedium.isChecked()) {
                    priority = 2;
                } else {
                    priority = 1;
                }
                
                boolean enableNotification = notificationSwitch.isChecked();
                
                if (event == null) {
                    // Create new event
                    Event newEvent = new Event(title, description, calendar.getTimeInMillis(), priority, enableNotification);
                    eventViewModel.insert(newEvent);
                    
                    // Schedule notification if enabled
                    if (enableNotification) {
                        // We need to get the ID after insertion, but we don't have it yet
                        // This is handled in the repository observer
                    }
                } else {
                    // Update existing event
                    event.setTitle(title);
                    event.setDescription(description);
                    event.setDateTime(calendar.getTimeInMillis());
                    event.setPriority(priority);
                    
                    // Handle notification changes
                    if (event.isNotificationEnabled() != enableNotification) {
                        if (enableNotification) {
                            // Enable notification
                            event.setNotificationEnabled(true);
                        } else {
                            // Cancel existing notification
                            NotificationHelper.cancelNotification(MainActivity.this, event.getId());
                            event.setNotificationEnabled(false);
                        }
                    } else if (enableNotification) {
                        // Notification was and is still enabled, but time might have changed
                        NotificationHelper.cancelNotification(MainActivity.this, event.getId());
                    }
                    
                    eventViewModel.update(event);
                    
                    // Reschedule notification if enabled
                    if (enableNotification) {
                        NotificationHelper.scheduleNotification(MainActivity.this, event);
                    }
                }
                
                dialog.dismiss();
            });
        });
        
        dialog.show();
    }
}