package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;


//TODO 1.1 Put in some images in the drawables folder
//TODO 1.2 Go to activity_main.xml and modify the layout

public class MainActivity extends AppCompatActivity {

    //TODO 1.2 Instance variables are declared for you, please import the libraries
    ArrayList<Integer> images;
    Button charaButton;
    Button charaButtonReset;
    ImageView charaImage;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO 1.3 Instantiate An ArrayList object
        //TODO 1.4 Add the image IDs to the ArrayList
        images = new ArrayList<>();
        images.add(R.drawable.eevee);
        images.add(R.drawable.bulbasaur);
        images.add(R.drawable.gyrados);
        images.add(R.drawable.psyduck);
        images.add(R.drawable.snorlax);
        images.add(R.drawable.spearow);

        //TODO 1.5 Get references to the charaButton and charaImage widgets using findViewById
        charaImage = findViewById(R.id.charaImage);
        charaButton = findViewById(R.id.charaButton);
        charaButtonReset = findViewById(R.id.charaButtonReset);


        //TODO 1.6 For charaButton, invoke the setOnClickListener method
        //TODO 1.7 Create an anonymous class which implements View.OnClickListener interface
        charaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                //get a random index inside the size of images
                int index = r.nextInt(images.size());
                //extract the image ID randomly
                int imageID = images.get(index);
                charaImage.setImageResource(imageID);
                Log.i("jinghan", "button is clicked");
            }
        });
        //TODO 1.8 Within onClick, write code to randomly select an image ID from the ArrayList and display it in the ImageView
        //TODO 1.9 [On your own] Create another button, which when clicked, will cause one image to always be displayed


        charaButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;
                //extract the image ID randomly
                int imageID = images.get(index);
                charaImage.setImageResource(imageID);

            }
        });

    }
}