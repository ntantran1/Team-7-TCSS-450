package edu.uw.tcss450.groupchat.ui.weather;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import edu.uw.tcss450.groupchat.R;
import edu.uw.tcss450.groupchat.databinding.FragmentWeatherHomeBinding;
import edu.uw.tcss450.groupchat.ui.contacts.Contact;
import edu.uw.tcss450.groupchat.ui.contacts.ContactsRecyclerViewAdapter;

/**
 * The Fragment class for Weather home page.
 *
 * @version November 19, 2020
 */
public class WeatherHomeFragment extends Fragment {

    private FragmentWeatherHomeBinding binding;
    private WeatherViewModel mWeatherModel;
    private WeatherSecondViewModel mWeatherSecondModel;
    private String mLat;
    private String mLon;
    private List<WeatherHourly> mHourly;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherModel = new ViewModelProvider(getActivity())
                .get(WeatherViewModel.class);
        mWeatherSecondModel = new ViewModelProvider(getActivity())
                .get(WeatherSecondViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherHomeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // getting response from the weather API
        mWeatherModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            try {
                // Setting layout for the weather fragment
                binding.buttonCelsius.setTextColor(Color.parseColor("#2196F3"));
                binding.buttonFahrenheit.setTextColor(Color.parseColor("#0E0E0E"));

                // a boolean value to check if degree is in celsius or fahrenheit
                final boolean[] value = {false};

                // Creating JSON from the api
                JSONObject main = response.getJSONObject("main");
                JSONObject cord = response.getJSONObject("coord");
                JSONObject wind = response.getJSONObject("wind");
                JSONArray info = response.getJSONArray("weather");
                JSONObject info2 = (JSONObject) info.get(0);

                binding.textDegree.setText(String.valueOf((int)(main.getDouble("temp"))));

                // Set on click listener for celsius button
                binding.buttonCelsius.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!value[0]) {
                            try {
                                binding.textDegree.setText(String.valueOf((int)(
                                        (main.getDouble("temp")-32) * 5/9)));
                                binding.buttonFahrenheit.setTextColor(Color.parseColor("#2196F3"));
                                binding.buttonCelsius.setTextColor(Color.parseColor("#0E0E0E"));
                                value[0] = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

                // Set on click listener for fahrenheit button
                binding.buttonFahrenheit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(value[0]) {
                            try {
                                binding.textDegree.setText(String.valueOf((int)(main.getDouble("temp"))));
                                binding.buttonCelsius.setTextColor(Color.parseColor("#2196F3"));
                                binding.buttonFahrenheit.setTextColor(Color.parseColor("#0E0E0E"));
                                value[0] = false;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

                // Getting weather information and display it
                binding.textCity.setText(response.getString("name"));
                binding.textDayTime.setText(LocalDate.now().getDayOfWeek().name() + " "
                        + new SimpleDateFormat("HH:mm",
                        Locale.getDefault()).format(new Date()));
                binding.textHumidity.setText("Humidity:  "
                        + (int)main.getDouble("humidity") + "%");

                binding.textWind.setText("Wind:  " +
                        (int)(wind.getDouble("speed")) + " mph");

                binding.textCondition.setText(info2.getString("main"));

                setImage(info2, binding.imageCondition);

                binding.buttonFahrenheit.setText("℉");
                binding.buttonCelsius.setText("℃");
                binding.textSlash.setText("|");

                binding.searchZip.onActionViewCollapsed();

                this.mLat = String.valueOf(cord.getDouble("lat"));
                this.mLon = String.valueOf(cord.getDouble("lon"));
                mWeatherSecondModel.connectDaily(binding, mLon, mLat);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Getting response for daily and hourly weather info
        mWeatherSecondModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            try {
                JSONArray daily = response.getJSONArray("daily");

                JSONArray hourly = response.getJSONArray("hourly");

                this.getDaily(binding, daily);
                this.getHourly(binding, hourly);
                binding.layoutWait.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        // Set up default location weather
        if(mWeatherModel.getVal()) {
            binding.layoutWait.setVisibility(View.VISIBLE);
            mWeatherModel.connect(binding, "98502");

            mWeatherModel.setVal();
        }

        // add listener for search bar
        binding.searchZip.setSubmitButtonEnabled(true);
        binding.searchZip.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mWeatherModel.connect(binding, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.buttonRefresh.setOnClickListener(button ->
                mWeatherModel.connect(binding, mWeatherModel.mZip));
    }

    /**
     * A method to display daily weather info.
     * @param b, the FragmentWeatherHomeBinding.
     * @param d, the daily JSONArray.
     */
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getDaily(FragmentWeatherHomeBinding b, JSONArray d) throws JSONException {

        //Loop to set up weather info for 5 days
        for(int i = 1; i <= 5; i++) {
            JSONObject day = (JSONObject) d.get(i);
            JSONObject t = day.getJSONObject("temp");
            JSONArray w = day.getJSONArray("weather");
            JSONObject c = (JSONObject) w.get(0);

            String max = String.valueOf((int)((t.getDouble("max"))));
            String min = String.valueOf((int)((t.getDouble("min"))));
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Date date = calendar.getTime();
            int year = calendar.get(Calendar.YEAR);

            // Setting up weather info for each day
            if(i == 1) {
                setImage(c, b.imageOne);
                b.textWeatherOne.setText(max + "° / " + min + "°");
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) + 1;
                b.textDayOne.setText(LocalDate.ofYearDay(year, dayOfYear).getDayOfWeek().name());

            } else if(i == 2) {
                setImage(c, binding.imageTwo);
                b.textWeatherTwo.setText(max + "° / " + min + "°");
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) + 2;
                b.textDayTwo.setText(LocalDate.ofYearDay(year, dayOfYear).getDayOfWeek().name());
            } else if(i == 3) {
                setImage(c, binding.imageThree);
                b.textWeatherThree.setText(max + "° / " + min + "°");
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) + 3;
                b.textDayThree.setText(LocalDate.ofYearDay(year, dayOfYear).getDayOfWeek().name());
            }
            else if(i == 4) {
                setImage(c, binding.imageFour);
                b.textWeatherFour.setText(max + "° / " + min + "°");
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) + 4;
                b.textDayFour.setText(LocalDate.ofYearDay(year, dayOfYear).getDayOfWeek().name());
            } else if(i == 5) {
                setImage(c, binding.imageFive);
                b.textWeatherFive.setText(max + "° / " + min + "°");
                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) + 5;
                b.textDayFive.setText(LocalDate.ofYearDay(year, dayOfYear).getDayOfWeek().name());
            }

        }

    }

    private void getHourly(FragmentWeatherHomeBinding b, JSONArray h) throws JSONException {
        mHourly = new ArrayList<>();
        for(int i = 1; i <= 24; i++) {
            JSONObject hour = (JSONObject) h.get(i);
            JSONArray w = hour.getJSONArray("weather");
            JSONObject c = (JSONObject) w.get(0);
            Calendar rightNow = Calendar.getInstance();
            int time = rightNow.get(Calendar.HOUR_OF_DAY) + i ;
            if(time >= 24) {
                time = time - 24;
            }
            String temp = String.valueOf((int)(hour.getDouble("temp")));
            String id = c.getString("icon");

            mHourly.add(new WeatherHourly(time, id, temp));

        }

        RecyclerView view = binding.hourlyView;

        if (view instanceof RecyclerView) {
            view.setLayoutManager(new LinearLayoutManager(view.getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            view.setAdapter(new WeatherRecyclerViewAdapter(mHourly));

        }

    }

    /**
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