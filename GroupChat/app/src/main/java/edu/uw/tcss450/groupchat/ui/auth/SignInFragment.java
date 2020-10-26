package edu.uw.tcss450.groupchat.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.R;
import edu.uw.tcss450.databinding.FragmentSignInBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private FragmentSignInBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSigninRegister.setOnClickListener(button -> registerClicked());
        binding.buttonSigninSignin.setOnClickListener(this::signInClicked);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void registerClicked() {
        Navigation.findNavController(getView()).navigate(
                SignInFragmentDirections.actionSignInFragmentToRegisterFragment());
    }

    private void signInClicked(View view) {
        boolean fail = false;
        if (binding.editTextEmail.getText().toString().equals("")) {
            binding.editTextEmail.setError("Email is empty!");
            fail = true;
        }

        if (binding.editTextPassword.getText().toString().equals("")) {
            binding.editTextPassword.setError("Password is empty!");
            fail = true;
        }

        if (fail) {
            return;
        }

        if (binding.editTextEmail.getText().toString().contains("@") == false) {
            binding.editTextEmail.setError("Not valid email!");
            return;
        }

        Navigation.findNavController(getView()).navigate(
                SignInFragmentDirections.actionSignInFragmentToMainActivity(
                        binding.editTextEmail.getText().toString(), ""
                )
        );
        getActivity().finish();
    }
}