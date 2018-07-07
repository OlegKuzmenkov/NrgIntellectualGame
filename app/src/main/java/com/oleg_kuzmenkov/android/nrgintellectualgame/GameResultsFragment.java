package com.oleg_kuzmenkov.android.nrgintellectualgame;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;


public class GameResultsFragment extends Fragment {
    private static final String BUNDLE_CONTENT_COUNT_QUESTIONS = "count_questions";
    private static final String BUNDLE_CONTENT_COUNT_RIGHT_ANSWERS = "count_right_answers";

    private TextView mGameResultsTextView;
    private Button mGoToMainMenuButton;
    private int mCountQuestions;
    private int mCountRightAnswers;

    public static GameResultsFragment newInstance(int countQuestions,int countRightAnswers) {
        GameResultsFragment fragment = new GameResultsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(BUNDLE_CONTENT_COUNT_QUESTIONS , countQuestions);
        arguments.putInt(BUNDLE_CONTENT_COUNT_RIGHT_ANSWERS, countRightAnswers);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_game_results, container, false);

        mGameResultsTextView = v.findViewById(R.id.game_results_text_view);
        mGoToMainMenuButton = v.findViewById(R.id.go_to_main_menu_button);

        if (getArguments() != null && getArguments().containsKey(BUNDLE_CONTENT_COUNT_QUESTIONS)) {
            mCountQuestions = getArguments().getInt(BUNDLE_CONTENT_COUNT_QUESTIONS);
            mCountRightAnswers = getArguments().getInt(BUNDLE_CONTENT_COUNT_RIGHT_ANSWERS);
        }
        else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }

        setContent();

        mGoToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        animateButton();
        return v;
    }

    /**
     * Animate Button
     */
    private void animateButton(){
        ObjectAnimator animation = ObjectAnimator.ofFloat(mGoToMainMenuButton, "rotationY", 0.0f, 360f);
        animation.setDuration(2000);
        animation.setStartDelay(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setInterpolator(new AnticipateOvershootInterpolator());
        animation.start();
    }

    /**
     * Set the received content in the fragment's view
     */
    private void setContent(){
       mGameResultsTextView.setText("You answered correctly for "+mCountRightAnswers+" out of "+mCountQuestions+
               " questions. Congratulations!");
    }
}


