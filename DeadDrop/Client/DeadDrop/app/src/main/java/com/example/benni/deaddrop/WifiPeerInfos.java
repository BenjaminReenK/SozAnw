package com.example.benni.deaddrop;

/**
 * Created by Benni on 29.11.2015.
 */
public class WifiPeerInfos {

    private String deviceName;
    private String deviceAddress;
    private String tcpAddress;

    public WifiPeerInfos() {
        this.deviceName = "";
        this.deviceAddress = "";
        this.tcpAddress = "";
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getTcpAddress() {
        return tcpAddress;
    }

    public void setTcpAddress(String tcpAddress) {
        this.tcpAddress = tcpAddress;
    }
}
