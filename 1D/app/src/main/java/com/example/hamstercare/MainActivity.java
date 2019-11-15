package com.example.hamstercare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button topUpFoodButton;
    Button topUpWaterButton;



    //In MainActivity, create a constant for the notification channel ID.
    // Every notification channel must be associated with an ID that is unique within your package.
    // You use this channel ID later, to post your notifications.
    private static final String PRIMARY_FOOD_CHANNEL_ID = "primary_food_notification_channel";
    private static final String PRIMARY_WATER_CHANNEL_ID = "primary_water_notification_channel";
    //The Android system uses the NotificationManager class to deliver notifications to the user.
    // In MainActivity.java, create a member variable to store the NotificationManager object.
    private NotificationManager mFoodNotifyManager;
    private NotificationManager mWaterNotifyManager;
    //You need to associate the notification with a notification ID so that your code can update
    // or cancel the notification in the future. In MainActivity.java, create a constant for the notification ID:
    private static final int FOOD_NOTIFICATION_ID = 0;
    private static final int WATER_NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createFoodNotificationChannel();
        createWaterNotificationChannel();

        topUpFoodButton = findViewById(R.id.topUpFoodButton);
        topUpWaterButton = findViewById(R.id.topUpWaterButton);

        //basicReadWrite();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference foodLow = database.getReference("foodLow");
        DatabaseReference waterLow = database.getReference("waterLow");
        final DatabaseReference topUpWater = database.getReference("topUpWater");
        final DatabaseReference topUpFood = database.getReference("topUpFood");
        //foodLow.setValue("false");
        // [END write_message]

        // [START read_message]
        // Read from the database
        foodLow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String foodValue = dataSnapshot.getValue(String.class);
                if (foodValue.equals("true")){
                    sendFoodNotification();
                }
                Log.i("AngryMickey", "foodValue is: " + foodValue);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("AngryMickey", "Failed to read value.", error.toException());
            }
        });

        waterLow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String waterValue = dataSnapshot.getValue(String.class);
                if (waterValue.equals("true")){
                    sendWaterNotification();
                }
                Log.i("AngryMickey", "waterValue is: " + waterValue);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("AngryMickey", "Failed to read value.", error.toException());
            }
        });

        topUpFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpFood.setValue("true");
                Log.i("jinghan", "Food top up button is clicked");
            }
        });
        topUpWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topUpWater.setValue("true");
                Log.i("jinghan", "Water top up button is clicked");
            }
        });
    }



    //Create a method stub for the sendNotification() method:
    public void sendFoodNotification() {
        NotificationCompat.Builder notifyBuilder = getFoodNotificationBuilder();
        mFoodNotifyManager.notify(FOOD_NOTIFICATION_ID, notifyBuilder.build());
    }
    public void sendWaterNotification() {
        NotificationCompat.Builder notifyBuilder = getWaterNotificationBuilder();
        mWaterNotifyManager.notify(WATER_NOTIFICATION_ID, notifyBuilder.build());
    }


    public void createFoodNotificationChannel() {
        mFoodNotifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_FOOD_CHANNEL_ID,
                    "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mFoodNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
    public void createWaterNotificationChannel()
    {
        mWaterNotifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_WATER_CHANNEL_ID,
                    "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mWaterNotifyManager.createNotificationChannel(notificationChannel);
        }
    }


    private NotificationCompat.Builder getFoodNotificationBuilder(){
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                FOOD_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_FOOD_CHANNEL_ID)
                .setContentTitle("Food is Low")
                .setContentText("Food is low, please top up!")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;

    }
    private NotificationCompat.Builder getWaterNotificationBuilder(){
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                WATER_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_FOOD_CHANNEL_ID)
                .setContentTitle("Water is Low")
                .setContentText("Water is low, please top up!")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;

    }
}
