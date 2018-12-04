package com.oleg_kuzmenkov.android.nrgintellectualgame.statistics;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.menu.MenuActivity;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.util.List;

public class BestPlayersActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String LOG_TAG = "BEST_PLAYER_ACTIVITY";

    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;
    private List<User> mBestPlayersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_players);

        //get Data From intent
        Intent intent = getIntent();
        mBestPlayersList = (List<User>) intent.getSerializableExtra(MenuActivity.INTENT_CONTENT);

        // create dynamic MapFragment
        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        Log.i(LOG_TAG, "OnMapReady");

        for (User user : mBestPlayersList) {
            LatLng userPlace = new LatLng(user.getLatitude(), user.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions().position(userPlace).title("Very good player"));
        }
    }
}
