package com.example.entrega.view;

public class LocationModal {
    private String locationName;
    private String logDate;
    private String latitude;
    private String longitude;
    private String altitude;
    private String locationType;
    private int id;
    public String getLocationName() { return locationName; }
    public void setdeviceName(String deviceName)
    {
        this.locationName = deviceName;
    }
    public String getLogDate()
    {
        return logDate;
    }
    public void setlogDate(String deviceYear)
    {
        this.logDate = deviceYear;
    }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getAltitude() { return altitude; }
    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }
    public String getdeviceType()
    {
        return locationType;
    }
    public void setLocationtype(String locationType)
    {
        this.locationType = locationType;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocationModal(String locationName,
                         String logDate,
                         String latitude,
                         String longitude,
                         String altitude,
                         String deviceType)
    {
        this.locationName = locationName;
        this.logDate = logDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.locationType = deviceType;
    }
}
