package com.latkrong.sigstrmap.location;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;

public class HeatMap
{
    private final HashMap<Location, HeatIndex> heatMap;

    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public HeatMap()
    {
        this.heatMap = new HashMap<>();
    }

    public void addLocation(final double latitude, final double longitude, final int index)
    {
        if (this.heatMap.isEmpty())
        {
            this.minLatitude = latitude;
            this.maxLatitude = latitude;
            this.minLongitude = longitude;
            this.maxLongitude = longitude;
        }
        else
        {
            this.minLatitude = Math.min(latitude, this.minLatitude);
            this.maxLatitude = Math.max(latitude, this.maxLatitude);
            this.minLongitude = Math.min(longitude, this.minLongitude);
            this.maxLongitude = Math.max(longitude, this.maxLongitude);
        }

        final Location location = new Location(latitude, longitude);
        if (this.heatMap.containsKey(location))
        {
            this.heatMap.get(location).addIndex(index);
        }
        else
        {
            this.heatMap.put(location, new HeatIndex(index));
        }
    }

    public HashMap<Location, HeatIndex> getHeatMap()
    {
        return this.heatMap;
    }

    public double getMinLatitude()
    {
        return this.minLatitude;
    }

    public double getMaxLatitude()
    {
        return this.maxLatitude;
    }

    public double getMinLongitude()
    {
        return this.minLongitude;
    }

    public double getMaxLongitude()
    {
        return this.maxLongitude;
    }

    public class Location
    {
        private final double latitude;
        private final double longitude;

        public Location(final double latitude, final double longitude)
        {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude()
        {
            return this.latitude;
        }

        public double getLongitude()
        {
            return this.longitude;
        }

        @Override
        public boolean equals(final Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    public class HeatIndex
    {
        private int latest;
        private double avg;
        private int max;
        private int min;
        private int count;

        public HeatIndex(final int index)
        {
            this.latest = index;
            this.avg = index;
            this.min = index;
            this.max = index;
            this.count = 1;
        }

        public void addIndex(final int index)
        {
            this.latest = index;
            this.avg = (this.avg * this.count + index) / (this.count + 1);
            this.min = Math.min(index, this.min);
            this.max = Math.max(index, this.max);
            ++this.count;
        }

        public int getLatest() { return this.latest; }

        public int getAvg() { return (int)Math.round(this.avg); }

        public int getMax() { return this.max; }

        public int getMin() { return this.min; }
    }
}
