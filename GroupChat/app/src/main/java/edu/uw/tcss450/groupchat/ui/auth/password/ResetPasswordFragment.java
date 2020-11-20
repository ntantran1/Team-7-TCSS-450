package edu.uw.tcss450.groupchat.ui.auth.password;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentChangePasswordBinding;
import edu.uw.tcss450.groupchat.databinding.FragmentResetPasswordBinding;
import edu.uw.tcss450.groupchat.utils.PasswordValidator;

import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.groupchat.utils.PasswordValidator.checkPwdSpecialChar;

/**
 * The Fragment class for Reset Password page.
 *
 * @version November 19, 2020
 */
public class ResetPasswordFragment extends Fragment {

    private ResetPasswordViewModel mResetPasswordModel;

    private FragmentResetPasswordBinding binding;

    private final PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /**
     * Default constructor.
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
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonReset.setOnClickListener(button -> validateEmail());

        mResetPasswordModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);
    }

    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editEmail.setError("Please enter a valid Email."));
    }

    private void verifyAuthWithServer() {
        mResetPasswordModel.connect(binding.editEmail.getText().toString());
    }

    private void navigateToLogin() {
        ResetPasswordFragmentDirections.ActionResetPasswordFragmentToSignInFragment directions =
                ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment();

        directions.setEmail(binding.editEmail.getText().toString());
        directions.setVerify("Please check your email to reset your password.");

        Navigation.findNavController(getView()).navigate(directions);
    }

    private void observeResponse(final JSONObject response) {
        if (response.length() > 0) {
            if (response.has("code")) {
                try {
                    binding.editEmail.setError("Error Authenticating: "
                            + response.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                navigateToLogin();
                binding.editEmail.setText("");
                binding.editEmail.setError(null);
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}