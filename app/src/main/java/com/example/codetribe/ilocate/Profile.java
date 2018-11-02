package com.example.codetribe.ilocate;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Profile extends AppCompatActivity {

    TextView location, username, email;
    Button message, friends, likes;

    static String city, state, country, address, key;
    double lat, lng;
    List<Address> addresses;
    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference databaseReference;
    userInformation user = new userInformation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        location = (TextView) findViewById(R.id.location);
        email = (TextView) findViewById(R.id.email);
        username = (TextView) findViewById(R.id.username);

        friends = (Button) findViewById(R.id.friends);
        likes = (Button) findViewById(R.id.likes);
        message = (Button) findViewById(R.id.message);


        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        System.out.println(key);

        //reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final userInformation person = dataSnapshot.getValue(userInformation.class);

                //User Last Seen Address
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                lat = person.getUserLat();
                lng = person.getUserLong();
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                    address = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    String zip = addresses.get(0).getPostalCode();
                    country = addresses.get(0).getCountryName();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                location.setText(address);
                email.setText(person.getEmail().toString());
                username.setText(person.getUsername().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
