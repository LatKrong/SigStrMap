package com.latkrong.sigstrmap.location;

import android.graphics.Color;
import android.graphics.Rect;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class HeatMapCellDrawInfo
{
    private final Rect rect;
    private final int color;

    public HeatMapCellDrawInfo(final int x, final int y, final int width, final int height,
                               final int index)
    {
        this.rect = new Rect(x, y, x + width, y + height);
        this.color = Color.rgb(255, 255 - index, 0);
    }

    public Rect getRect()
    {
        return this.rect;
    }

    public int getColor()
    {
        return this.color;
    }

    @Override
    public boolean equals(final Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        sb.append('[');
        sb.append('[')
                .append(this.rect.left).append(", ")
                .append(this.rect.top).append(", ")
                .append(this.rect.right).append(", ")
                .append(this.rect.bottom).append("], ");
        sb.append(String.format("%X ", this.color));
        sb.append(']');

        return sb.toString();
    }
}
