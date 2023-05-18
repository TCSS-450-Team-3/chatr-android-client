package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentHourlyWeatherCardBinding;
import edu.uw.tcss450.kylerr10.chatapp.ui.weather.HourlyForecast;

/**
 * RecyclerView Adapter for the {@link edu.uw.tcss450.kylerr10.chatapp.ui.weather.HourlyWeatherCardFragment}
 * in the {@link edu.uw.tcss450.kylerr10.chatapp.ui.weather.WeatherFragment}.
 *
 * @author Jasper Newkirk
 */
public class HourlyWeatherCardRecyclerViewAdapter extends
        RecyclerView.Adapter<HourlyWeatherCardRecyclerViewAdapter.HourlyWeatherCardViewHolder> {
    /**
     * List of hourly forecasts
     */
    private List<HourlyForecast> mHourlyForecasts;

    /**
     * Constructor for the RecyclerView Adapter.
     * @param hourlyForecasts the list of hourly forecasts.
     */
    public HourlyWeatherCardRecyclerViewAdapter(List<HourlyForecast> hourlyForecasts) {
        mHourlyForecasts = hourlyForecasts;
    }

    @NonNull
    @Override
    public HourlyWeatherCardViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        return new HourlyWeatherCardViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.fragment_hourly_weather_card, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherCardViewHolder holder, int position) {
        //Sets the notification for a notification view
        holder.setHourlyForecast(mHourlyForecasts.get(position));
    }

    @Override
    public int getItemCount() {
        return mHourlyForecasts.size();
    }

    /**
     * ViewHolder for the RecyclerView Adapter. Holds the hourly weather card view.
     */
    public class HourlyWeatherCardViewHolder extends RecyclerView.ViewHolder {
        /**
         * The view of the hourly weather card.
         */
        public final View mView;
        /**
         * The binding of the hourly weather card.
         */
        public FragmentHourlyWeatherCardBinding mBinding;

        /**
         * Constructor for the ViewHolder.
         * @param itemView the view of the hourly weather card.
         */
        public HourlyWeatherCardViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mBinding = FragmentHourlyWeatherCardBinding.bind(itemView);
        }

        /**
         * Sets the hourly forecast for the hourly weather card.
         * @param hourlyForecast the hourly forecast to set.
         */
        public void setHourlyForecast(final HourlyForecast hourlyForecast) {
            // TODO: implement code to actually set values in the hourly forecast
            mBinding.textTemperature.setText(hourlyForecast.getTemperature());
            mBinding.imageWeatherPreview.setImageIcon(hourlyForecast.getForecastIcon(mView));
            mBinding.imageWeatherPreview.setVisibility(View.VISIBLE);
            mBinding.textHour.setText(hourlyForecast.getHour());
            mBinding.textMeridiem.setText(hourlyForecast.getMeridiem());
        }

    }
}
