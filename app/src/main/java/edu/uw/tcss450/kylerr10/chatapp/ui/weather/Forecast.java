package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import java.io.Serializable;
import java.util.List;

/**
 * Class to encapsulate the data for a forecast. This includes the city, state, daily forecast list,
 * and hourly forecast list.
 * @author Jasper Newkirk
 */
public class Forecast implements Serializable {
    /**
     * The name of the city.
     */
    private final String mCity;
    /**
     * The state as an 2 letter abbreviation.
     */
    private final String mState;
    /**
     * The list of daily forecasts.
     */
    private final List<DailyForecast> mDailyList;
    /**
     * The list of hourly forecasts.
     */
    private final List<HourlyForecast> mHourlyList;

    /**
     * Constructs a Forecast object to store the city, state, daily forecast list, and
     * hourly forecast list for a particular location.
     * @param mCity The name of the city.
     * @param mState The state as an 2 letter abbreviation.
     * @param mDailyList The list of daily forecasts.
     * @param mHourlyList The list of hourly forecasts.
     */
    public Forecast(
            final String mCity,
            final String mState,
            final List<DailyForecast> mDailyList,
            final List<HourlyForecast> mHourlyList
    ) {
        this.mCity = mCity;
        this.mState = mState;
        this.mDailyList = mDailyList;
        this.mHourlyList = mHourlyList;
    }

    /**
     * Returns the name of the city.
     * @return The name of the city.
     */
    public String getCity() {
        return mCity;
    }

    /**
     * Returns the state as an 2 letter abbreviation.
     * @return The state as an 2 letter abbreviation.
     */
    public String getState() {
        return mState;
    }

    /**
     * Returns the list of daily forecasts.
     * @return The list of daily forecasts.
     */
    public List<DailyForecast> getDailyList() {
        return mDailyList;
    }

    /**
     * Returns the list of hourly forecasts.
     * @return The list of hourly forecasts.
     */
    public List<HourlyForecast> getHourlyList() {
        return mHourlyList;
    }
}
