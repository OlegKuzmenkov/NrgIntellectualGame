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

public class GameFragment extends Fragment implements GameScreenView, GameFragmentCallBacks {
    private static final String LOG_TAG = "Message";
    private static final String BUNDLE_CONTENT = "content";
    private static final String BUNDLE_TIMER = "timer";
    private static final String BUNDLE_ANSWER = "answer";
    private final float mVolume = 0.01f;

    private TextView mTimerTextView;
    private TextView mQuestionTextView;
    private Button mFirstAnswerButton;
    private Button mSecondAnswerButton;
    private Button mThirdAnswerButton;
    private Button mFourthAnswerButton;
    private Button mRightAnswerButton;
    private Button mAnswerButton;
    private String mRightAnswer;
    private int timer;
    private boolean mAnswerIsDone;

    private MediaPlayer mMediaPlayerForRightAnswer;
    private MediaPlayer mMediaPlayerForWrongAnswer;
    private TimerForQuestion mTimerForQuestion;

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

        mMediaPlayerForRightAnswer = MediaPlayer.create(getActivity(), R.raw.right_answer_sound);
        mMediaPlayerForWrongAnswer = MediaPlayer.create(getActivity(), R.raw.wrong_answer_sound);
        mMediaPlayerForRightAnswer.setVolume(mVolume, mVolume);
        mMediaPlayerForWrongAnswer.setVolume(mVolume, mVolume);

        mTimerTextView = v.findViewById(R.id.timer_view);
        mQuestionTextView = v.findViewById(R.id.question_text_view);

