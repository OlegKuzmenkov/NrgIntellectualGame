package com.oleg_kuzmenkov.android.nrgintellectualgame;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            Intent intent = getIntent();
            User user = (User)intent.getSerializableExtra("1");
            fragment = GameFragment.newInstance(user);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
