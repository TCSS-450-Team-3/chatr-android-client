package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentWeatherBinding;

/**
 * A simple {@link Fragment} subclass responsible for relaying weather information to the user.
 * @author Jasper Newkirk
 */
public class WeatherFragment extends Fragment {

    private ForecastViewModel mForecastModel;
    private LocationViewModel mLocationModel;
    private UserLocationViewModel mUserLocationModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mForecastModel = new ViewModelProvider(requireActivity()).get(ForecastViewModel.class);
        mLocationModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        mUserLocationModel = new ViewModelProvider(requireActivity()).get(UserLocationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    /**
     * Events for when the fragment and its views are created.
     * Also handles building of the RecyclerView that displays hourly and daily forecasts.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentWeatherBinding binding = FragmentWeatherBinding.bind(requireView());

        mForecastModel.addForecastObserver(getViewLifecycleOwner(), forecast -> {
            if (!forecast.getCity().isEmpty() && !forecast.getState().isEmpty()) {
                binding.textCurrentCityState.setText(String.format("%s, %s", forecast.getCity(), forecast.getState()));
                mLocationModel.addLocationObserver(getViewLifecycleOwner(), location -> {
                    if (location != null) {
                        // Determine if the location is the devices current location or a marked location
                        binding.textCurrentLocation.setText(
                                location.getLatitude() == forecast.getLatitude()
                                        && location.getLongitude() == forecast.getLongitude()
                                        ? R.string.title_current_location
                                        : R.string.title_marked_location
                        );
                        // Determine if the current or marked location is a saved location
                        mUserLocationModel.addLocationObserver(getViewLifecycleOwner(), savedLocations -> {
                            if (savedLocations != null && savedLocations.size() > 0) {
                                for (UserLocation savedLocation : savedLocations) {
                                    if (savedLocation.getLatitude() == forecast.getLatitude()
                                            && savedLocation.getLongitude() == forecast.getLongitude()) {
                                        binding.textCurrentLocation.setText(R.string.title_saved_location);
                                        return;
                                    }
                                }
                            } else Log.e("LOCATIONINFO", "User location is null.");
                        });
                    } else Log.e("LOCATIONINFO", "Location is null.");
                });
            } else Log.e("FORECASTINFO", "City/State for forecast is empty.");
            if (!forecast.getDailyList().isEmpty()) {
                binding.recyclerViewWeatherDaily.setAdapter(
                        new DailyWeatherCardRecyclerViewAdapter(forecast.getDailyList())
                );
            } else Log.e("FORECASTINFO", "Daily forecast list is empty.");
            if (!forecast.getHourlyList().isEmpty()) {
                binding.textCurrentTemperature.setText(forecast.getHourlyList().get(0).getTemperature());
                binding.textWeatherDescription.setText(forecast.getHourlyList().get(0).getForecast());
                binding.imageWeathericon.setImageIcon(forecast.getHourlyList().get(0).getForecastIcon(binding.weatherCurrentCard));
                binding.imageWeathericon.setVisibility(View.VISIBLE);
                binding.recyclerViewWeatherHourly.setAdapter(
                        new HourlyWeatherCardRecyclerViewAdapter(forecast.getHourlyList())
                );
            } else Log.e("FORECASTINFO", "Hourly forecast list is empty.");
        });

        binding.openLocationButton.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.navigation_location);
        });
    }
}