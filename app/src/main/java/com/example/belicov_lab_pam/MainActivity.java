package com.example.belicov_lab_pam;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.pm.PackageManager;
import com.bumptech.glide.Glide;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<Item> itemList;

    private static final String CHANNEL_ID = "notify-channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView gifView = findViewById(R.id.backgroundGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.background)
                .into(gifView);

        inputText = findViewById(R.id.inputText);
        recyclerView = findViewById(R.id.recyclerView);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnRemove = findViewById(R.id.btnRemove);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnNotify = findViewById(R.id.btnNotify);
        Button btnOrganiser = findViewById(R.id.btnOpenOrganiser);

        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        btnOrganiser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OrganiserActivity.class);
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v -> {
            String text = inputText.getText().toString().trim();
            if (!text.isEmpty()) {
                itemList.add(new Item("Item: " + text, "Description for " + text));
                adapter.notifyItemInserted(itemList.size() - 1);
                inputText.setText("");
            } else {
                Toast.makeText(this, "Input is empty", Toast.LENGTH_SHORT).show();
            }
        });

        btnRemove.setOnClickListener(v -> {
            if (!itemList.isEmpty()) {
                int lastIndex = itemList.size() - 1;
                itemList.remove(lastIndex);
                adapter.notifyItemRemoved(lastIndex);
            }
        });

        btnSearch.setOnClickListener(v -> {
            String query = inputText.getText().toString().trim();
            if (!query.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)));
                startActivity(intent);
            }
        });

        btnNotify.setOnClickListener(v -> {
            btnNotify.setEnabled(false); // отключаем кнопку
            final int[] secondsRemaining = {10};
            btnNotify.setText("10");

            Handler handler = new Handler();
            Runnable countdownRunnable = new Runnable() {
                @Override
                public void run() {
                    secondsRemaining[0]--;
                    if (secondsRemaining[0] > 0) {
                        btnNotify.setText(String.valueOf(secondsRemaining[0]));
                        handler.postDelayed(this, 1000);
                    } else {
                        btnNotify.setText("Notify in 10s");
                        btnNotify.setEnabled(true);
                        showNotification("Hello!", "This is a push notification after 10 seconds.");
                    }
                }
            };

            handler.postDelayed(countdownRunnable, 1000);
        });

        createNotificationChannel();
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Main channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

}
