package edu.uw.tcss450.groupchat.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentHomeBinding;
import edu.uw.tcss450.groupchat.model.UserInfoViewModel;
import edu.uw.tcss450.groupchat.model.chats.ChatMessageViewModel;
import edu.uw.tcss450.groupchat.model.weather.WeatherSecondViewModel;
import edu.uw.tcss450.groupchat.model.weather.WeatherViewModel;
import edu.uw.tcss450.groupchat.ui.weather.WeatherHomeFragment;

/**
 * Fragment for Home page.
 *
 * @version November 5
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private WeatherViewModel mWeatherModel;
    //private WeatherHomeFragment fWeather;

    private ChatMessageViewModel mChatModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherModel = new ViewModelProvider(getActivity())
                .get(WeatherViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserInfoViewModel model = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        binding.textLabel.setText("Welcome home " + model.getEmail() + "!");

        // getting response from the weather API
        mWeatherModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            try {
                // Creating JSON from the api
                JSONObject main = response.getJSONObject("main");
                JSONArray info = response.getJSONArray("weather");
                JSONObject info2 = (JSONObject) info.get(0);

                // Getting weather information and display it
                binding.textDegree2.setText(String.valueOf((int)(main.getDouble("temp"))) + "℉");
                binding.textCity2.setText(response.getString("name"));
                binding.textDayTime2.setText(LocalDate.now().getDayOfWeek().name() + " "
                        + new SimpleDateFormat("HH:mm",
                        Locale.getDefault()).format(new Date()));
                setImage(info2, binding.imageCondition2);

                binding.textCondition2.setText(info2.getString("main"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * (Same method from the weather fragment class to display the weather icon)
     * Method to set the icons for weather condition
     * @param info2, the JSONObject that contain the weather info
     * @param v, the image view for weather condition
     */
    public void setImage(JSONObject info2, ImageView v) throws JSONException {
        String icon = info2.getString("icon");
        if (icon.contentEquals("01d")) {
            v.setImageResource(R.drawable.ic_1d);
        } else if (icon.contentEquals("02d")) {
            v.setImageResource(R.drawable.ic_2d);
        } else if (icon.contentEquals("03d")) {
            v.setImageResource(R.drawable.ic_3d);
        } else if (icon.contentEquals("04d")) {
            v.setImageResource(R.drawable.ic_4d);
        } else if (icon.contentEquals("09d")) {
            v.setImageResource(R.drawable.ic_9d);
        } else if (icon.contentEquals("10d")) {
            v.setImageResource(R.drawable.ic_10d);

        } else if (icon.contentEquals("11d")) {
            v.setImageResource(R.drawable.ic_11d);

        } else if (icon.contentEquals("13d")) {
            v.setImageResource(R.drawable.ic_13d);

        } else if (icon.contentEquals("50d")) {
            v.setImageResource(R.drawable.ic_50d);

        } else if (icon.contentEquals("01n")) {
            v.setImageResource(R.drawable.ic_1n);

        } else if (icon.contentEquals("02n")) {
            v.setImageResource(R.drawable.ic_2n);

        } else if (icon.contentEquals("03n")) {
            v.setImageResource(R.drawable.ic_3n);

        } else if (icon.contentEquals("04n")) {
            v.setImageResource(R.drawable.ic_4n);

        } else if (icon.contentEquals("09n")) {
            v.setImageResource(R.drawable.ic_9n);

        } else if (icon.contentEquals("10n")) {
            v.setImageResource(R.drawable.ic_10n);

        } else if (icon.contentEquals("11n")) {
            v.setImageResource(R.drawable.ic_11n);

        } else if (icon.contentEquals("13n")) {
            v.setImageResource(R.drawable.ic_13n);

        } else if (icon.contentEquals("50n")) {
            v.setImageResource(R.drawable.ic_50n);

        }
    }
}