        mFirstAnswerButton = v.findViewById(R.id.first_answer_button);
        mFirstAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswerButton = mFirstAnswerButton;
                checkAnswer();
            }
        });

        mSecondAnswerButton = v.findViewById(R.id.second_answer_button);
        mSecondAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswerButton = mSecondAnswerButton;
                checkAnswer();
            }
        });

        mThirdAnswerButton = v.findViewById(R.id.third_answer_button);
        mThirdAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswerButton = mThirdAnswerButton;
                checkAnswer();
            }
        });

        mFourthAnswerButton = v.findViewById(R.id.fourth_answer_button);
        mFourthAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswerButton = mFourthAnswerButton;
                checkAnswer();
            }
        });

        if (savedInstanceState == null) {
            // start new game
            timer = 10;
            mPresenter = new GameScreenPresenter(RepositoryImpl.get(getActivity().getApplicationContext()));
            mPresenter.setView(this);
            if (getArguments() != null && getArguments().containsKey(BUNDLE_CONTENT)) {
                User user = (User) getArguments().getSerializable(BUNDLE_CONTENT);
                mPresenter.setUser(user);
                mPresenter.checkIsExistUserLocation();
                Log.d(LOG_TAG, "User login" + user.getUserLogin());
            } else {
                throw new IllegalArgumentException("Must be created through newInstance(...)");
            }
            mPresenter.onClickSinglePlayerButton();
        } else {
            // restore the question
            mAnswerIsDone = savedInstanceState.getBoolean(BUNDLE_ANSWER);
            timer = savedInstanceState.getInt(BUNDLE_TIMER);
            mPresenter = (GameScreenPresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
            mPresenter.setView(this);
            mPresenter.setRepository(RepositoryImpl.get(getActivity().getApplicationContext()));
            mPresenter.checkIsExistUserLocation();

            if (timer < 1 || mAnswerIsDone == true) {
                if (timer == 0 && mAnswerIsDone == false) {
                    mMediaPlayerForWrongAnswer.start();
                }
                if (mPresenter.isLastQuestion()) {
                    Log.d(LOG_TAG, "It was last question");
                    mPresenter.showResultsOfTheGame();
                } else {
                    timer = 10;
                    mPresenter.getNextQuestion();
                }
            } else {
                mPresenter.getQuestion();
            }
            Log.d(LOG_TAG, "SavedInstanceState is true");
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
    public void stopGettingUserLocation() {
        Log.d(LOG_TAG, "stopGettingUserLocation");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mFusedLocationClient = null;
    }

    @Override
    public void displayQuestion(Question question) {
        Log.d(LOG_TAG, "displayQuestion");
        mAnswerIsDone = false;
        mQuestionTextView.setText(question.getQuestionText());
        mFirstAnswerButton.setText(question.getFirstCaseAnswer());
        mSecondAnswerButton.setText(question.getSecondCaseAnswer());
        mThirdAnswerButton.setText(question.getThirdCaseAnswer());
        mFourthAnswerButton.setText(question.getFourthCaseAnswer());
        mRightAnswer = question.getRightAnswer();
        mRightAnswerButton = identifyCorrectAnswerButton(mRightAnswer);
        startTimerForQuestion(timer);
    }

    @Override
    public void displayResultsOfGame(int countOfQuestions, int countWriteAnswers) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = GameResultsFragment.newInstance(countOfQuestions, countWriteAnswers);
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void checkAnswer() {
        mTimerForQuestion.cancel();
        mAnswerIsDone = true;
        setDisableAnswerButtons();

        if (mAnswerButton == mRightAnswerButton) {
            //Answer is right
            mPresenter.sendAnswerResult(true);
            mAnswerButton.setBackgroundResource(R.drawable.right_answer_button_border);
            mMediaPlayerForRightAnswer.start();
        } else {
            //Answer is wrong
            if (mAnswerButton != null) {
                mAnswerButton.setBackgroundResource(R.drawable.wrong_answer_button_border);
            }
            mRightAnswerButton.setBackgroundResource(R.drawable.right_answer_button_border);
            mMediaPlayerForWrongAnswer.start();
        }
        showResultAfterAnswer();
    }

    private void startTimerForQuestion(int remainTime) {
        mTimerForQuestion = new TimerForQuestion(remainTime, mTimerTextView, this);
    }

    private void showResultAfterAnswer() {
        Thread pauseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mRightAnswerButton.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mAnswerButton != mRightAnswerButton) {
                            mRightAnswerButton.setBackgroundResource(R.drawable.answer_button_border);
                        }
                        if (mAnswerButton != null) {
                            mAnswerButton.setBackgroundResource(R.drawable.answer_button_border);
                        }
                        setEnableAnswerButtons();

                        if (mPresenter.isLastQuestion()) {
                            Log.d(LOG_TAG, "It was last question");
                            mPresenter.showResultsOfTheGame();
                        } else {
                            timer = 10;
                            mPresenter.getNextQuestion();
                        }
                    }
                });

            }
        });
        pauseThread.start();
    }

    private Button identifyCorrectAnswerButton(final String rightAnswer) {
        if (mFirstAnswerButton.getText().toString().equals(rightAnswer)) {
            return mFirstAnswerButton;
        }
        if (mSecondAnswerButton.getText().toString().equals(rightAnswer)) {
            return mSecondAnswerButton;
        }
        if (mThirdAnswerButton.getText().toString().equals(rightAnswer)) {
            return mThirdAnswerButton;
        } else {
            return mFourthAnswerButton;
        }
    }

    @Override
    public void setEnableAnswerButtons() {
        mFirstAnswerButton.setClickable(true);
        mSecondAnswerButton.setClickable(true);
        mThirdAnswerButton.setClickable(true);
        mFourthAnswerButton.setClickable(true);
    }

    @Override
    public void setDisableAnswerButtons() {
        mFirstAnswerButton.setClickable(false);
        mSecondAnswerButton.setClickable(false);
        mThirdAnswerButton.setClickable(false);
        mFourthAnswerButton.setClickable(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull final  Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "Fragment: onSaveInstanceState");
        Log.d(LOG_TAG, "Remain time = " + mTimerForQuestion.getRemainTime());
        timer = mTimerForQuestion.getRemainTime();
        outState.putInt(BUNDLE_TIMER, timer);
        outState.putBoolean(BUNDLE_ANSWER, mAnswerIsDone);
        outState.putSerializable(BUNDLE_CONTENT, mPresenter);

    }

    @Override
    public void showRightAnswer() {
        setDisableAnswerButtons();
        mRightAnswerButton.setBackgroundResource(R.drawable.right_answer_button_border);
        mMediaPlayerForWrongAnswer.start();
        showResultAfterAnswer();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Fragment: onDestroy");
        if (mTimerForQuestion != null) {
            mTimerForQuestion.cancel();
        }
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        mPresenter.detach();
        super.onDestroy();
    }

}

