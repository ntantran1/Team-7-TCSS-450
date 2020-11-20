package edu.uw.tcss450.groupchat.ui.weather;

/**
 * The class represents weather in an hour timestamp.
 *
 * @version November 19, 2020
 */
public class WeatherHourly {
    private int mHour;

    private String mId;

    private String mTemp;


    /**
     * Weather Constructor.
     * @param hour the current hour of the day
     * @param id the id of object
     * @param temp the temperature of this hour
     */
    public WeatherHourly(final int hour, final String id, final String temp) {
        mHour = hour;
        mId = id;
        mTemp = temp;
    }

    /**
     * Returns the hour.
     * @return the hour
     */
    public int getHour() {
        return mHour;
    }

    /**
     * Returns the id
     * @return the id
     */
    public String getID() {
        return mId;
    }

    /**
     * Returns the temperature.
     * @return the mTemp
     */
    public String getTemp() {
        return mTemp;
    }
}
