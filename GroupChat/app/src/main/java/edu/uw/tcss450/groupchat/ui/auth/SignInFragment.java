package edu.uw.tcss450.groupchat.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.groupchat.databinding.FragmentSignInBinding;

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

        binding.buttonRegister.setOnClickListener(button -> register());
        binding.buttonSignin.setOnClickListener(button -> signin());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void register() {
        NavDirections action = SignInFragmentDirections.actionSignInFragmentToRegisterFragment();
        Navigation.findNavController(getView()).navigate(action);
    }

    private void signin() {
        String email = binding.textEmail.getText().toString();
        NavDirections action = SignInFragmentDirections.actionSignInFragmentToMainActivity(email);
        Navigation.findNavController(getView()).navigate(action);

        getActivity().finish();
    }
}