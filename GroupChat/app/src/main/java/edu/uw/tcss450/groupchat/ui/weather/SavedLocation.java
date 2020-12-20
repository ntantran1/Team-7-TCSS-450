package edu.uw.tcss450.groupchat.ui.weather;

public class SavedLocation {

    private String mName;

    private Double mLatitude;

    private Double mLongitude;

    public SavedLocation(String name, double lat, double lon) {
        mName = name;
        mLatitude = lat;
        mLongitude = lon;
    }

    public SavedLocation(SavedLocation location) {
        mName = location.getName();
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    public String getName() {
        return mName;
    }

    public double getLatitude() {
        return Double.valueOf(mLatitude);
    }

    public double getLongitude() {
        return Double.valueOf(mLongitude);
    }

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
