package com.latkrong.sigstrmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.latkrong.sigstrmap.location.HeatMapManager;
import com.latkrong.sigstrmap.location.WiFiHeatMapManager;
import com.latkrong.sigstrmap.view.WiFiHeatMapView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks
{
    static final int DEFAULT_UPDATE_INTERVAL_IN_MILLIS = 0;
    static final int DEFAULT_MIN_DISTANCE = 0;
    static final int RC_LOCATION = 1;

    WiFiHeatMapView heatMap;
    HeatMapManager heatMapManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.heatMap = findViewById(R.id.wiFiHeatMap);
        this.heatMapManager = new WiFiHeatMapManager(
                (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE));

        setupLocationListener();
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, final String[] permissions, final int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(final int requestCode, @NonNull final List<String> perms) {}

    @Override
    public void onPermissionsDenied(final int requestCode, @NonNull final List<String> perms)
    {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
        {
            new AppSettingsDialog.Builder(this).build().show();
        }
        else
        {
            finishAndRemoveTask();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE)
        {
            if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {
                finishAndRemoveTask();
            }
        }
    }

    @AfterPermissionGranted(RC_LOCATION)
    @SuppressLint("MissingPermission")
    private void setupLocationListener()
    {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            final LocationManager locationManager =
                    (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                   DEFAULT_UPDATE_INTERVAL_IN_MILLIS,
                                                   DEFAULT_MIN_DISTANCE,
                                                   new MainLocationListener());
        }
        else
        {
            EasyPermissions.requestPermissions(
                    this,
                    this.getString(R.string.location_permission_rationale),
                    RC_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private class MainLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(final Location location)
        {
            heatMapManager.addLocation(location);

            if (heatMap.getWidth() != 0 && heatMap.getHeight() != 0)
            {
                heatMap.setHeatMapCellDrawInfos(heatMapManager.getHeatMapCellDrawInfos(
                        heatMap.getWidth(),
                        heatMap.getHeight(),
                        WiFiHeatMapView.DEFAULT_MARGIN));
                heatMap.setDebugingString(heatMapManager.getDebugingString());
                heatMap.invalidate();
            }
        }

        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras) {}

        @Override
        public void onProviderEnabled(final String provider) {}

        @Override
        public void onProviderDisabled(final String provider) {}
    }
}
