package com.example.norman_lee.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonConvert;
    Button buttonSetExchangeRate;
    EditText editTextValue;
    TextView textViewResult;
    TextView textViewExchangeRate;
    double exchangeRate;
    public final String TAG = "Logcat";
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.mainsharedprefs";
    public static final String RATE_KEY = "Rate_Key";
    ExchangeRate exchangeRateCalculation;
    String exchangeRateStored;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 4.5 Get a reference to the sharedPreferences object
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        //TODO 4.6 Retrieve the value using the key, and set a default when there is none
        String defaultValue = getResources().getString(R.string.default_exchange_rate);
        exchangeRateStored = mPreferences.getString(RATE_KEY, defaultValue);

        //App: data on exchange Rate:
        // 1. sharedPreferences
        // 2. SubActivity via intent
        //TODO modify 3.13 below

        //TODO 3.13 Get the intent, retrieve the values passed to it, and instantiate the ExchangeRate class
        //TODO 3.13a See ExchangeRate class --->

        // 1) App is started for the first time
        // 2) App is in use, and user has just used SubActivity
        Intent intentSubToMain = getIntent();

        String home = intentSubToMain.getStringExtra(SubActivity.HOME_KEY);
        String foreign = intentSubToMain.getStringExtra(SubActivity.FOREIGN_KEY);

        if (home == null && foreign == null){
            // 1) App is started for the first time
            //use default exchange rate
            //exchangeRateCalculation = new ExchangeRate();
            exchangeRateCalculation = new ExchangeRate(exchangeRateStored);
        } else {
            // 2) App is in use, and user has just used SubActivity
            exchangeRateCalculation = new ExchangeRate(home, foreign);
        }
        //TODO next, modify TODO 2.2 and 2.3



        //TODO 2.1 Use findViewById to get references to the widgets in the layout
        editTextValue = findViewById(R.id.editTextValue);
        textViewResult = findViewById(R.id.textViewResult);
        buttonConvert = findViewById(R.id.buttonConvert);
        textViewExchangeRate = findViewById(R.id.textViewExchangeRate);
        //TODO 2.2 Assign a default exchange rate of 2.95 to the textView
        //textViewExchangeRate.setText(R.string.default_exchange_rate);
        textViewExchangeRate.setText(exchangeRateCalculation.getExchangeRate().toString());
        //TODO 2.3 Set up setOnClickListener for the Convert Button
        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextValue.getText().toString().equals("")){//all widgets have their own getter and setter
                    Log.i("jinghan","Edit text is empty");
                    // toast is a message that pop up a few seconds then disappear
                    // context is the super super class of main activity
                    // 3 inputs: context, message, duration
                    Toast.makeText(MainActivity.this, "Please enter a value", Toast.LENGTH_LONG).show();
                    //pops up when user click button but no text
                } else {
                    //ExchangeRate exchangeRate = new ExchangeRate(); //instantiate exchange rate class
                    //textViewExchangeRate.setText(exchangeRate.getExchangeRate().toString());
                    String foreignValue = editTextValue.getText().toString(); //get edittextvalue
                    String result = exchangeRateCalculation.calculateAmount(foreignValue).toString(); //calculate amount returns bigdecimal
                    // display in the widget: value
                    textViewResult.setText(result);
                }
            }
        });
        //TODO 2.4 Display a Toast & Logcat message if the editTextValue widget contains an empty string
        //TODO 2.5 If not, calculate the units of B with the exchange rate and display it
        //TODO 2.5a See ExchangeRate class --->


        //TODO 3.1 Modify the Android Manifest to specify that the parent of SubActivity is MainActivity
        //TODO 3.2 Get a reference to the Set Exchange Rate Button
        buttonSetExchangeRate=findViewById(R.id.buttonSetExchangeRate);
        //TODO 3.3 Set up setOnClickListener for this
        buttonSetExchangeRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                startActivity(intent);
            }
        });
        //TODO 3.4 Write an Explicit Intent to get to SubActivity


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Jinghan", "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("jinghan", "onResume()");
    }

    @Override
    protected void onPause() {
        //TODO 4.7 In onPause, get a reference to the SharedPreferences.Editor object
        //TODO 4.8 store the exchange rate using the putString method with a key
        super.onPause();
        Log.i("jinghan", "onPause()");

        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(RATE_KEY, exchangeRateCalculation.getExchangeRate().toString());
        editor.apply();

        //go back to 4.6 in onCreate
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("jinghan", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("jinghan", "onDestroy()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //TODO 4.1 Go to res/menu/menu_main.xml and add a menu item Set Exchange Rate
    //TODO 4.2 In onOptionsItemSelected, add a new if-statement and code accordingly

    //TODO 5.1 Go to res/menu/menu_main.xml and add a menu item Open Map App
    //TODO 5.2 In onOptionsItemSelected, add a new if-statement
    //TODO 5.3 code the Uri object and set up the intent

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menuSetExchangeRate) {
            Intent intent = new Intent(this, SubActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menuOpenApp) {
            //build the URI
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("geo")
            .opaquePart("0.0")
            .appendQueryParameter("q", "Changi Airport");

            Uri uriGeoLocation = builder.build();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uriGeoLocation);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO 4.3 override the methods in the Android Activity Lifecycle here
    //TODO 4.4 for each of them, write a suitable string to display in the Logcat



}
