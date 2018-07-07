package com.oleg_kuzmenkov.android.nrgintellectualgame;

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

import java.util.List;

public class BestPlayersActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String INTENT_CONTENT = "1";
    private final String LOG_TAG = "Message";

    private GoogleMap mGoogleMap;;
    private MapFragment mMapFragment;
    List<User> mBestPlayersList;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_players);

        //get Data From intent
        Intent intent = getIntent();
        mBestPlayersList = (List<User>) intent.getSerializableExtra(INTENT_CONTENT );

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
        Log.d(LOG_TAG, "OnMapReady");
        Log.d(LOG_TAG,"Best players count = "+mBestPlayersList.size());
        for(int i = 0; i < mBestPlayersList.size();i++){
            LatLng userPlace = new LatLng(mBestPlayersList.get(i).getLatitude(), mBestPlayersList.get(i).getLongitude());
            mGoogleMap.addMarker(new MarkerOptions().position(userPlace).title("Very good player"));
        }
    }
}
