package com.latkrong.sigstrmap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.latkrong.sigstrmap.adapter.HeatmapItemAdapter;

public class MainActivity extends AppCompatActivity
        implements HeatmapItemAdapter.ListItemClickListener
{
    private HeatmapItemAdapter heatmapItemAdapter;
    private RecyclerView heatmapList;

    private FloatingActionButton addHeatmap;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heatmapList = (RecyclerView)findViewById(R.id.heatmap_list);
        heatmapList.setLayoutManager(new LinearLayoutManager(this));
        heatmapList.setHasFixedSize(true);
        heatmapList.addItemDecoration(new DividerItemDecoration(this.getApplicationContext(),
                                                                DividerItemDecoration.VERTICAL));

        heatmapItemAdapter = new HeatmapItemAdapter(this.getApplicationContext(),
                                                    WifiHeatMapActivity.HEAT_MAP_FILE_EXTENSION,
                                                    this);
        heatmapList.setAdapter(heatmapItemAdapter);

        final WifiManager wifiManager =
                (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("Creating heatmap for %s?",
                                         wifiManager.getConnectionInfo().getSSID()))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id)
                    {
                        final Intent intent =
                                new Intent(MainActivity.this, WifiHeatMapActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", null);
        final AlertDialog addHeatmapDialog = builder.create();

        addHeatmap = findViewById(R.id.add_heatmap);
        addHeatmap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                addHeatmapDialog.show();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.heatmapItemAdapter.refreshFileList(this.getApplicationContext());
    }

    @Override
    public void onListItemClick(final String filename)
    {
        final Intent intent =
                new Intent(MainActivity.this, WifiHeatMapActivity.class);
        intent.putExtra(WifiHeatMapActivity.HEAT_MAP_FILENAME, filename);
        startActivity(intent);
    }
}
