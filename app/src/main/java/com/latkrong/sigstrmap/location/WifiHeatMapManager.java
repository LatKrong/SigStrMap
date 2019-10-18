package com.latkrong.sigstrmap.location;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiHeatMapManager extends HeatMapManager
{
    private transient WifiManager wifiManager;

    public WifiHeatMapManager(final WifiManager wifiManager)
    {
        super();

        this.wifiManager = wifiManager;
    }

    public void setWifiManager(final WifiManager wifiManager)
    {
        this.wifiManager = wifiManager;
    }

    @Override
    public int currentIndex()
    {
        if (this.wifiManager == null) { return -1; }

        final WifiInfo wifiInfo = this.wifiManager.getConnectionInfo();
        return WifiManager
                .calculateSignalLevel(wifiInfo.getRssi(), HeatMapManager.HEAT_INDEX_LEVEL);
    }
}
