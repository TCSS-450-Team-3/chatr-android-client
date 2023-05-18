package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import android.graphics.drawable.Icon;
import android.view.View;

import java.io.Serializable;

import edu.uw.tcss450.kylerr10.chatapp.R;

/**
 * Class to encapsulate the data for a hourly forecast. This includes the time, whether it is day
 * or night, the temperature, the unit of temperature, and the short forecast for the hour.
 * This data is almost always how it is retrieved from the Weather.gov API.
 * @author Jasper Newkirk
 */
public class DailyForecast implements Serializable {
    /**
     * The day of the week.
     */
    private final String mDay;
    /**
     * The high temperature for the day.
     */
    private final int mTemperatureHigh;
    /**
     * The low temperature for the day.
     */
    private final int mTemperatureLow;
    /**
     * The unit of temperature, almost always "F", for Fahrenheit.
     */
    private final String mTemperatureUnit;
    /**
     * The short forecast for the day.
     */
    private final String mShortForecast;

    /**
     * Constructs a DailyForecast object to store the day, high temperature, low temperature,
     * temperature unit, and short forecast for a particular day. This data is how it is retrieved
     * from the Weather.gov API.
     * @param mDay The day of the week.
     * @param mTemperatureHigh The high temperature for the day.
     * @param mTemperatureLow The low temperature for the day.
     * @param mTemperatureUnit The unit of temperature, almost always "F", for Fahrenheit.
     * @param mShortForecast The short forecast for the day.
     */
    public DailyForecast(
            final String mDay,
            final int mTemperatureHigh,
            final int mTemperatureLow,
            final String mTemperatureUnit,
            final String mShortForecast
    ) {
        this.mDay = mDay;
        this.mTemperatureHigh = mTemperatureHigh;
        this.mTemperatureLow = mTemperatureLow;
        this.mTemperatureUnit = mTemperatureUnit;
        this.mShortForecast = mShortForecast;
    }

    /**
     * Returns the day of the week.
     * @return The day of the week.
     */
    public String getDay() {
        return mDay;
    }

    /**
     * Returns the high temperature for the day with a degree symbol suffix.
     * @return The high temperature for the day with a degree symbol suffix.
     */
    public String getTemperatureHigh() {
        return mTemperatureHigh + "°";
    }

    /**
     * Returns the low temperature for the day with a degree symbol suffix.
     * @return The low temperature for the day with a degree symbol suffix.
     */
    public String getTemperatureLow() {
        return mTemperatureLow + "°";
    }

    /**
     * Returns the short forecast for the day.
     * @return The short forecast for the day.
     */
    public String getTemperatureUnit() {
        return mTemperatureUnit;
    }

    /**
     * Returns the icon best representing the short forecast for the day.
     * @param view The view to get the context from.
     * @return The icon best representing the short forecast for the day.
     */
    public Icon getForecastIcon(View view) {
        String condition = this.mShortForecast.toLowerCase();
        int iconID = R.drawable.ic_sunny_onsecondarycontainer_24dp; // Sunny default
        if (condition.contains("partly sunny") || condition.contains("partly cloudy")) {
            iconID = R.drawable.ic_partlycloudy_onsecondarycontainer_24dp;
        } else if (condition.contains("rain")) {
            iconID = R.drawable.ic_rainy_onsecondarycontainer_24dp;
        } else if (condition.contains("snow")) {
            iconID = R.drawable.ic_snowy_onsecondarycontainer_24dp;
        } else if (condition.contains("hail")) {
            iconID = R.drawable.ic_hail_onsecondarycontainer_24dp;
        } else if (condition.contains("cloudy")) {
            iconID = R.drawable.ic_cloudy_onsecondarycontainer_24dp;
        }
        return Icon.createWithResource(view.getContext(), iconID);
    }
}