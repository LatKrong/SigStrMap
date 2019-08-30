package com.latkrong.sigstrmap.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.latkrong.sigstrmap.location.HeatMapCellDrawInfo;

import java.util.List;

public class WiFiHeatMapView extends View
{
    public static final int DEFAULT_MARGIN = 100;
    public static final int DEFAULT_FONT_SIZE = 25;

    private Bitmap wifiHeapMap;
    private Canvas heatMapCanvas;
    private final Paint fillPaint;
    private List<HeatMapCellDrawInfo> heatMapCellDrawInfos;
    private String debugingString;

    public WiFiHeatMapView(final Context context, @Nullable final AttributeSet attrs)
    {
        super(context, attrs);

        this.fillPaint = new Paint();
        this.fillPaint.setStyle(Paint.Style.FILL);
        this.fillPaint.setTextSize(DEFAULT_FONT_SIZE);
        this.fillPaint.setTypeface(Typeface.MONOSPACE);
    }

    public void setHeatMapCellDrawInfos(final List<HeatMapCellDrawInfo> heatMapCellDrawInfos)
    {
        this.heatMapCellDrawInfos = heatMapCellDrawInfos;
    }

    public void setDebugingString(final String debugingString)
    {
        this.debugingString = debugingString;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        this.wifiHeapMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.heatMapCanvas = new Canvas(this.wifiHeapMap);
    }

    @Override
    protected void onDraw(final Canvas canvas)
    {
        if (heatMapCellDrawInfos == null || heatMapCellDrawInfos.isEmpty()) { return; }

        this.heatMapCanvas.drawColor(Color.WHITE);
        for (final HeatMapCellDrawInfo drawInfo : heatMapCellDrawInfos)
        {
            this.fillPaint.setColor(drawInfo.getColor());
            this.heatMapCanvas.drawRect(drawInfo.getRect(), this.fillPaint);
        }

        int textHeightOffset = 5;
        this.fillPaint.setColor(Color.BLACK);
        for (final String str : debugingString.split("\n"))
        {
            this.heatMapCanvas
                    .drawText(str, 5, textHeightOffset + DEFAULT_FONT_SIZE, this.fillPaint);
            textHeightOffset += DEFAULT_FONT_SIZE + 5;
        }

        canvas.drawBitmap(this.wifiHeapMap, 0, 0, null);
    }
}
