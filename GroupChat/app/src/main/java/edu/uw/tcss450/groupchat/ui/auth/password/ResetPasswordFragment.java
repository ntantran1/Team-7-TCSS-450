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
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.groupchat.databinding.FragmentResetPasswordBinding;
import edu.uw.tcss450.groupchat.utils.PasswordValidator;

import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdSpecialChar;

/**
 * Fragment for Password Recovery
 * @version November 18
 */
public class ResetPasswordFragment extends Fragment {

    private FragmentResetPasswordBinding binding;

    private ResetPasswordViewModel mResetPasswordModel;

    private final PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /**
     * Default constructor
     */
    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResetPasswordModel = new ViewModelProvider(getActivity())
                .get(ResetPasswordViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonReset.setOnClickListener(this::attemptReset);
        mResetPasswordModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResetResponse);
    }

    private void attemptReset(final View button) {

        validateEmail();
    }

    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editReset.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editReset.setError("Please enter a valid Email."));
    }

    private void verifyAuthWithServer() {
        mResetPasswordModel.connect(
                binding.editReset.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the result
    }

    private void navigateToLogin() {
        ResetPasswordFragmentDirections.ActionResetPasswordFragmentToSignInFragment directions =
                ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment();

        directions.setEmail(binding.editReset.getText().toString());
        directions.setVerify("Please check your email to update your password.");

        Navigation.findNavController(getView()).navigate(directions);
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to ResetPasswordViewModel.
     *
     * @param response the Response from the server
     */
    private void observeResetResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editReset.setError(
                            "Error Authenticating: " +
                                    response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                //
                navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}