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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentWeatherBinding;

/**
 * A simple {@link Fragment} subclass responsible for relaying weather information to the user.
 * @author Jasper Newkirk
 */
public class WeatherFragment extends Fragment {

    private ForecastViewModel mForecastModel;
    private LocationFragment mLocationFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationFragment = new LocationFragment();
        mForecastModel = new ViewModelProvider(requireActivity()).get(ForecastViewModel.class);
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
            } else Log.e("FORECASTINFO", "City/State for forecast is empty.");
            if (!forecast.getDailyList().isEmpty()) {
                binding.recyclerViewWeatherDaily.setAdapter(
                        new DailyWeatherCardRecyclerViewAdapter(forecast.getDailyList())
                );
            } else Log.e("FORECASTINFO", "Daily forecast list is empty.");
            if (!forecast.getHourlyList().isEmpty()) {
                binding.textCurrentTemperature.setText(forecast.getHourlyList().get(0).getTemperature());
                binding.textWeatherDescription.setText(forecast.getHourlyList().get(0).getForecast());
                binding.imageWeathericon.setVisibility(View.VISIBLE);
                binding.recyclerViewWeatherHourly.setAdapter(
                        new HourlyWeatherCardRecyclerViewAdapter(forecast.getHourlyList())
                );
            } else Log.e("FORECASTINFO", "Hourly forecast list is empty.");
        });

        binding.openLocationButton.setOnClickListener(this::openLocationDialog);
    }

    @Override
    public void onPause() {
        super.onPause();
        hideLocationDialog();
    }

    public void openLocationDialog(View view) {
        FloatingActionButton button = (FloatingActionButton) view;
        // The device is smaller, so show the fragment fullscreen
        showLocationDialog(button);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                hideLocationDialog();
                button.show();
            }
        });
    }

    private void showLocationDialog(FloatingActionButton button) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .add(R.id.weather_root, mLocationFragment)
            .setReorderingAllowed(true)
            .addToBackStack(null)
            .commit();
        button.hide();
    }

    private void hideLocationDialog() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            .remove(mLocationFragment)
            .setReorderingAllowed(true)
            .commit();
        //mLocationFragment.dismiss();
    }
}