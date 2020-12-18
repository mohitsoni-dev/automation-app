package android.example.checkpoint1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button start, stop;
    public static ArrayAdapter<String> adapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.buttonStart);
        stop = (Button) findViewById(R.id.buttonStop);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, BackgroundService.names);
        listView.setAdapter(adapter);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View view) {
        if (view == start) {
            //startService(new Intent(this, BackgroundService.class));

            Intent intent = new Intent(this, Bubble.class);
            PendingIntent bubblePendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, 0);

            NotificationChannel channel = new NotificationChannel("ok", "NEW CHANNEL", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("THIS IS CHANNEL");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Icon icon = Icon.createWithResource(this, R.drawable.ic_launcher_foreground);

            Notification.BubbleMetadata bubbleMetadata = new Notification.BubbleMetadata.Builder()
                    .setIntent(bubblePendingIntent)
                    .setIcon(icon)
                    .setDesiredHeight(600)
                    .build();

            Person chatPartner = new Person.Builder()
                    .setName("ChatPartner")
                    .setImportant(true)
                    .build();


            Notification.Builder builder = new Notification.Builder(getApplicationContext(), channel.getId())
                    .setSmallIcon(android.R.drawable.sym_call_incoming)
                    .setContentTitle("NOTIFICATION TITLe")
                    .setContentText("CONTENT TEXT")
                    .setBubbleMetadata(bubbleMetadata)
                    .addPerson(chatPartner)
                    .setAutoCancel(true);

            notificationManager.notify(0, builder.build());


            //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        } else if (view == stop) {
            stopService(new Intent(this, BackgroundService.class));
        }
    }
}

/*
ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

for (int i = 0; i < runningAppProcessInfo.size(); i++) {
  if(runningAppProcessInfo.get(i).processName.equals("com.the.app.you.are.looking.for") {
    // Do you stuff
  }
}
*/
