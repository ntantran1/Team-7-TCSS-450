package edu.uw.tcss450.groupchat.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserInfoViewModel extends ViewModel {

    private final String mEmail;

    private UserInfoViewModel(String email) {
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        private final String email;

        public UserInfoViewModelFactory(String email) {
            this.email = email;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(email);
            }
            throw new IllegalArgumentException("Argument must be: " + UserInfoViewModel.class);
        }
    }

}
