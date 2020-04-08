package com.latkrong.sigstrmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.latkrong.sigstrmap.R;

public class HeatmapItemAdapter
        extends RecyclerView.Adapter<HeatmapItemAdapter.HeatmapItemViewHolder>
{
    private final ListItemClickListener onClickListener;
    private final String fileExtension;
    private String[] heatmapFiles;

    public HeatmapItemAdapter(final Context applicationContext, final String fileExtension,
                              final ListItemClickListener onClickListener)
    {
        this.fileExtension = fileExtension;
        this.heatmapFiles = applicationContext.getFilesDir()
                .list((dir, name) -> { return name.endsWith(this.fileExtension); });
        this.onClickListener = onClickListener;
    }

    @Override
    public HeatmapItemViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType)
    {
        final Context context = viewGroup.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.heatmap_list_item, viewGroup, false);
        return new HeatmapItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HeatmapItemViewHolder holder, final int i)
    {
        holder.bind(heatmapFiles[i]);
    }

    @Override
    public int getItemCount()
    {
        return heatmapFiles.length;
    }

    public void refreshFileList(final Context applicationContext)
    {
        heatmapFiles = applicationContext.getFilesDir()
                .list((dir, name) -> { return name.endsWith(this.fileExtension); });
        notifyDataSetChanged();
    }

    public interface ListItemClickListener
    {
        void onListItemClick(String filename);
    }

    class HeatmapItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView heatmapItemView;

        public HeatmapItemViewHolder(final View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            heatmapItemView = (TextView)itemView.findViewById(R.id.heatmap_list_item);
        }

        public void bind(final String heatmapName)
        {
            this.heatmapItemView.setText(heatmapName);
        }

        @Override
        public void onClick(final View view)
        {
            onClickListener.onListItemClick(heatmapItemView.getText().toString());
        }
    }
}
