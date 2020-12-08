package edu.uw.tcss450.groupchat.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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

    private ChatMessageViewModel mChatModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherModel = new ViewModelProvider(getActivity()).get(WeatherViewModel.class);

        mWeatherModel.connect("98502");
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
     * Method to set the icons for weather condition
     *
     * @param info, the JSONObject that contain the weather info
     * @param view, the image view for weather condition
     */
    private void setImage(JSONObject info, ImageView view) throws JSONException {
        String icon = info.getString("icon");

        switch (icon) {
            case "01d":
                view.setImageResource(R.drawable.ic_1d);
                break;
            case "02d":
                view.setImageResource(R.drawable.ic_2d);
                break;
            case "03d":
                view.setImageResource(R.drawable.ic_3d);
                break;
            case "04d":
                view.setImageResource(R.drawable.ic_4d);
                break;
            case "09d":
                view.setImageResource(R.drawable.ic_9d);
                break;
            case "10d":
                view.setImageResource(R.drawable.ic_10d);
                break;
            case "11d":
                view.setImageResource(R.drawable.ic_11d);
                break;
            case "13d":
                view.setImageResource(R.drawable.ic_13d);
                break;
            case "50d":
                view.setImageResource(R.drawable.ic_50d);
                break;
            case "01n":
                view.setImageResource(R.drawable.ic_1n);
                break;
            case "02n":
                view.setImageResource(R.drawable.ic_2n);
                break;
            case "03n":
                view.setImageResource(R.drawable.ic_3n);
                break;
            case "04n":
                view.setImageResource(R.drawable.ic_4n);
                break;
            case "09n":
                view.setImageResource(R.drawable.ic_9n);
                break;
            case "10n":
                view.setImageResource(R.drawable.ic_10n);
                break;
            case "11n":
                view.setImageResource(R.drawable.ic_11n);
                break;
            case "13n":
                view.setImageResource(R.drawable.ic_13n);
                break;
            case "50n":
                view.setImageResource(R.drawable.ic_50n);
                break;
            default:
                Log.d("ICON ERROR", "Could not get weather icon");
                break;
        }
    }
}