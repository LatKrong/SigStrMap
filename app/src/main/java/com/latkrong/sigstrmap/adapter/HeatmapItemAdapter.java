package com.latkrong.sigstrmap.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.latkrong.sigstrmap.R;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HeatmapItemAdapter
        extends RecyclerView.Adapter<HeatmapItemAdapter.HeatmapItemViewHolder>
{
    private final Context applicationContext;
    private final String fileExtension;
    private final ListItemChangeListener onChangeListener;

    private List<String> heatmapFiles;

    public HeatmapItemAdapter(final Context applicationContext, final String fileExtension,
                              final ListItemChangeListener onChangeListener)
    {
        this.applicationContext = applicationContext;
        this.fileExtension = fileExtension;
        this.heatmapFiles = getFileList();
        this.onChangeListener = onChangeListener;
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
        holder.bind(this.heatmapFiles.get(i));
    }

    @Override
    public int getItemCount()
    {
        return this.heatmapFiles.size();
    }

    public void refreshFileList()
    {
        this.heatmapFiles = getFileList();
        notifyDataSetChanged();
    }

    public void removeItemRequest(final int position)
    {
        this.onChangeListener.onListItemDeleteRequest(this.heatmapFiles.get(position), position);
    }

    public void removeItem(final int position)
    {
        applicationContext.deleteFile(heatmapFiles.get(position) + fileExtension);
        heatmapFiles.remove(position);
        notifyItemRemoved(position);
    }

    private List<String> getFileList()
    {
        return Arrays.asList(this.applicationContext.getFilesDir().list((dir, name) -> {
            return name.endsWith(this.fileExtension);
        })).stream().map(name -> {
            return name.substring(0, name.length() - this.fileExtension.length());
        }).sorted().collect(Collectors.toList());
    }

    public interface ListItemChangeListener
    {
        void onListItemSelected(String filename);

        void onListItemDeleteRequest(String id, int position);
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
            onChangeListener
                    .onListItemSelected(heatmapItemView.getText().toString() + fileExtension);
        }
    }
}
