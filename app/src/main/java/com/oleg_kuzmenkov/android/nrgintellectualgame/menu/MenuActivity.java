package com.oleg_kuzmenkov.android.nrgintellectualgame.menu;

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

import com.oleg_kuzmenkov.android.nrgintellectualgame.news.NewsListActivity;
import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.GameData;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;
import com.oleg_kuzmenkov.android.nrgintellectualgame.statistics.StatisticsActivity;
import com.oleg_kuzmenkov.android.nrgintellectualgame.statistics.BestPlayersActivity;
import com.oleg_kuzmenkov.android.nrgintellectualgame.game.GameActivity;
import com.oleg_kuzmenkov.android.nrgintellectualgame.signin.SignInActivity;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements MenuView, View.OnClickListener {
    public static final String INTENT_CONTENT = "INTENT_CONTENT";
    private static final String BUNDLE_CONTENT = "BUNDLE_CONTENT";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String LOG_TAG = "MENU_ACTIVITY";

    private MenuPresenter mPresenter;

    private TextView mCurrentUser;
    private LinkedHashMap<Integer, Button> mMenuButtonsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initControls();

        setupPresenter(savedInstanceState);

        //get data from intent
        String userLogin = getIntent().getStringExtra(SignInActivity.INTENT_CONTENT);
        //get user data
        mPresenter.getUserData(userLogin);

        checkPermission();

    }

    /**
     * Enable or disable game menu depending on the parameter
     */
    @Override
    public void enableMenu(boolean isEnable) {
        int state = isEnable ? View.VISIBLE : View.GONE;

        for (Button button : mMenuButtonsMap.values()) {
            button.setVisibility(state);
        }
    }

    /**
     * Display login of current user
     */
    @Override
    public void displayUserLogin(String userLogin) {
        mCurrentUser.setText(String.format("Login:%s ", userLogin));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.single_player_button:
                mPresenter.onClickSinglePlayerButton();
                break;

            case R.id.statistics_button:
                mPresenter.onClickStatisticsButton();
                break;

            case R.id.best_players_button:
                mPresenter.onClickBestPlayersButton();
                break;

            case R.id.news_button:
                mPresenter.onClickReadNewsButton();
                break;

            case R.id.exit_button:
                finish();
                break;

            default:
                Log.i(LOG_TAG, "onClick() from unknown view");
                break;
        }
    }

    /**
     * Start new game
     */
    @Override
    public void startGameActivity(User user) {
        Intent startGameIntent = new Intent(getApplicationContext(), GameActivity.class);
        startGameIntent.putExtra(INTENT_CONTENT, user);
        startActivity(startGameIntent);
    }

    /**
     * Show statistics of current user
     */
    @Override
    public void startStatisticsActivity(User user) {
        Intent startStatisticsIntent = new Intent(getApplicationContext(), StatisticsActivity.class);
        startStatisticsIntent.putExtra(INTENT_CONTENT, user);
        startActivity(startStatisticsIntent);
    }

    /**
     * Show location of the best players
     */
    @Override
    public void startBestPlayersActivity(List<User> listBestPlayers) {
        Intent startBestPlayersActivityIntent = new Intent(getApplicationContext(), BestPlayersActivity.class);
        startBestPlayersActivityIntent.putExtra(INTENT_CONTENT, (Serializable) listBestPlayers);
        startActivity(startBestPlayersActivityIntent);
    }

    /**
     * Show last available news
     */
    @Override
    public void startNewsActivity() {
        Intent startNewsActivityIntent = new Intent(getApplicationContext(), NewsListActivity.class);
        startActivity(startNewsActivityIntent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(R.drawable.exit_icon)
                .setTitle("Closing the game")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
        outState.putSerializable(BUNDLE_CONTENT, mPresenter);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;

            default:
                break;
        }
    }

    /**
     * Check user permission
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.ACCESS_FINE_LOCATION;

            if (ContextCompat.checkSelfPermission(this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                //permission not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{permission}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * Initialize all controls
     */
    private void initControls() {
        mCurrentUser = findViewById(R.id.current_user_text_view);

        mMenuButtonsMap = new LinkedHashMap<>();
        mMenuButtonsMap.put(R.id.single_player_button, (Button) findViewById(R.id.single_player_button));
        mMenuButtonsMap.put(R.id.statistics_button, (Button) findViewById(R.id.statistics_button));
        mMenuButtonsMap.put(R.id.best_players_button, (Button) findViewById(R.id.best_players_button));
        mMenuButtonsMap.put(R.id.news_button, (Button) findViewById(R.id.news_button));
        mMenuButtonsMap.put(R.id.exit_button, (Button) findViewById(R.id.exit_button));

        for (Button button : mMenuButtonsMap.values()) {
            button.setOnClickListener(this);
        }
    }

    /**
     * Setup presenter. Create or restore it.
     */
    private void setupPresenter(final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // create presenter
            mPresenter = new MenuPresenter();
        } else {
            // restore presenter
            mPresenter = (MenuPresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
        }

        mPresenter.setView(this);
        mPresenter.setRepository(GameData.get(this));
    }
}
