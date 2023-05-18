package edu.uw.tcss450.kylerr10.chatapp.ui.weather;

import android.graphics.drawable.Icon;
import android.view.View;

import java.io.Serializable;

import edu.uw.tcss450.kylerr10.chatapp.R;

public class HourlyForecast implements Serializable {
    /**
     * The time of the forecast.
     */
    private final String mTime;
    /**
     * Whether it is day or night.
     */
    private final boolean mIsDayTime;
    /**
     * The temperature.
     */
    private final int mTemperature;
    /**
     * The unit of temperature, almost always "F", for Fahrenheit.
     */
    private final String mTemperatureUnit;
    /**
     * The short forecast for the hour.
     */
    private final String mShortForecast;

    /**
     * Constructs a HourlyForecast object to store the time, whether it is day or night, the
     * temperature, the unit of temperature, and the short forecast for the hour. This data is how
     * it is retrieved from the Weather.gov API.
     * @param mTime The time of the forecast.
     * @param mIsDayTime Whether it is day or night.
     * @param mTemperature The temperature.
     * @param mTemperatureUnit The unit of temperature, almost always "F", for Fahrenheit.
     * @param mShortForecast The short forecast for the hour.
     */
    public HourlyForecast(
            final String mTime,
            final boolean mIsDayTime,
            final int mTemperature,
            final String mTemperatureUnit,
            final String mShortForecast
    ) {
        this.mTime = mTime;
        this.mIsDayTime = mIsDayTime;
        this.mTemperature = mTemperature;
        this.mTemperatureUnit = mTemperatureUnit;
        this.mShortForecast = mShortForecast;
    }

    /**
     * Returns the time of the forecast.
     * @return The time of the forecast.
     */
    public String getHour() {
        return this.mTime.substring(0, this.mTime.indexOf(":"));
    }

    /**
     * Returns the AM/PM suffix of the forecast time.
     * @return The AM/PM suffix of the forecast time.
     */
    public String getMeridiem() {
        return this.mTime.substring(this.mTime.length() - 2);
    }

    /**
     * Returns the temperature with the degree symbol appended.
     * @return The temperature with the degree symbol appended.
     */
    public String getTemperature() {
        return mTemperature + "°";
    }

    /**
     * Returns the unit of temperature, almost always "F", for Fahrenheit.
      * @return The unit of temperature, almost always "F", for Fahrenheit.
     */
    public String getTemperatureUnit() {
        return mTemperatureUnit;
    }

    /**
     * Returns the short forecast for the hour.
     * @return The short forecast for the hour.
     */
    public String getForecast() {
        return mShortForecast;
    }

    /**
     * Returns the icon for the forecast based on the best guess, given the short forecast description.
     * @param view The view to get the context from.
     * @return The icon for the forecast based on the best guess, given the short forecast description.
     */
    public Icon getForecastIcon(View view) {
        String condition = this.mShortForecast.toLowerCase();
        int iconID = R.drawable.ic_sunny_onsecondarycontainer_24dp; // Sunny default
        if (!mIsDayTime) {
            if (condition.contains("cloudy")) {
                iconID = R.drawable.ic_cloudynight_onsecondarycontainer_24dp;
            } else {
                iconID = R.drawable.ic_night_onsecondarycontainer_24dp;
            }
        } else if (condition.contains("partly sunny") || condition.contains("partly cloudy")) {
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