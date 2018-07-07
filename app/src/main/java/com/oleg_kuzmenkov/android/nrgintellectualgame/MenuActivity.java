package com.oleg_kuzmenkov.android.nrgintellectualgame;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements MenuScreenView{
    private static final String BUNDLE_CONTENT = "content";
    private static final String INTENT_CONTENT = "content";
    private final String LOG_TAG = "Message";

    private Button mSinglePlayerButton;
    private Button mStatisticsButton;
    private Button mBestPlayersButton;
    private Button mNewsButton;
    private Button mExitButton;
    private TextView mCurrentUserTextView;
    private MenuScreenPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentUserTextView = findViewById(R.id.current_user_text_view);

        mSinglePlayerButton = findViewById(R.id.single_player_button);
        mSinglePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.startGameActivity();
            }
        });

        mStatisticsButton = findViewById(R.id.statistics_button);
        mStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.startStatisticsActivity();
            }
        });

        mBestPlayersButton = findViewById(R.id.best_players_button);
        mBestPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.startBestPlayersActivity();
            }
        });

        mNewsButton = findViewById(R.id.news_button);
        mNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.startNewsActivity();
            }
        });

        mExitButton = findViewById(R.id.exit_button);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //get Data From intent
        Intent intent = getIntent();
        String userLogin = intent.getStringExtra(INTENT_CONTENT );
        Log.d(LOG_TAG, "UserLogin - "+userLogin);

        if(savedInstanceState == null){
            mPresenter = new MenuScreenPresenter();
        } else{
            mPresenter = (MenuScreenPresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
        }
        // Set View and Repository in presenter
        mPresenter.setView(this);
        mPresenter.setRepository(RepositoryImpl.get(this));
        //get User Data
        mPresenter.checkUsers(userLogin);

        if(checkPermission() == false) {
            startRequestForPermission();
        }
    }

    @Override
    public void hideMenu() {
        mSinglePlayerButton.setVisibility(View.GONE);
        mStatisticsButton.setVisibility(View.GONE);
        mBestPlayersButton.setVisibility(View.GONE);
        mNewsButton.setVisibility(View.GONE);
        mExitButton.setVisibility(View.GONE);
    }

    @Override
    public void displayMenu() {
        mSinglePlayerButton.setVisibility(View.VISIBLE);
        mStatisticsButton.setVisibility(View.VISIBLE);
        mBestPlayersButton.setVisibility(View.VISIBLE);
        mNewsButton.setVisibility(View.VISIBLE);
        mExitButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayUserLogin(User user) {
        mCurrentUserTextView.setText("Login:"+user.getUserLogin());
    }

    @Override
    public void startGameActivity(User user) {
        Intent startGameIntent = new Intent(getApplicationContext(), GameActivity.class);
        startGameIntent.putExtra("1",user);
        startActivity(startGameIntent);
    }

    @Override
    public void startStatisticsActivity(User user) {
        Intent startStatisticsIntent = new Intent(getApplicationContext(), StatisticsActivity.class);
        startStatisticsIntent.putExtra("1",user);
        startActivity(startStatisticsIntent);
    }

    @Override
    public void startBestPlayersActivity(List<User> listBestPlayers) {
        Intent startBestPlayersActivityIntent = new Intent(getApplicationContext(), BestPlayersActivity.class);
        startBestPlayersActivityIntent.putExtra("1",(Serializable) listBestPlayers);
        startActivity(startBestPlayersActivityIntent);
    }

    @Override
    public void startNewsActivity() {
        Intent startNewsActivityIntent = new Intent(getApplicationContext(), NewsActivity.class);
        startActivity(startNewsActivityIntent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(R.drawable.exit_icon)
                .setTitle("Closing the game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "MenuActivity:onSaveInstanceState");
        outState.putSerializable(BUNDLE_CONTENT,mPresenter);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(LOG_TAG, "Permission Granted");
                } else {
                    // destroy Activity
                    Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),UserLoginActivity.class);
                    startActivity(intent);
                    finish();

                }
                break;

            default:
                break;
        }
    }

    /**
     * Check permission
     */
    private boolean checkPermission(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //location Permission already granted
                return true;
            } else {
                return false;
            }
        } else {
            //location Permission already granted
            return true;
        }
    }

    /**
     * Request permission
     */
    private void startRequestForPermission () {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}
