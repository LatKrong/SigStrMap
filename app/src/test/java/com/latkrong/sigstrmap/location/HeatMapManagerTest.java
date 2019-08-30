package com.latkrong.sigstrmap.location;

import android.location.Location;
import android.location.LocationManager;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class HeatMapManagerTest
{
    private static final double TEST_START_LATITUDE = 37.422;
    private static final double TEST_START_LONGITUDE = 122.084;

    private static final int TEST_HEIGHT = 300;
    private static final int TEST_WIDTH = 100;
    private static final int TEST_MARGIN = 5;

    private int testCurrentIndex = 0;
    private HeatMapManager testHeatMapManager;

    @Before
    public void init()
    {
        testHeatMapManager = new HeatMapManager()
        {
            @Override
            public int currentIndex()
            {
                return testCurrentIndex;
            }
        };
    }

    @Test
    public void testGetHeatMapCellDrawInfos_WithValidInput_Success()
    {
        // Arrange

        // Initialize with starting location.
        addLocation(TEST_START_LATITUDE, TEST_START_LONGITUDE, 1);
        // Adding location for adjacent cells.
        addLocation(TEST_START_LATITUDE + testHeatMapManager.getLatitudeDelta(),
                    TEST_START_LONGITUDE, 2);
        addLocation(TEST_START_LATITUDE,
                    TEST_START_LONGITUDE + 2 * testHeatMapManager.getLongitudeDelta(), 3);
        addLocation(TEST_START_LATITUDE + 5 * testHeatMapManager.getLatitudeDelta(),
                    TEST_START_LONGITUDE + 7 * testHeatMapManager.getLongitudeDelta(), 4);
        // Adding location for existing cells.
        addLocation(HeatMapManager.normalizeCoordinate(TEST_START_LATITUDE,
                                                       testHeatMapManager.getLatitudeDelta()) +
                            testHeatMapManager.getLatitudeDelta() / 2,
                    TEST_START_LONGITUDE, 5);
        addLocation(TEST_START_LATITUDE,
                    HeatMapManager.normalizeCoordinate(TEST_START_LONGITUDE,
                                                       testHeatMapManager.getLongitudeDelta()) +
                            2.5 * testHeatMapManager.getLongitudeDelta(), 6);

        // Act

        final List<HeatMapCellDrawInfo> actualDrawInfos =
                testHeatMapManager.getHeatMapCellDrawInfos(TEST_WIDTH, TEST_HEIGHT, TEST_MARGIN);

        // Assert

        // Latitude = y, Longitude = x, LatitudeDelta = CellHeight, LongitudeDelta = CellWidth.
        // Longitude related index are 1 less than actual, might be due to some rounding issues.
        final int cols = 8;
        final int rows = 5;
        final int cellWidth = (TEST_WIDTH - 2 * TEST_MARGIN) / cols;
        final int cellHeight = (TEST_HEIGHT - 2 * TEST_MARGIN) / rows;
        final List<HeatMapCellDrawInfo> expectedDrawInfos = Arrays.asList(
                new HeatMapCellDrawInfo(TEST_MARGIN, TEST_MARGIN, cellWidth, cellHeight, 5),
                new HeatMapCellDrawInfo(TEST_MARGIN, TEST_MARGIN + cellHeight, cellWidth,
                                        cellHeight,
                                        2),
                new HeatMapCellDrawInfo(TEST_MARGIN + cellWidth, TEST_MARGIN, cellWidth,
                                        cellHeight, 6),
                new HeatMapCellDrawInfo(TEST_MARGIN + 7 * cellWidth, TEST_MARGIN + 4 * cellHeight,
                                        cellWidth, cellHeight, 4));
        Assert.assertThat(
                actualDrawInfos,
                IsIterableContainingInAnyOrder.containsInAnyOrder(expectedDrawInfos.toArray()));
    }

    private void addLocation(final double latitude, final double longitude, final int index)
    {
        final Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        this.testCurrentIndex = index;
        this.testHeatMapManager.addLocation(location);
    }
}
