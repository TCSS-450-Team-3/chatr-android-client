package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.databinding.FragmentDailyWeatherCardBinding;

/**
 * RecyclerView Adapter for the {@link edu.uw.tcss450.kylerr10.chatapp.ui.weather.DailyWeatherCardFragment}
 * in the {@link edu.uw.tcss450.kylerr10.chatapp.ui.weather.WeatherFragment}.
 *
 * @author Jasper Newkirk
 */
public class DailyWeatherCardRecyclerViewAdapter extends
        RecyclerView.Adapter<DailyWeatherCardRecyclerViewAdapter.DailyWeatherCardViewHolder> {
    /**
     * List of notifications
     */
    private List<DailyForecast> mDailyForecasts;

    /**
     * Constructor for the RecyclerView Adapter.
     * @param dailyForecasts the list of daily forecasts.
     */
    public DailyWeatherCardRecyclerViewAdapter(List<DailyForecast> dailyForecasts) {
        mDailyForecasts = dailyForecasts;
    }

    @NonNull
    @Override
    public DailyWeatherCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DailyWeatherCardViewHolder(
            LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_daily_weather_card, parent, false
            )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DailyWeatherCardViewHolder holder, int position) {
        //Sets the notification for a notification view
        holder.setDailyForecast(mDailyForecasts.get(position));
    }

    @Override
    public int getItemCount() {
        return mDailyForecasts.size();
    }

    /**
     * ViewHolder for the RecyclerView Adapter. Holds the daily weather card view.
     */
    public static class DailyWeatherCardViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentDailyWeatherCardBinding mBinding;

        /**
         * Constructor for the ViewHolder.
         * @param itemView the view for the ViewHolder.
         */
        public DailyWeatherCardViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mBinding = FragmentDailyWeatherCardBinding.bind(itemView);
        }

        /**
         * Sets the values for the daily forecast card. This includes the high and low temperatures,
         * the weather icon, and the day of the week.
         * @param dailyForecast the daily forecast object to set the values for.
         */
        public void setDailyForecast(final DailyForecast dailyForecast) {
            // TODO: implement code to actually set values in the daily forecast
            mBinding.textTemperatureHigh.setText(dailyForecast.getTemperatureHigh());
            mBinding.textTemperatureLow.setText(dailyForecast.getTemperatureLow());
            mBinding.imageWeatherPreview.setImageIcon(dailyForecast.getForecastIcon(mView));
            mBinding.imageWeatherPreview.setVisibility(View.VISIBLE);
            mBinding.textDay.setText(dailyForecast.getDay());
        }

    }
}
