package com.example.belicov_lab_pam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import android.widget.ImageView;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    private EditText inputTitle, inputDescription;

    private TextView eventTitle;
    private TimePicker timePicker;
    private Button btnSave;

    private long selectedDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ImageView gifView = findViewById(R.id.backgroundGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.background)
                .into(gifView);


        eventTitle = findViewById(R.id.eventTitle);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        timePicker = findViewById(R.id.timePicker);
        btnSave = findViewById(R.id.btnSaveEvent);

        selectedDateMillis = getIntent().getLongExtra("selectedDate", System.currentTimeMillis());

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(new java.util.Date(selectedDateMillis));

        eventTitle.setText("New event for " + formattedDate);

        btnSave.setOnClickListener(v -> saveEvent());
    }


    private void saveEvent() {
        String title = inputTitle.getText().toString().trim();
        String desc = inputDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDateMillis);
        cal.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        cal.set(Calendar.MINUTE, timePicker.getMinute());
        long datetime = cal.getTimeInMillis();

        writeEventToXml(new Event(title, desc, datetime));
        Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void writeEventToXml(Event event) {
        try {
            File file = new File(getFilesDir(), "events.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            Element rootElement;
            if (file.exists()) {
                doc = dBuilder.parse(file);
                rootElement = doc.getDocumentElement();
            } else {
                doc = dBuilder.newDocument();
                rootElement = doc.createElement("events");
                doc.appendChild(rootElement);
            }

            Element eventElement = doc.createElement("event");

            Element title = doc.createElement("title");
            title.appendChild(doc.createTextNode(event.getTitle()));
            eventElement.appendChild(title);

            Element desc = doc.createElement("description");
            desc.appendChild(doc.createTextNode(event.getDescription()));
            eventElement.appendChild(desc);

            Element time = doc.createElement("datetime");
            time.appendChild(doc.createTextNode(String.valueOf(event.getDatetime())));
            eventElement.appendChild(time);

            rootElement.appendChild(eventElement);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(file));
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
