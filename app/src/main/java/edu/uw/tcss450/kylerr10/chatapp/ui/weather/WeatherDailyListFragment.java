package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.kylerr10.chatapp.R;

public class WeatherDailyListFragment extends Fragment {

    private WeatherDailyListViewModel mViewModel;

    public static WeatherDailyListFragment newInstance() {
        return new WeatherDailyListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weather_daily_list, container, false);
    }

}