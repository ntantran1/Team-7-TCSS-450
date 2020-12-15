package edu.uw.tcss450.groupchat.ui.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentWeatherSearchBinding;
import edu.uw.tcss450.groupchat.model.weather.LocationViewModel;
import edu.uw.tcss450.groupchat.model.weather.WeatherSearchDailyViewModel;
import edu.uw.tcss450.groupchat.model.weather.WeatherSearchViewModel;

/**
 * Fragment for custom location search of weather.
 *
 * @version December 2020
 */
public class WeatherSearchFragment extends Fragment {

    private LocationViewModel mLocationModel;

    private WeatherSearchViewModel mWeatherModel;

    private WeatherSearchDailyViewModel mDailyModel;

    private List<WeatherHourly> mHourly;

    private FragmentWeatherSearchBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationModel = new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        mWeatherModel = new ViewModelProvider(getActivity()).get(WeatherSearchViewModel.class);
        mDailyModel = new ViewModelProvider(getActivity()).get(WeatherSearchDailyViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        if (prefs.contains(getString(R.string.keys_prefs_lat))
                && prefs.contains(getString(R.string.keys_prefs_lon))) {
            double lat = prefs.getFloat(getString(R.string.keys_prefs_lat), 0);
            double lon = prefs.getFloat(getString(R.string.keys_prefs_lon), 0);

            Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lon);

            mWeatherModel.connect(lat, lon);
            mWeatherModel.initialize(location);
        }

        mLocationModel.addLocationObserver(getViewLifecycleOwner(), location -> {
            if (!mWeatherModel.isInitialized()) {
                mWeatherModel.connect(location.getLatitude(), location.getLongitude());
                mWeatherModel.initialize(location);
            }
        });

        binding.searchZip.setSubmitButtonEnabled(true);
        binding.searchZip.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mWeatherModel.connectZip(query);
                binding.weatherWait.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.buttonRefresh.setOnClickListener(button -> {
            if (binding.searchZip.getQuery().toString().isEmpty()) {
                Location location = mWeatherModel.getLocation();
                mWeatherModel.connect(location.getLatitude(), location.getLongitude());
            } else {
                mWeatherModel.connectZip(binding.searchZip.getQuery().toString());
            }
            binding.weatherWait.setVisibility(View.VISIBLE);
        });

        mWeatherModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            if (response.length() > 0) {
                if (response.has("code")) {
                    Log.d("Weather", "Invalid location");
                } else {
                    try {
                        AtomicBoolean celsius = new AtomicBoolean(false);

                        JSONObject main = response.getJSONObject("main");
                        JSONObject coord = response.getJSONObject("coord");
                        JSONObject wind = response.getJSONObject("wind");
                        JSONArray weather = response.getJSONArray("weather");
                        JSONObject info = (JSONObject) weather.get(0);

                        Location location = new Location("");
                        location.setLatitude(coord.getDouble("lat"));
                        location.setLongitude(coord.getDouble("lon"));
                        prefs.edit().putFloat(getString(R.string.keys_prefs_lat),
                                (float) coord.getDouble("lat")).apply();
                        prefs.edit().putFloat(getString(R.string.keys_prefs_lon),
                                (float) coord.getDouble("lon")).apply();
                        mWeatherModel.setLocation(location);

                        binding.textCity.setText(response.getString("name"));
                        binding.textDayTime.setText(LocalDate.now().getDayOfWeek().name() + " "
                                + new SimpleDateFormat("HH:mm",
                                Locale.getDefault()).format(new Date()));
                        binding.textCondition.setText(info.getString("main"));
                        setImage(info, binding.imageCondition);
                        binding.textDegree.setText(String.valueOf((int) main.getDouble("temp")));
                        binding.textHumidity.setText("Humidity: "
                                + (int) main.getDouble("humidity") + "%");
                        binding.textWind.setText("Wind: "
                                + (int) wind.getDouble("speed") + " mph");

                        mDailyModel.connect(coord.getDouble("lat"),
                                coord.getDouble("lon"));

                        binding.buttonCelsius.setOnClickListener(v -> {
                            if (!celsius.get()) {
                                try {
                                    int temp = (int) (main.getDouble("temp") - 32) * 5 / 9;
                                    binding.textDegree.setText(String.valueOf(temp));
                                    binding.buttonFahrenheit.setTextColor(Color.GRAY);
                                    binding.buttonCelsius.setTextColor(Color.BLACK);
                                    celsius.set(true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("JSON Parse Error", e.getMessage());
                                }
                            }
                        });

                        binding.buttonFahrenheit.setOnClickListener(v -> {
                            if (celsius.get()) {
                                try {
                                    int temp = (int) main.getDouble("temp");
                                    binding.textDegree.setText(String.valueOf(temp));
                                    binding.buttonFahrenheit.setTextColor(Color.BLACK);
                                    binding.buttonCelsius.setTextColor(Color.GRAY);
                                    celsius.set(false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("JSON Parse Error", e.getMessage());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("JSON Parse Error", e.getMessage());
                    }
                }
            } else {
                Log.d("JSON Response", "No Response");
            }
        });

        mDailyModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            if (response.length() > 0) {
                if (response.has("code")) {
                    Log.d("Weather", "Invalid location");
                } else {
                    try {
                        JSONArray daily = response.getJSONArray("daily");
                        JSONArray hourly = response.getJSONArray("hourly");
                        getDaily(daily);
                        getHourly(hourly);
                        binding.layoutWait.setVisibility(View.GONE);
                        binding.weatherWait.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        Log.e("JSON Parse Error", e.getMessage());
                    }
                }
            } else {
                Log.d("JSON Response", "No Response");
            }
        });
    }

    private void getDaily(JSONArray daily) throws JSONException {
        for (int i = 1; i < 6; i++) {
            JSONObject day = (JSONObject) daily.get(i);
            JSONObject temp = day.getJSONObject("temp");
            JSONArray weather = day.getJSONArray("weather");
            JSONObject info = (JSONObject) weather.get(0);

            String max = String.valueOf((int) temp.getDouble("max"));
            String min = String.valueOf((int) temp.getDouble("min"));
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int year = calendar.get(Calendar.YEAR);
            int dayYear = calendar.get(Calendar.DAY_OF_YEAR) + i;

            switch (i) {
                case 1:
                    setImage(info, binding.imageOne);
                    binding.textWeatherOne.setText(max + "°F / " + min + "°F");
                    binding.textDayOne.setText(LocalDate.ofYearDay(year, dayYear).getDayOfWeek().name());
                    break;
                case 2:
                    setImage(info, binding.imageTwo);
                    binding.textWeatherTwo.setText(max + "°F / " + min + "°F");
                    binding.textDayTwo.setText(LocalDate.ofYearDay(year, dayYear).getDayOfWeek().name());
                    break;
                case 3:
                    setImage(info, binding.imageThree);
                    binding.textWeatherThree.setText(max + "°F / " + min + "°F");
                    binding.textDayThree.setText(LocalDate.ofYearDay(year, dayYear).getDayOfWeek().name());
                    break;
                case 4:
                    setImage(info, binding.imageFour);
                    binding.textWeatherFour.setText(max + "°F / " + min + "°F");
                    binding.textDayFour.setText(LocalDate.ofYearDay(year, dayYear).getDayOfWeek().name());
                    break;
                case 5:
                    setImage(info, binding.imageFive);
                    binding.textWeatherFive.setText(max + "°F / " + min + "°F");
                    binding.textDayFive.setText(LocalDate.ofYearDay(year, dayYear).getDayOfWeek().name());
                    break;
                default:
                    Log.d("DAILY WEATHER ERROR", "Could not set daily weather");
                    break;
            }
        }
    }

    private void getHourly(JSONArray hourly) throws JSONException {
        mHourly = new ArrayList<>();
        for (int i = 1; i < 25; i++) {
            JSONObject hour = (JSONObject) hourly.get(i);
            JSONArray weather = hour.getJSONArray("weather");
            JSONObject info = (JSONObject) weather.get(0);

            Calendar calendar = Calendar.getInstance();
            int time = calendar.get(Calendar.HOUR_OF_DAY) + i;
            if (time >= 24) {
                time -= 24;
            }
            String temp = String.valueOf((int) hour.getDouble("temp"));
            String icon = info.getString("icon");

            mHourly.add(new WeatherHourly(time, icon, temp));
        }

        RecyclerView recyclerView = binding.hourlyView;
        if (recyclerView instanceof RecyclerView) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(new WeatherRecyclerViewAdapter(mHourly));
        }
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