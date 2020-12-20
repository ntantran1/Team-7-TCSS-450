package edu.uw.tcss450.groupchat.ui.weather;

/**
 * The SavedLocation object represents a location in the form of a name, latitude, and longitude.
 *
 * @version December 19, 2020
 */
public class SavedLocation {

    private String mName;

    private Double mLatitude;

    private Double mLongitude;

    /**
     * Creates a new SavedLocation object with the given arguments.
     * @param name the name of the location
     * @param lat the latitude of the location
     * @param lon the longitude of the location
     */
    public SavedLocation(String name, double lat, double lon) {
        mName = name;
        mLatitude = lat;
        mLongitude = lon;
    }

    /**
     * Creates a new SavedLocation object from the given location.
     * @param location the location to create
     */
    public SavedLocation(SavedLocation location) {
        mName = location.getName();
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    /**
     * Returns the name of this object.
     * @return the name of this location
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the latitude of this object.
     * @return the latitude of this location
     */
    public double getLatitude() {
        return Double.valueOf(mLatitude);
    }

    /**
     * Returns the longitude of this object.
     * @return the longitude of this location
     */
    public double getLongitude() {
        return Double.valueOf(mLongitude);
    }

    /**
     * Updates the name of this object.
     * @param name the new name of this location
     */
    public void setName(final String name) {
        mName = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != SavedLocation.class) return false;

        SavedLocation other = (SavedLocation) o;
        if (Math.abs(other.getLatitude() - mLatitude) > 0.001) return false;
        return !(Math.abs(other.getLongitude() - mLongitude) > 0.001);
    }

    @Override
    public int hashCode() {
        return mName.hashCode() + mLatitude.hashCode() + mLongitude.hashCode();
    }

    @Override
    public String toString() {
        if (mName.startsWith("Current Location") || mName.length() < 26) return mName;
        return mName.substring(0, 26) + "...";
    }
}
