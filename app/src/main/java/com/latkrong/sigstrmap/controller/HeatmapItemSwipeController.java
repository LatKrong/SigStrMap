package com.latkrong.sigstrmap.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.latkrong.sigstrmap.adapter.HeatmapItemAdapter;

public class HeatmapItemSwipeController extends ItemTouchHelper.SimpleCallback
{
    private final HeatmapItemAdapter adapter;

    private final Drawable deleteIcon;
    private final ColorDrawable deleteBackground;

    public HeatmapItemSwipeController(final HeatmapItemAdapter adapter,
                                      final Context applicationContext)
    {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;

        deleteIcon = ContextCompat.getDrawable(applicationContext, android.R.drawable.ic_menu_delete);
        deleteBackground = new ColorDrawable(Color.RED);
    }

    @Override
    public void onChildDraw(final Canvas c, final RecyclerView recyclerView,
                            final RecyclerView.ViewHolder viewHolder, final float dX,
                            final float dY, final int actionState, final boolean isCurrentlyActive)
    {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (dX == 0 && !isCurrentlyActive)
        {
            deleteBackground.setBounds(0, 0, 0, 0);
            deleteBackground.draw(c);
            return;
        }

        final View itemView = viewHolder.itemView;

        deleteBackground.setBounds(itemView.getRight() + (int)dX, itemView.getTop(),
                                   itemView.getRight(), itemView.getBottom());
        deleteBackground.draw(c);

        final int deleteIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        final int deleteIconTop =
                itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
        final int deleteIconBottom = deleteIconTop + deleteIcon.getIntrinsicHeight();

        final int deleteIconOffset = (int)Math.max(0, itemView.getHeight() + dX) - deleteIconMargin;
        final int deleteIconLeft =
                itemView.getRight() - deleteIcon.getIntrinsicWidth() + deleteIconOffset;
        final int deleteIconRight = itemView.getRight() + deleteIconOffset;

        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteIcon.draw(c);
    }

    @Override
    public boolean onMove(final RecyclerView recyclerView,
                          final RecyclerView.ViewHolder viewHolder,
                          final RecyclerView.ViewHolder target)
    {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction)
    {
        final int position = viewHolder.getAdapterPosition();
        adapter.removeItemRequest(position);
    }
}
