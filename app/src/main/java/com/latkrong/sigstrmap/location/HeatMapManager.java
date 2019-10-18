package com.latkrong.sigstrmap.location;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class HeatMapManager
{
    protected static final int HEAT_INDEX_LEVEL = 255;
    protected static final double MIN_DISTANCE_APART_IN_METERS = 0.5;

    private static final int EARTH_RADIUS = 6731000;

    private final HeatMap heatMap;

    private double latitude;
    private double longitude;
    private double latitudeDelta;
    private double longitudeDelta;
    private double normalizedLatitude;
    private double normalizedLongitude;
    private int index;

    public HeatMapManager()
    {
        this.heatMap = new HeatMap();

        this.latitudeDelta = 0.0;
        this.longitudeDelta = 0.0;
    }

    public void addLocation(final Location location)
    {
        if (currentIndex() == -1) { return; }

        if (this.longitudeDelta == 0.0)
        {
            initializeDelta(location.getLatitude(), MIN_DISTANCE_APART_IN_METERS);
        }

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.normalizedLatitude = normalizeCoordinate(this.latitude, this.latitudeDelta);
        this.normalizedLongitude = normalizeCoordinate(this.longitude, this.longitudeDelta);
        this.index = currentIndex();

        this.heatMap.addLocation(this.normalizedLatitude, this.normalizedLongitude, this.index);
    }

    public List<HeatMapCellDrawInfo> getHeatMapCellDrawInfos(final int width, final int height,
                                                             final int margin)
    {
        final int cols = numCells(this.heatMap.getMaxLongitude(), this.heatMap.getMinLongitude(),
                                  this.longitudeDelta);
        final int rows = numCells(this.heatMap.getMaxLatitude(), this.heatMap.getMinLatitude(),
                                  this.latitudeDelta);
        final int cellWidth = (width - 2 * margin) / cols;
        final int cellHeight = (height - 2 * margin) / rows;

        final List<HeatMapCellDrawInfo> heatMapCellDrawInfos = new ArrayList<>();
        for (final Map.Entry<HeatMap.Location, HeatMap.HeatIndex> entry :
                this.heatMap.getHeatMap().entrySet())
        {
            final HeatMap.Location location = entry.getKey();
            final int col = cellIndex(location.getLongitude(), this.heatMap.getMinLongitude(),
                                      this.longitudeDelta);
            final int row = cellIndex(location.getLatitude(), this.heatMap.getMinLatitude(),
                                      this.latitudeDelta);

            heatMapCellDrawInfos.add(new HeatMapCellDrawInfo(margin + col * cellWidth,
                                                             margin + row * cellHeight,
                                                             cellWidth,
                                                             cellHeight,
                                                             entry.getValue().getLatest()));
        }
        return heatMapCellDrawInfos;
    }

    public String getDebugingString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append("Latitude             : ").append(this.latitude).append('\n');
        sb.append("Longitude            : ").append(this.longitude).append('\n');
        sb.append("Latitude Delta       : ").append(this.latitudeDelta).append('\n');
        sb.append("Longitude Delta      : ").append(this.latitudeDelta).append('\n');
        sb.append("Normalized Latitude  : ").append(this.normalizedLatitude).append('\n');
        sb.append("Normalized Longitude : ").append(this.normalizedLongitude).append('\n');
        sb.append("Current Index        : ").append(this.index);

        return sb.toString();
    }

    public abstract int currentIndex();

    protected double getLatitudeDelta()
    {
        return this.latitudeDelta;
    }

    protected double getLongitudeDelta()
    {
        return this.longitudeDelta;
    }

    protected void initializeDelta(final double latitude, final double minDistance)
    {
        this.latitudeDelta = 180 / Math.PI * (minDistance / EARTH_RADIUS);
        this.longitudeDelta = 180 / Math.PI * (minDistance / EARTH_RADIUS) /
                Math.cos(Math.toRadians(latitude));
    }

    protected static double normalizeCoordinate(final double coordinate, final double delta)
    {
        return (int)(coordinate / delta) * delta;
    }

    private int cellIndex(final double curr, final double min, final double delta)
    {
        return (int)Math.floor((curr - min) / delta);
    }

    private int numCells(final double max, final double min, final double delta)
    {
        return (int)Math.floor((max - min) / delta + 1);
    }
}
