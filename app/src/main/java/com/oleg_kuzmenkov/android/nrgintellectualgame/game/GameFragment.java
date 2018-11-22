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

import java.util.List;

public class GameFragment extends Fragment implements GameScreenView, View.OnClickListener {
    private static final String LOG_TAG = "Message";
    private static final String BUNDLE_CONTENT = "content";
    private static final float VOLUME = 0.01f;

    private TextView mQuestionTimerTextView;
    private TextView mQuestionTextView;
    private Button mFirstAnswerButton;
    private Button mSecondAnswerButton;
    private Button mThirdAnswerButton;
    private Button mFourthAnswerButton;

    private MediaPlayer mMediaPlayerForRightAnswer;
    private MediaPlayer mMediaPlayerForWrongAnswer;

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private GameScreenPresenter mPresenter;

    public static GameFragment newInstance(final User user) {
        GameFragment fragment = new GameFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BUNDLE_CONTENT, user);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_game, container, false);

        initMediaPlayer();

        mQuestionTimerTextView = v.findViewById(R.id.timer_view);
        mQuestionTextView = v.findViewById(R.id.question_text_view);

        mFirstAnswerButton = v.findViewById(R.id.first_answer_button);
        mFirstAnswerButton.setOnClickListener(this);
        mSecondAnswerButton = v.findViewById(R.id.second_answer_button);
        mSecondAnswerButton.setOnClickListener(this);
        mThirdAnswerButton = v.findViewById(R.id.third_answer_button);
        mThirdAnswerButton.setOnClickListener(this);
        mFourthAnswerButton = v.findViewById(R.id.fourth_answer_button);
        mFourthAnswerButton.setOnClickListener(this);

        if (savedInstanceState == null) {
            // start new game
            mPresenter = new GameScreenPresenter(RepositoryImpl.get(getActivity().getApplicationContext()));
            mPresenter.setView(this);
            if (getArguments() != null && getArguments().containsKey(BUNDLE_CONTENT)) {
                User user = (User) getArguments().getSerializable(BUNDLE_CONTENT);
                mPresenter.setUser(user);
                mPresenter.checkIsExistUserLocation();
                Log.d(LOG_TAG, "User login" + user.getUserLogin());
            }

            mPresenter.onClickSinglePlayerButton();
        } else {
            // restore state
            mPresenter = (GameScreenPresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
            restoreState();
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
    public void startGettingUserLocation() {
        Log.d(LOG_TAG, "startGettingUserLocation");
        createLocationCallback();
        createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                // Send Message to User
                Log.d(LOG_TAG, "Geolocation is disabled");
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.first_answer_button:
                mPresenter.checkAnswer(mFirstAnswerButton.getText().toString());
                break;

            case R.id.second_answer_button:
                mPresenter.checkAnswer(mSecondAnswerButton.getText().toString());
                break;

            case R.id.third_answer_button:
                mPresenter.checkAnswer(mThirdAnswerButton.getText().toString());
                break;

            case R.id.fourth_answer_button:
                mPresenter.checkAnswer(mFourthAnswerButton.getText().toString());
                break;
        }
    }

    @Override
    public void stopGettingUserLocation() {
        Log.d(LOG_TAG, "stopGettingUserLocation");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mFusedLocationClient = null;
    }

    @Override
    public void displayQuestion(final Question question) {
        Log.d(LOG_TAG, "displayQuestion");
        //mAnswerIsDone = false;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionTextView.setText(question.getQuestionText());
                mFirstAnswerButton.setText(question.getFirstCaseAnswer());
                mSecondAnswerButton.setText(question.getSecondCaseAnswer());
                mThirdAnswerButton.setText(question.getThirdCaseAnswer());
                mFourthAnswerButton.setText(question.getFourthCaseAnswer());
            }
        });
        //mRightAnswerButton = identifyCorrectAnswerButton(mRightAnswer);
        //startTimerForQuestion(timer);
    }

    @Override
    public void displayResultsOfGame(int countOfQuestions, int countWriteAnswers) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = GameResultsFragment.newInstance(countOfQuestions, countWriteAnswers);
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private Button getAppropriateButton(String buttonText) {
        if (mFirstAnswerButton.getText().toString().equals(buttonText)) {
            return mFirstAnswerButton;
        }
        if (mSecondAnswerButton.getText().toString().equals(buttonText)) {
            return mSecondAnswerButton;
        }
        if (mThirdAnswerButton.getText().toString().equals(buttonText)) {
            return mThirdAnswerButton;
        }
        return mFourthAnswerButton;
    }

    @Override
    public void displayRightAnswerResult(final String rightAnswer) {
        getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
        mMediaPlayerForRightAnswer.start();
    }

    @Override
    public void displayWrongAnswerResult(String rightAnswer, String wrongAnswer) {
        getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
        getAppropriateButton(wrongAnswer).setBackgroundResource(R.drawable.wrong_answer_button_border);
        mMediaPlayerForWrongAnswer.start();
    }

    @Override
    public void displayRightAnswer(final String rightAnswer) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getAppropriateButton(rightAnswer).setBackgroundResource(R.drawable.right_answer_button_border);
                mMediaPlayerForWrongAnswer.start();
            }
        });
    }

    @Override
    public void enableAnswerButtons(final boolean isEnable) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFirstAnswerButton.setClickable(isEnable);
                mSecondAnswerButton.setClickable(isEnable);
                mThirdAnswerButton.setClickable(isEnable);
                mFourthAnswerButton.setClickable(isEnable);
            }
        });
    }

    @Override
    public void clearButtons() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFirstAnswerButton.setBackgroundResource(R.drawable.answer_button_border);
                mSecondAnswerButton.setBackgroundResource(R.drawable.answer_button_border);
                mThirdAnswerButton.setBackgroundResource(R.drawable.answer_button_border);
                mFourthAnswerButton.setBackgroundResource(R.drawable.answer_button_border);
            }
        });
    }

    @Override
    public void setQuestionRemainTime(final int remainTime) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionTimerTextView.setText(Integer.toString(remainTime));
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
                mQuestionTimerTextView.setBackgroundResource(R.drawable.time_green_indicator);
            }
        });
    }

    @Override
    public void setRedTimeIndicator() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mQuestionTimerTextView.setBackgroundResource(R.drawable.time_red_indicator);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Fragment: onDestroy");
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        mPresenter.detach();
        super.onDestroy();
    }

    private void initMediaPlayer() {
        mMediaPlayerForRightAnswer = MediaPlayer.create(getActivity(), R.raw.right_answer_sound);
        mMediaPlayerForRightAnswer.setVolume(VOLUME, VOLUME);
        mMediaPlayerForWrongAnswer = MediaPlayer.create(getActivity(), R.raw.wrong_answer_sound);
        mMediaPlayerForWrongAnswer.setVolume(VOLUME, VOLUME);
    }

    private void restoreState() {
        mPresenter.setView(this);
        mPresenter.setRepository(RepositoryImpl.get(getActivity().getApplicationContext()));
        mPresenter.checkIsExistUserLocation();
        mPresenter.restoreQuestion();
        Log.d(LOG_TAG, "SavedInstanceState is true");
    }

}

