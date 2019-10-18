package com.latkrong.sigstrmap.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Assert;
import org.junit.Test;

public class HeatMapTest
{
    @Test
    public void testSerializationAndDeserialization()
    {
        // Arrange

        final Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();

        // Act

        final HeatMap heatMap = new HeatMap();
        heatMap.addLocation(1.0, 2.0, 200);
        heatMap.addLocation(5.278, 4.97, 100);
        heatMap.addLocation(1.0, 2.0, 150);

        // Assert

        Assert.assertEquals(
                gson.toJson(gson.fromJson("{" +
                                          "  \"heatMap\": [" +
                                          "    [" +
                                          "      { \"latitude\": 1.0, \"longitude\": 2.0 }," +
                                          "      {" +
                                          "        \"latest\": 150, \"avg\": 175.0," +
                                          "        \"max\": 200, \"min\": 150, \"count\": 2" +
                                          "      }" +
                                          "    ]," +
                                          "    [" +
                                          "      { \"latitude\": 5.278, \"longitude\": 4.97 }," +
                                          "      {" +
                                          "        \"latest\": 100, \"avg\": 100.0," +
                                          "        \"max\": 100, \"min\": 100, \"count\": 1" +
                                          "      }" +
                                          "    ]" +
                                          "  ]," +
                                          "  \"minLatitude\": 1.0, \"maxLatitude\": 5.278," +
                                          "  \"minLongitude\": 2.0, \"maxLongitude\": 4.97" +
                                          "}", HeatMap.class)),
                gson.toJson(heatMap));
    }
}
