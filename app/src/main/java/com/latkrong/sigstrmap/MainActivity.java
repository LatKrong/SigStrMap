package com.latkrong.sigstrmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.latkrong.sigstrmap.location.HeatMapManager;
import com.latkrong.sigstrmap.location.WifiHeatMapManager;
import com.latkrong.sigstrmap.view.WiFiHeatMapView;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String HEAT_MAP_MANAGER_SAVE_KEY = "HeatMapManager";

    private static final int DEFAULT_MIN_DISTANCE = 0;
    private static final int RC_LOCATION = 1;

    private final LocationListener LOCATION_LISTENER = new MainLocationListener();

    private WiFiHeatMapView heatMap;
    private HeatMapManager heatMapManager;

    private int updateIntervalMillis = 0;

    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .create();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        this.heatMap = findViewById(R.id.wiFiHeatMap);

        if (savedInstanceState == null ||
                !savedInstanceState.containsKey(HEAT_MAP_MANAGER_SAVE_KEY))
        {
            this.heatMapManager = new WifiHeatMapManager(
                    (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        }
        else
        {
            this.heatMapManager =
                    GSON.fromJson(savedInstanceState.getString(HEAT_MAP_MANAGER_SAVE_KEY),
                                  WifiHeatMapManager.class);
            ((WifiHeatMapManager)this.heatMapManager).setWifiManager(
                    (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        }

        this.updateIntervalMillis = sharedPreferences
                .getInt(getString(R.string.settings_update_interval_in_millis_key), getResources()
                                .getInteger(R.integer.settings_update_interval_in_millis_default));

        setupLocationListener();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(HEAT_MAP_MANAGER_SAVE_KEY, GSON.toJson(this.heatMapManager));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings:
                final Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                                                   this.updateIntervalMillis,
                                                   DEFAULT_MIN_DISTANCE,
                                                   LOCATION_LISTENER);
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

    @Override
    @SuppressLint("MissingPermission")
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                                          final String key)
    {
        if (key.equals(getString(R.string.settings_update_interval_in_millis_key)))
        {
            this.updateIntervalMillis = sharedPreferences
                    .getInt(key,
                            getResources().getInteger(
                                    R.integer.settings_update_interval_in_millis_default));

            final LocationManager locationManager =
                    (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(LOCATION_LISTENER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                                   this.updateIntervalMillis,
                                                   DEFAULT_MIN_DISTANCE,
                                                   LOCATION_LISTENER);
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
