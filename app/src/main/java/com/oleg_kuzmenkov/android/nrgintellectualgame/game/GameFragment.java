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

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.RepositoryImpl;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.util.HashMap;
import java.util.List;

public class GameFragment extends Fragment implements GameScreenView, View.OnClickListener {
    private static final String LOG_TAG = "GAME_FRAGMENT";
    private static final String BUNDLE_CONTENT = "BUNDLE";
    private static final float VOLUME = 0.01f;

    private TextView mQuestionTimer;
    private TextView mQuestionText;
    private HashMap<Integer, Button> mGameButtonsMap;

    private MediaPlayer mRightAnswerPlayer;
    private MediaPlayer mWrongAnswerPlayer;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mLocationClient;

    private GameScreenPresenter mPresenter;

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

        initMedia();
        initControls(v);

        if (savedInstanceState == null) {
            // start new game
            initPresenter();
            mPresenter.startGame();
        } else {
            // restore game
            restorePresenter(savedInstanceState);
            mPresenter.restoreQuestion();
        }

        return v;
    }

    /**
     * Create the Location callback
     */
    private void createLocationCallback() {
        Log.d(LOG_TAG, "createLocationCallback");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(LOG_TAG, "OnLocationResult from Service");
                //set location result
                setLocationResult(locationResult);
            }
        };
    }

    /**
     * Create the Location request
     */
    private void createLocationRequest() {
        Log.d(LOG_TAG, "createLocationRequest");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Set user's location
     */
    private void setLocationResult(final LocationResult locationResult) {
        Log.d(LOG_TAG, "OnLocationResult");
        List<Location> locationList;

        if (locationResult != null) {
            locationList = locationResult.getLocations();
        } else {
            return;
        }

        if (locationList.size() > 0) {
            //the last location in the list is the newest
            Location currentLocation = locationList.get(locationList.size() - 1);
            Log.d(LOG_TAG, "Location latitude - " + currentLocation.getLatitude());
            Log.d(LOG_TAG, "Location longitude - " + currentLocation.getLongitude());
            mPresenter.setUserLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
    }

    @Override
    public void startLocationService() {
        Log.d(LOG_TAG, "startGettingUserLocation");
        createLocationCallback();
        createLocationRequest();
        mLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                // Send Message to User
                Log.d(LOG_TAG, "Geolocation is disabled");
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

    @Override
    public void stopLocationService() {
        Log.d(LOG_TAG, "stopGettingUserLocation");
        mLocationClient.removeLocationUpdates(mLocationCallback);
        mLocationClient = null;
    }

    @Override
    public void displayQuestion(final Question question) {
        Log.d(LOG_TAG, "displayQuestion");
        //mAnswerIsDone = false;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionText.setText(question.getQuestionText());
                int index = 0;
                for (Button button : mGameButtonsMap.values()) {
                    button.setText(question.getAnswersList().get(index));
                    index++;
                }
            }
        });
    }

    @Override
    public void displayResultsOfGame(int rightAnswersCount) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = GameResultsFragment.newInstance(rightAnswersCount);
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Nullable
    private Button getAppropriateButton(String buttonText) {
        for (Button button : mGameButtonsMap.values()) {
            if (button.getText().toString().equals(buttonText)) {
                return button;
            }
        }

        return null;
    }

    @Override
    public void displayRightAnswerResult(final String rightAnswer) {
        getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
        mRightAnswerPlayer.start();
    }

    @Override
    public void displayWrongAnswerResult(String rightAnswer, String wrongAnswer) {
        getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
        getAppropriateButton(wrongAnswer).setBackgroundResource(R.drawable.wrong_answer_button_border);
        mWrongAnswerPlayer.start();
    }

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

    @Override
    public void clearButtons() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Button button : mGameButtonsMap.values()) {
                    button.setBackgroundResource(R.drawable.answer_button_border);;
                }
            }
        });
    }

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

    @Override
    public void setGreenTimeIndicator() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionTimer.setBackgroundResource(R.drawable.time_green_indicator);
            }
        });
    }

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
        Log.d(LOG_TAG, "Fragment: onDestroy");
        if (mLocationClient != null) {
            mLocationClient.removeLocationUpdates(mLocationCallback);
        }
        mPresenter.detach();
        super.onDestroy();
    }

    private void initMedia() {
        mRightAnswerPlayer = MediaPlayer.create(getActivity(), R.raw.right_answer_sound);
        mRightAnswerPlayer.setVolume(VOLUME, VOLUME);
        mWrongAnswerPlayer = MediaPlayer.create(getActivity(), R.raw.wrong_answer_sound);
        mWrongAnswerPlayer.setVolume(VOLUME, VOLUME);
    }

    private void initControls(@NonNull View view) {
        mQuestionTimer = view.findViewById(R.id.timer_view);
        mQuestionText = view.findViewById(R.id.question_text_view);

        mGameButtonsMap = new HashMap();
        mGameButtonsMap.put(R.id.first_answer_button, (Button) view.findViewById(R.id.first_answer_button));
        mGameButtonsMap.put(R.id.second_answer_button, (Button) view.findViewById(R.id.second_answer_button));
        mGameButtonsMap.put(R.id.third_answer_button, (Button) view.findViewById(R.id.third_answer_button));
        mGameButtonsMap.put(R.id.fourth_answer_button, (Button) view.findViewById(R.id.fourth_answer_button));

        for (Button button : mGameButtonsMap.values()) {
            button.setOnClickListener(this);
        }
    }

    private void initPresenter() {
        mPresenter = new GameScreenPresenter(RepositoryImpl.get(getActivity().getApplicationContext()));
        mPresenter.setView(this);
        Bundle bundle = getArguments();

        if (bundle != null && bundle.containsKey(BUNDLE_CONTENT)) {
            mPresenter.setUser((User) bundle.getSerializable(BUNDLE_CONTENT));
            mPresenter.getUserLocation();
        }
    }

    private void restorePresenter(@NonNull Bundle savedInstanceState) {
        mPresenter = (GameScreenPresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
        mPresenter.setView(this);
        mPresenter.setRepository(RepositoryImpl.get(getActivity().getApplicationContext()));
        mPresenter.getUserLocation();
    }
}

