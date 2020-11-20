package edu.uw.tcss450.groupchat.ui.weather;

public class WeatherHourly {
    private int mHour;

    private String mId;

    private String mTemp;


    /**
     * Weather Constructor.
     * @param hour the username of the contact
     * @param id the name (first and last) of the contact
     * @param temp the email of the contact
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
