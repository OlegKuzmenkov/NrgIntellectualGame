package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Question;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.GameData;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.util.HashMap;
import java.util.List;

public class GameFragment extends Fragment implements GameView, View.OnClickListener {
    private static final String LOG_TAG = "GAME_FRAGMENT";
    private static final String BUNDLE_CONTENT = "BUNDLE";
    private static final float VOLUME = 0.05f;

    private TextView mQuestionTimer;
    private TextView mQuestionText;
    private HashMap<Integer, Button> mGameButtonsMap;

    private MediaPlayer mRightAnswerPlayer;
    private MediaPlayer mWrongAnswerPlayer;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mLocationClient;

    private GamePresenter mPresenter;

    public static GameFragment newInstance(final User user) {
        GameFragment fragment = new GameFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BUNDLE_CONTENT, user);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_game, container, false);

        mRightAnswerPlayer = initMedia(R.raw.right_answer_sound);
        mWrongAnswerPlayer = initMedia(R.raw.wrong_answer_sound);
        initControls(v);

        setupPresenter(savedInstanceState);

        if (savedInstanceState == null) {
            // start new game
            mPresenter.startGame();
        } else {
            // restore game
            mPresenter.restoreGame();
        }

        return v;
    }

    /**
     * Create the Location callback
     */
    private void createLocationCallback() {
        Log.i(LOG_TAG, "createLocationCallback");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //set location result
                setLocationResult(locationResult);
            }
        };
    }

    /**
     * Create the Location request
     */
    private void createLocationRequest() {
        Log.i(LOG_TAG, "createLocationRequest");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Set user's location
     */
    private void setLocationResult(final LocationResult locationResult) {
        if (locationResult != null) {
            Location userLocation = locationResult.getLastLocation();
            mPresenter.setUserLocation(userLocation.getLatitude(), userLocation.getLongitude());
        }
    }

    /**
     * Start the location service
     */
    @Override
    public void startLocationService() {
        createLocationCallback();
        createLocationRequest();
        mLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //location permission already granted
                mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                //location permission is not granted
                Log.i(LOG_TAG, "Geolocation is disabled");
            }

        } else {
            mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    @Override
    public void onClick(final View view) {
        Button button = mGameButtonsMap.get(view.getId());

        if (button != null) {
            mPresenter.checkAnswer(button.getText().toString());
        }
    }

    /**
     * Stop the location service
     */
    @Override
    public void stopLocationService() {
        if (mLocationClient != null) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     * Display question on the screen
     */
    @Override
    public void displayQuestion(final Question question) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                List<String> answersList = question.getAnswersList();

                for (Button button : mGameButtonsMap.values()) {
                    button.setText(answersList.get(index));
                    index++;
                }

                mQuestionText.setText(question.getQuestionText());
            }
        });
    }

    /**
     * Display last game results
     */
    @Override
    public void displayResultsOfGame(int rightAnswersCount) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = GameResultsFragment.newInstance(rightAnswersCount);
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    /**
     * Find the appropriate button
     */
    @Nullable
    private Button getAppropriateButton(String buttonText) {
        for (Button button : mGameButtonsMap.values()) {
            if (button.getText().toString().equals(buttonText)) {
                return button;
            }
        }

        return null;
    }

    /**
     * Display results after right answer from player
     */
    @Override
    public void displayRightAnswerResult(final String rightAnswer) {
        getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
        mRightAnswerPlayer.start();
    }

    /**
     * Display results after wrong answer from player
     */
    @Override
    public void displayWrongAnswerResult(String rightAnswer, String wrongAnswer) {
        getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
        getAppropriateButton(wrongAnswer).setBackgroundResource(R.drawable.wrong_answer_button_border);
        mWrongAnswerPlayer.start();
    }

    /**
     * Display results when the user did not answer the question
     */
    @Override
    public void displayRightAnswer(final String rightAnswer) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
                mWrongAnswerPlayer.start();
            }
        });
    }

    /**
     * Enable or disable game buttons depending on the parameter
     */
    @Override
    public void enableAnswerButtons(final boolean isEnable) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Button button : mGameButtonsMap.values()) {
                    button.setClickable(isEnable);
                }
            }
        });
    }

    /**
     * Refresh game buttons between questions
     */
    @Override
    public void clearButtons() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Button button : mGameButtonsMap.values()) {
                    button.setBackgroundResource(R.drawable.answer_button_border);
                }
            }
        });
    }

    /**
     * Show question's remain time for answering on the screen
     */
    @Override
    public void setQuestionRemainTime(final int remainTime) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionTimer.setText(Integer.toString(remainTime));
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull final  Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_CONTENT, mPresenter);
    }

    /**
     * Show green question's remain time indicator for answering on the screen
     */
    @Override
    public void setGreenTimeIndicator() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionTimer.setBackgroundResource(R.drawable.time_green_indicator);
            }
        });
    }

    /**
     * Show red question's remain time indicator for answering on the screen
     */
    @Override
    public void setRedTimeIndicator() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionTimer.setBackgroundResource(R.drawable.time_red_indicator);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "GameFragment: onDestroy");
        stopLocationService();
        mPresenter.detach();
        super.onDestroy();
    }

    /**
     * Initialize media player
     */
    private MediaPlayer initMedia(int id) {
        MediaPlayer media = MediaPlayer.create(getActivity(), id);
        media.setVolume(VOLUME, VOLUME);
        return media;
    }

    /**
     * Initialize all controls
     */
    private void initControls(@NonNull View view) {
        mQuestionTimer = view.findViewById(R.id.timer_view);
        mQuestionText = view.findViewById(R.id.question_text_view);

        mGameButtonsMap = new HashMap<>();
        mGameButtonsMap.put(R.id.first_answer_button, (Button) view.findViewById(R.id.first_answer_button));
        mGameButtonsMap.put(R.id.second_answer_button, (Button) view.findViewById(R.id.second_answer_button));
        mGameButtonsMap.put(R.id.third_answer_button, (Button) view.findViewById(R.id.third_answer_button));
        mGameButtonsMap.put(R.id.fourth_answer_button, (Button) view.findViewById(R.id.fourth_answer_button));

        for (Button button : mGameButtonsMap.values()) {
            button.setOnClickListener(this);
        }
    }

    /**
     * Setup presenter. Create or restore it.
     */
    private void setupPresenter(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            // create presenter
            mPresenter = new GamePresenter(GameData.get(getActivity().getApplicationContext()));
            Bundle bundle = getArguments();

            if (bundle != null && bundle.containsKey(BUNDLE_CONTENT)) {
                mPresenter.setUser((User) bundle.getSerializable(BUNDLE_CONTENT));
            }
        } else {
            // restore presenter
            mPresenter = (GamePresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
            mPresenter.setRepository(GameData.get(getActivity().getApplicationContext()));
        }

        mPresenter.setView(this);
        mPresenter.getUserLocation();
    }
}

