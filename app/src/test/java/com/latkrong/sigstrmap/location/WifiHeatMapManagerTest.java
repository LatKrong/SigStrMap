package com.latkrong.sigstrmap.location;

import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class WifiHeatMapManagerTest
{
    private WifiManager testWifiManager;
    private WifiHeatMapManager testWifiHeatMapManager;

    @Before
    public void init()
    {
        this.testWifiManager = Mockito.mock(WifiManager.class);
        this.testWifiHeatMapManager = new WifiHeatMapManager(testWifiManager);
    }

    @Test
    public void testSerializationAndDeserialization()
    {
        // Arrange

        final Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setPrettyPrinting()
                .create();

        // Act

        this.testWifiHeatMapManager.setWifiManager(null);
        addLocation(1.0, 2.0, 50);
        this.testWifiHeatMapManager.setWifiManager(this.testWifiManager);
        addLocation(3.0, 4.0, 100);

        // Assert

        Assert.assertEquals(
                gson.toJson(gson.fromJson("{" +
                                          "  \"heatMap\": {" +
                                          "    \"heatMap\": [" +
                                          "      [" +
                                          "        {" +
                                          "          \"latitude\": 2.9999971411251902," +
                                          "          \"longitude\": 3.999996171668123" +
                                          "        }," +
                                          "        {" +
                                          "          \"latest\": 254, \"avg\": 254.0," +
                                          "          \"max\": 254, \"min\": 254," +
                                          "          \"count\": 1" +
                                          "        }" +
                                          "      ]" +
                                          "    ]," +
                                          "    \"minLatitude\": 2.9999971411251902," +
                                          "    \"maxLatitude\": 2.9999971411251902," +
                                          "    \"minLongitude\": 3.999996171668123," +
                                          "    \"maxLongitude\": 3.999996171668123" +
                                          "  }," +
                                          "  \"latitude\": 3.0, \"longitude\": 4.0," +
                                          "  \"latitudeDelta\": 4.256111982846704E-6," +
                                          "  \"longitudeDelta\": 4.261952841093067E-6," +
                                          "  \"normalizedLatitude\": 2.9999971411251902," +
                                          "  \"normalizedLongitude\": 3.999996171668123," +
                                          "  \"index\": 254" +
                                          "}", WifiHeatMapManager.class)),
                gson.toJson(this.testWifiHeatMapManager));
    }

    private void addLocation(final double latitude, final double longitude, final int index)
    {
        final Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        final WifiInfo mockWifiInfo = Mockito.mock(WifiInfo.class);
        Mockito.when(mockWifiInfo.getRssi()).thenReturn(index);
        Mockito.when(this.testWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);

        this.testWifiHeatMapManager.addLocation(location);
    }
}
