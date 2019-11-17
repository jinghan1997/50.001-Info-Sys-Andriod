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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    Button topUpFoodButton;
    Button topUpWaterButton;
    TextView foodLevel;
    TextView waterLevel;
    TextView prevWaterTopUpDateTimeText;
    TextView prevFoodTopUpDateTimeText;
    //ImageView hamsterImage;



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

        //find all the textviews and buttons
        topUpFoodButton = findViewById(R.id.topUpFoodButton);
        topUpWaterButton = findViewById(R.id.topUpWaterButton);
        foodLevel = findViewById(R.id.foodLevel);
        waterLevel = findViewById(R.id.waterLevel);
        prevWaterTopUpDateTimeText = findViewById(R.id.prevWaterTopUpDateTime);
        prevFoodTopUpDateTimeText = findViewById(R.id.prevFoodTopUpDateTime);
        //hamsterImage = findViewById(R.id.hamster);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference foodLow = database.getReference("foodLow");
        final DatabaseReference waterLow = database.getReference("waterLow");
        final DatabaseReference topUpWater = database.getReference("topUpWater");
        final DatabaseReference topUpFood = database.getReference("topUpFood");
        final DatabaseReference prevFoodTopUpDateTime = database.getReference("prevFoodTopUpDateTime");
        final DatabaseReference prevWaterTopUpDateTime = database.getReference("prevWaterTopUpDateTime");
        //foodLow.setValue("false");


        // To read the value of foodLow variable in firebase
        foodLow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String lowFoodValue = dataSnapshot.getValue(String.class);
                if (lowFoodValue.equals("true")){
                    //for UI, let user see the food level status
                    foodLevel.setText("Current food level: Insufficient");
                    sendNotification(FOOD_NOTIFICATION_ID);
                } else if (lowFoodValue.equals("false")){
                    //cancel notification
                    mFoodNotifyManager.cancel(FOOD_NOTIFICATION_ID);
                    //for UI, let user see the food level status
                    foodLevel.setText("Current food level: Sufficient");
                }
                Log.i("jinghan", "lowFoodValue is: " + lowFoodValue);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("jinghan", "Failed to read value.", error.toException());
            }
        });

        // To read the value of waterLow variable in firebase
        waterLow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String LowWaterValue = dataSnapshot.getValue(String.class);
                if (LowWaterValue.equals("true")){
                    //for UI, let user see the water level status
                    waterLevel.setText("Current water level: Insufficient");
                    //Log.i ("jinghan", waterLevel.getText().toString());
                    sendNotification(WATER_NOTIFICATION_ID);
                } else if (LowWaterValue.equals("false")){
                    //cancel notification
                    mWaterNotifyManager.cancel(WATER_NOTIFICATION_ID);
                    //for UI, let user see the water level status
                    waterLevel.setText("Current water level: Sufficient");
                }
                Log.i("jinghan", "LowWaterValue is: " + LowWaterValue);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("jinghan", "Failed to read value.", error.toException());
            }
        });

        // To read the value of prevFoodTopUpDateTime variable in firebase
        prevFoodTopUpDateTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String lowFoodValue = dataSnapshot.getValue(String.class);
                prevFoodTopUpDateTimeText.setText(lowFoodValue);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("jinghan", "Failed to read value.", error.toException());
            }
        });

        // To read the value of prevWaterTopUpDateTime variable in firebase
        prevWaterTopUpDateTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String lowWaterValue = dataSnapshot.getValue(String.class);
                prevWaterTopUpDateTimeText.setText(lowWaterValue);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("jinghan", "Failed to read value.", error.toException());
            }
        });

        //press buttons
        topUpFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodLevel.getText().toString().equals("Current food level: Sufficient")){
                    //to remind users and prevent overflowing
                    Toast.makeText(MainActivity.this, "Food level is sufficient, why top up?", Toast.LENGTH_LONG).show();
                } else {
                    topUpFood.setValue("true");
                    setNewDateTime(prevFoodTopUpDateTimeText, prevFoodTopUpDateTime);
                }
            }
        });
        topUpWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waterLevel.getText().toString().equals("Current water level: Sufficient")){
                    //to remind users and prevent overflowing
                    Toast.makeText(MainActivity.this, "Water level is sufficient, why top up?", Toast.LENGTH_LONG).show();
                } else {
                    topUpWater.setValue("true");
                    setNewDateTime(prevWaterTopUpDateTimeText, prevWaterTopUpDateTime);
                }
            }
        });
    }
    //end of onCreate


    //Create a method stub for the sendNotification() method:
    private void sendNotification(int NOTIFICATION_ID) {
        if (NOTIFICATION_ID == WATER_NOTIFICATION_ID) {
            NotificationCompat.Builder notifyBuilder = getWaterNotificationBuilder();
            mWaterNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        } else if (NOTIFICATION_ID == FOOD_NOTIFICATION_ID) {
            NotificationCompat.Builder notifyBuilder = getFoodNotificationBuilder();
            mFoodNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        }
    }

    private void createFoodNotificationChannel() {
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
    private void createWaterNotificationChannel()
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

    private void setNewDateTime(TextView textView, DatabaseReference referenceToSet){
        //to change the textview for new date and time and update firebase
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String prevTopUp = "Topped up at: " + sdf.format(Calendar.getInstance().getTime());
        referenceToSet.setValue(prevTopUp);
        textView.setText(prevTopUp);
    }



}
