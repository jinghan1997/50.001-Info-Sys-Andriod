package com.example.hamstercare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    // create inner classes for event listeners
    // we can't use anonymous classes because we need to reference non-final vars
    private class ImEvtListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String url = dataSnapshot.getValue(String.class);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference httpsRef = storage.getReferenceFromUrl(url);

            final ProgressBar yourHamsterProgBar = findViewById(R.id.yourHamsterProgBar);

            GlideApp.with(MainActivity.this)
                    .load(httpsRef)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            yourHamsterProgBar.setVisibility(View.GONE);
                            yourHamsterIm.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Failed to load image. Please try again later.", Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            yourHamsterProgBar.setVisibility(View.GONE);
                            yourHamsterIm.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(yourHamsterIm);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("mickey1356", "couldn't read database");
        }
    }

    Button topUpFoodButton;
    Button topUpWaterButton;
    TextView foodLevel;
    TextView waterLevel;
    TextView prevWaterTopUpDateTimeText;
    TextView prevFoodTopUpDateTimeText;

    Button showHamsterBtn;
    ImageView yourHamsterIm;
    //ImageView hamsterImage;

    String imUrl;

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
        createNotificationChannel(PRIMARY_FOOD_CHANNEL_ID);
        createNotificationChannel(PRIMARY_WATER_CHANNEL_ID);

        //find all the textviews and buttons
        topUpFoodButton = findViewById(R.id.topUpFoodButton);
        topUpWaterButton = findViewById(R.id.topUpWaterButton);
        foodLevel = findViewById(R.id.foodLevel);
        waterLevel = findViewById(R.id.waterLevel);
        prevWaterTopUpDateTimeText = findViewById(R.id.prevWaterTopUpDateTime);
        prevFoodTopUpDateTimeText = findViewById(R.id.prevFoodTopUpDateTime);
        //hamsterImage = findViewById(R.id.hamster);
        // for camera
        showHamsterBtn = findViewById(R.id.showHamsterBtn);
        yourHamsterIm = findViewById(R.id.yourHamsterIm);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference foodLow = database.getReference("foodLow");
        final DatabaseReference waterLow = database.getReference("waterLow");
        final DatabaseReference topUpWater = database.getReference("topUpWater");
        final DatabaseReference topUpFood = database.getReference("topUpFood");
        final DatabaseReference prevFoodTopUpDateTime = database.getReference("prevFoodTopUpDateTime");
        final DatabaseReference prevWaterTopUpDateTime = database.getReference("prevWaterTopUpDateTime");
        //foodLow.setValue("false");

        // initialise yourHamsterIm to last uploaded image
        final DatabaseReference picUrl = database.getReference("picUrl");

        picUrl.addValueEventListener(new ImEvtListener());


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
                    sendNotification(WATER_NOTIFICATION_ID);
                } else if (LowWaterValue.equals("false")){
                    //cancel notification
                    mWaterNotifyManager.cancel(WATER_NOTIFICATION_ID);
                    waterLevel.setText("Current water level: Sufficient");
                }
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
        showHamsterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = db.getReference("takePic");
                dbRef.setValue("true");
                findViewById(R.id.yourHamsterProgBar).setVisibility(View.VISIBLE);
                findViewById(R.id.yourHamsterIm).setVisibility(View.GONE);
            }
        });
    }
    //end of onCreate


    //Create a method stub for the sendNotification() method:
    private void sendNotification(int NOTIFICATION_ID) {
        if (NOTIFICATION_ID == WATER_NOTIFICATION_ID) {
            //NotificationCompat.Builder notifyBuilder = getWaterNotificationBuilder();
            NotificationCompat.Builder notifyBuilder = getNotificationBuilder(WATER_NOTIFICATION_ID);
            mWaterNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        } else if (NOTIFICATION_ID == FOOD_NOTIFICATION_ID) {
            //NotificationCompat.Builder notifyBuilder = getFoodNotificationBuilder();
            NotificationCompat.Builder notifyBuilder = getNotificationBuilder(FOOD_NOTIFICATION_ID);
            mFoodNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        }
    }

    private void createNotificationChannel(String PRIMARY_CHANNEL_ID)
    {
        if (PRIMARY_CHANNEL_ID.equals(PRIMARY_WATER_CHANNEL_ID)) {
            mWaterNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        } else {
            mFoodNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            if (PRIMARY_CHANNEL_ID.equals(PRIMARY_WATER_CHANNEL_ID)) {
                mWaterNotifyManager.createNotificationChannel(notificationChannel);
            } else {
                mFoodNotifyManager.createNotificationChannel(notificationChannel);
            }
        }
    }


    private NotificationCompat.Builder getNotificationBuilder(int NOTIFICATION_ID){
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String primaryChannelId = PRIMARY_FOOD_CHANNEL_ID;
        String contentTitle =  "Food is Low";
        String contentText = "Food is low, please top up!";

        if (NOTIFICATION_ID==WATER_NOTIFICATION_ID) {
            primaryChannelId = PRIMARY_WATER_CHANNEL_ID;
            contentTitle =  "Water is Low";
            contentText = "Water is low, please top up!";
        }
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, primaryChannelId)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_pet)
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
