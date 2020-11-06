package edu.uw.tcss450.groupchat.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * View model for user's information.
 *
 * @version November 5
 */
public class UserInfoViewModel extends ViewModel {
    /** User email. */
    private final String mEmail;

    /** User login token. */
    private final String mJwt;

    private UserInfoViewModel(String email, String jwt) {
        mEmail = email;
        mJwt = jwt;
    }

    /**
     * Get user email.
     *
     * @return user email string
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Get user login.
     *
     * @return user login token string
     */
    public String getJwt() {
        return mJwt;
    }

    /**
     * Utility Factory class for initilizing UserInfoViewModel.
     */
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {
        private final String email;
        private final String jwt;

        /**
         * Main public constructor to initialize the Factory.
         *
         * @param email user email string
         * @param jwt user login token string
         */
        public UserInfoViewModelFactory(String email, String jwt) {
            this.email = email;
            this.jwt = jwt;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(email, jwt);
            }
            throw new IllegalArgumentException("Argument must be: " + UserInfoViewModel.class);
        }
    }

}
