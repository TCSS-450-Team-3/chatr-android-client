package edu.uw.tcss450.kylerr10.chatapp.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.uw.tcss450.kylerr10.chatapp.listdata.Notification;
import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentHomeBinding;
import edu.uw.tcss450.kylerr10.chatapp.ui.weather.ForecastViewModel;
import edu.uw.tcss450.kylerr10.chatapp.ui.weather.HourlyForecast;

/**
 * Fragment representing the main page of the Home activity for the app.
 *
 * @author Kyler Robison
 */
public class HomeFragment extends Fragment {

    ForecastViewModel mForecastModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForecastModel = new ViewModelProvider(requireActivity()).get(ForecastViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Events for when the fragment and its views are created.
     * Also handles building of the RecyclerView that displays notifications.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentHomeBinding binding = FragmentHomeBinding.bind(requireView());
        // Set the information for the weather card based on the forecast data from the API
        mForecastModel.addForecastObserver(getViewLifecycleOwner(), forecast -> {
            if (!forecast.getCity().isEmpty() && !forecast.getState().isEmpty()) {
                binding.textCitystateWeather.setText(String.format("%s, %s", forecast.getCity(), forecast.getState()));
                if (!forecast.getHourlyList().isEmpty()) {
                    HourlyForecast currentForecast = forecast.getHourlyList().get(0);
                    binding.textWeatherdescription.setText(currentForecast.getForecast());
                    binding.textTemperatureHigh.setText(currentForecast.getTemperature());
                    binding.imageWeathericon.setImageIcon(currentForecast.getForecastIcon(binding.homeWeatherCard));
                    binding.imageWeathericon.setVisibility(View.VISIBLE);
                } else Log.e("FORECASTINFO", "Hourly forecast list is empty.");
            } else Log.e("FORECASTINFO", "City/State for forecast is empty.");
        });

        // Create a list of dummy notifications
        ArrayList<Notification> notifications = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            notifications.add(new Notification());
        }

        binding.recyclerViewNotifications.setAdapter(
                new NotificationsRecyclerViewAdapter(notifications)
        );
    }

}