package ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.fragmentslab.databinding.FragmentFirstBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment implements View.OnClickListener {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // add this Fragment object as the OnClickListener for the Red button
        binding.buttonRed.setOnClickListener(this);

        // use a lambda expression to add the OnClickListener for the Green button
        binding.buttonGreen.setOnClickListener(button -> processColor(Color.GREEN));

        // user a method reference to add the OnClickListener for the Blue button
        binding.buttonBlue.setOnClickListener(this::handleBlue);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.buttonRed) processColor(Color.BLUE);
    }

    public void processColor(int color) {
        Log.d("ACTIVITY", "Red: " + Color.red(color)
                + " Green: " + Color.green(color)
                + " Blue: " + Color.blue(color));

        // the following object represents the action from first to color
        FirstFragmentDirections.ActionFirstFragmentToColorFragment directions =
                FirstFragmentDirections.actionFirstFragmentToColorFragment(color);

        // use the navigate method to perform the navigation
        Navigation.findNavController(getView()).navigate(directions);
    }

    private void handleBlue(View v) {
        if (v == binding.buttonBlue) processColor(Color.RED);
    }
}