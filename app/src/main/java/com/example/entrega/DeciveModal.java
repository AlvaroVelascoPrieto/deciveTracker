package com.example.entrega;

public class DeciveModal {

    // variables for our deviceName,
    // description, tracks and duration, id.
    private String deviceName;
    private String deviceYear;
    private String deviceModel;
    private String deviceType;
    private int id;

    // creating getter and setter methods
    public String getDeviceName() { return deviceName; }

    public void setdeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getdeviceYear()
    {
        return deviceYear;
    }

    public void setdeviceYear(String deviceYear)
    {
        this.deviceYear = deviceYear;
    }

    public String getdeviceModel() { return deviceModel; }

    public void setdeviceModel(String deviceModel)
    {
        this.deviceModel = deviceModel;
    }

    public String getdeviceType()
    {
        return deviceType;
    }

    public void
    setdeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    // constructor
    public DeciveModal(String deviceName,
                       String deviceYear,
                       String deviceModel,
                       String deviceType)
    {
        this.deviceName = deviceName;
        this.deviceYear = deviceYear;
        this.deviceModel = deviceModel;
        this.deviceType = deviceType;
    }
}
