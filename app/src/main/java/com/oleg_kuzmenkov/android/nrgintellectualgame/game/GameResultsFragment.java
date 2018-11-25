package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

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

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;

public class GameResultsFragment extends Fragment {
    private static final String BUNDLE_CONTENT_RIGHT_ANSWERS_COUNT = "RIGHT_ANSWERS_COUNT";

    private TextView mGameResult;
    private Button mExitButton;
    private int mQuestionsCount;
    private int mRightAnswersCount;

    public static GameResultsFragment newInstance(final int countRightAnswers) {
        GameResultsFragment fragment = new GameResultsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(BUNDLE_CONTENT_RIGHT_ANSWERS_COUNT, countRightAnswers);
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
        final View v = inflater.inflate(R.layout.fragment_game_results, container, false);

        mGameResult = v.findViewById(R.id.game_results_text_view);
        Bundle bundle = getArguments();

        if (bundle != null && bundle.containsKey(BUNDLE_CONTENT_RIGHT_ANSWERS_COUNT)) {
            int rightAnswersCount = getArguments().getInt(BUNDLE_CONTENT_RIGHT_ANSWERS_COUNT);
            showGameResult(rightAnswersCount);
        } else {
            throw new IllegalArgumentException("Must be created through newInstance(...)");
        }

        mExitButton = v.findViewById(R.id.go_to_main_menu_button);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        animateButton();

        return v;
    }

    /**
     * Animate exit button
     */
    private void animateButton() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(mExitButton, "rotationY",
                0.0f, 360f);
        animation.setDuration(2000);
        animation.setStartDelay(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setInterpolator(new AnticipateOvershootInterpolator());
        animation.start();
    }

    /**
     * Show game results
     */
    private void showGameResult(int rightAnswersCount) {
        int questionsCount = GameScreenPresenter.COUNT_QUESTIONS_FOR_GAME;
        String result = String.format("You answered correctly for %d ", rightAnswersCount);
        result = result.concat(String.format("out of %d questions. Congratulations!", questionsCount));
        mGameResult.setText(result);
    }
}


