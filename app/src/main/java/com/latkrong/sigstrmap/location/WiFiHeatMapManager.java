package com.latkrong.sigstrmap.location;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WiFiHeatMapManager extends HeatMapManager
{
    private final WifiManager wifiManager;

    public WiFiHeatMapManager(final WifiManager wifiManager)
    {
        super();

        this.wifiManager = wifiManager;
    }

    @Override
    public int currentIndex()
    {
        final WifiInfo wifiInfo = this.wifiManager.getConnectionInfo();
        return WifiManager
                .calculateSignalLevel(wifiInfo.getRssi(), HeatMapManager.HEAT_INDEX_LEVEL);
    }
}
