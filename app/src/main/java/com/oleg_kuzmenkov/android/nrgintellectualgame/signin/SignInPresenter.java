package com.oleg_kuzmenkov.android.nrgintellectualgame.signin;

import java.io.Serializable;

public class SignInPresenter implements Serializable {

    private transient SignInView mSignInView;

    SignInPresenter() { }

    void setView(final SignInView signInView) {
        mSignInView = signInView;
    }

    /**
     * Detach presenter with view
     */
    void detach() {
        mSignInView = null;
    }

    /**
     * Check user login
     */
    void checkUserLogin(String userLogin) {
        if (userLogin.equals("")) {
            mSignInView.displayNotification();
        } else {
            mSignInView.displayMenu(userLogin);
        }
    }
}

