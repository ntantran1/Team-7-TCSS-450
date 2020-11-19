package edu.uw.tcss450.groupchat.ui.auth.password;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChangePasswordBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.utils.PasswordValidator;

import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkClientPredicate;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdUpperCase;

/**
 * Fragment for Change Password feature
 * @version November 18
 */
public class ChangePasswordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;

    private ChangePasswordViewModel mChangePasswordModel;

    private UserInfoViewModel mUserViewModel;

    private final PasswordValidator mOldPasswordValidator =
            checkClientPredicate(pwd -> pwd.equals(binding.editOldPassword.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    private final PasswordValidator mNewPasswordValidator =
            checkClientPredicate(pwd -> pwd.equals(binding.editNewPassword2.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    /**
     * Default constructor
     */
    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mChangePasswordModel = provider.get(ChangePasswordViewModel.class);
        mUserViewModel = provider.get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonConfirm.setOnClickListener(button -> validateOldPassword());

        mChangePasswordModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    private void validateOldPassword() {
        mOldPasswordValidator.processResult(
                mOldPasswordValidator.apply(binding.editOldPassword.getText().toString()),
                this::validateNewPasswordsMatch,
                result -> binding.editOldPassword.setError("Please enter a valid Password."));
    }

    private void validateNewPasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(binding.editNewPassword2.getText().toString().trim()));

        mNewPasswordValidator.processResult(
                matchValidator.apply(binding.editNewPassword1.getText().toString().trim()),
                this::validateNewPassword,
                result -> binding.editNewPassword1.setError("New Passwords must match."));
    }

    private void validateNewPassword() {
        mNewPasswordValidator.processResult(
                mNewPasswordValidator.apply(binding.editNewPassword1.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editNewPassword1.setError("Please enter a New valid Password."));
    }

    private void verifyAuthWithServer() {
        mChangePasswordModel.connect(mUserViewModel.getJwt(),
                binding.editOldPassword.getText().toString(),
                binding.editNewPassword1.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the result
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to ChangePasswordViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editOldPassword.setError("Error Authenticating: "
                            + response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                String text = "Password Changed Successfully";
                binding.labelChanged.setText(text);

                binding.editOldPassword.setText("");
                binding.editNewPassword1.setText("");
                binding.editNewPassword2.setText("");

                binding.editOldPassword.setError(null);
                binding.editNewPassword1.setError(null);
                binding.editNewPassword2.setError(null);
            }
        } else {
            Log.d("JSON Response", "No Response");
            binding.labelChanged.setText("");
        }
    }
}