package com.example.codetribe.ilocate;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int ACCESS_COARSE_LOCATION = 0;
    int permistionChecker = 2;

    //Firebase
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    Get_Coordinates coordinates;
    Location mLocation;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        coordinates = new Get_Coordinates(this);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final userInformation info = dataSnapshot.getValue(userInformation.class);
                String userName = info.getUsername();
                String email = info.getEmail();
                Double userLat = coordinates.getLatitude();
                Double userLng = coordinates.getLongitude();

                info.setUserLong(userLng);
                info.setUserLat(userLat);
                info.setUsername(userName);
                info.setEmail(email);

                databaseReference.setValue(info);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MapsActivity.this, SignInActivity.class));
                    finish();
                }
            }
        };
    }
//FirebaseAuth.AuthStateListener()

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(MapsActivity.this,
                            "Location Permission Denied", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mLocation = coordinates.getLocation();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInformation userInfo = dataSnapshot.getValue(userInformation.class);
                // Add a marker in User's location, and move the camera.
                LatLng mLocation = new LatLng(userInfo.getUserLat(), userInfo.getUserLong());
                mMap.addMarker(new MarkerOptions().position(mLocation).title(userInfo.getEmail()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getOtherUser(mMap);
    }

    void getOtherUser(GoogleMap googleMap) {
        mMap = googleMap;

        final DatabaseReference mPostReference;
        mPostReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userLat;
                String userLng;
                String userEmail;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    System.out.println(databaseReference.getKey() + " : " + snapshot.getKey());
                    if (databaseReference.getKey().equals(snapshot.getKey())) {
                        System.out.println("current user");
                    } else {
                        userLat = String.valueOf(snapshot.child("userLat").getValue());
                        userLng = String.valueOf(snapshot.child("userLong").getValue());
                        userEmail = (String) snapshot.child("email").getValue();

                        Double mLat = Double.valueOf(userLat);
                        Double mLng = Double.valueOf(userLng);
                        LatLng mLocation = new LatLng(mLat, mLng);
                        mMap.addMarker(new MarkerOptions()
                                .position(mLocation)
                                .title(userEmail)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void signOut() { //sign out method
        mAuth.signOut();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:  //Logout
                signOut();
                return true;
            case R.id.get_loc:  //Get User Location
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
