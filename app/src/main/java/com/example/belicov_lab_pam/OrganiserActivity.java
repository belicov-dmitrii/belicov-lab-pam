package com.example.belicov_lab_pam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.bumptech.glide.Glide;
import android.widget.ImageView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class OrganiserActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView eventRecyclerView;
    private Button btnAddEvent;
    private ArrayList<Event> eventList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private long selectedDate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser);

        ImageView gifView = findViewById(R.id.backgroundGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.background)
                .into(gifView);



        calendarView = findViewById(R.id.calendarView);
        eventRecyclerView = findViewById(R.id.eventRecyclerView);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        selectedDate = calendarView.getDate();

        eventAdapter = new EventAdapter(new ArrayList<>());
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView.setAdapter(eventAdapter);

        loadEventsForDate(selectedDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = getDateInMillis(year, month, dayOfMonth);
            loadEventsForDate(selectedDate);
        });

        btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(OrganiserActivity.this, AddActivity.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventsForDate(selectedDate);
    }

    private void loadEventsForDate(long date) {
        ArrayList<Event> allEvents = readEventsFromXml();
        ArrayList<Event> matchedEvents = new ArrayList<>();

        Calendar calEvent = Calendar.getInstance();
        Calendar calSelected = Calendar.getInstance();
        calSelected.setTimeInMillis(date);

        for (Event e : allEvents) {
            calEvent.setTimeInMillis(e.getDatetime());
            if (calEvent.get(Calendar.YEAR) == calSelected.get(Calendar.YEAR) &&
                    calEvent.get(Calendar.MONTH) == calSelected.get(Calendar.MONTH) &&
                    calEvent.get(Calendar.DAY_OF_MONTH) == calSelected.get(Calendar.DAY_OF_MONTH)) {
                matchedEvents.add(e);
            }
        }

        matchedEvents.sort((e1, e2) -> Long.compare(e1.getDatetime(), e2.getDatetime()));

        eventAdapter.updateEvents(matchedEvents);
    }


    private long getDateInMillis(int year, int month, int day) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        return cal.getTimeInMillis();
    }

    private ArrayList<Event> readEventsFromXml() {
        ArrayList<Event> events = new ArrayList<>();
        try {
            File file = new File(getFilesDir(), "events.xml");
            if (!file.exists()) return events;

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            NodeList eventNodes = doc.getElementsByTagName("event");

            for (int i = 0; i < eventNodes.getLength(); i++) {
                Element element = (Element) eventNodes.item(i);

                String title = element.getElementsByTagName("title").item(0).getTextContent();
                String description = element.getElementsByTagName("description").item(0).getTextContent();
                long datetime = Long.parseLong(element.getElementsByTagName("datetime").item(0).getTextContent());

                events.add(new Event(title, description, datetime));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }
}
