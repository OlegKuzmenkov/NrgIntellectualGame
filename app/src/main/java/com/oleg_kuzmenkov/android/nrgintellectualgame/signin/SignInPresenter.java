package com.oleg_kuzmenkov.android.nrgintellectualgame.signin;

import java.io.Serializable;

public class SignInPresenter implements Serializable {
    private static final String LOG_TAG = "SIGN_IN_PRESENTER";

    private transient SignInView mSignInView;

    SignInPresenter() { }

    void setView(final SignInView signInView) {
        mSignInView = signInView;
    }

    /**
     * Detach View and presenter
     */
    void detach() {
        mSignInView = null;
    }

    void checkUserLogin(String userLogin) {
        if (userLogin.equals("")) {
            mSignInView.displayNotification();
        } else {
            mSignInView.displayMenu(userLogin);
        }
    }
}

