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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.latkrong.sigstrmap.location.HeatMapManager;
import com.latkrong.sigstrmap.location.WifiHeatMapManager;
import com.latkrong.sigstrmap.view.WiFiHeatMapView;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class WifiHeatMapActivity extends AppCompatActivity
        implements EasyPermissions.PermissionCallbacks,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    public static final String HEAT_MAP_FILENAME = "HEAT_MAP_FILENAME";
    public static final String HEAT_MAP_FILE_EXTENSION = ".heatmap";
    private static final String HEAT_MAP_MANAGER_SAVE_KEY = "HeatMapManager";

    private static final int DEFAULT_MIN_DISTANCE = 0;
    private static final int RC_LOCATION = 1;

    private final LocationListener LOCATION_LISTENER =
            new WifiHeatMapActivity.MainLocationListener();

    private WifiManager wifiManager;
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
        setContentView(R.layout.activity_wifi_heat_map);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        this.heatMap = findViewById(R.id.wiFiHeatMap);
        this.wifiManager =
                (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        final Intent intent = getIntent();
        if ((savedInstanceState == null ||
                !savedInstanceState.containsKey(HEAT_MAP_MANAGER_SAVE_KEY)) &&
                !intent.hasExtra(HEAT_MAP_FILENAME))
        {
            this.heatMapManager = new WifiHeatMapManager(this.wifiManager);
        }
        else if (savedInstanceState != null &&
                savedInstanceState.containsKey(HEAT_MAP_MANAGER_SAVE_KEY))
        {
            this.heatMapManager =
                    GSON.fromJson(savedInstanceState.getString(HEAT_MAP_MANAGER_SAVE_KEY),
                                  WifiHeatMapManager.class);
            ((WifiHeatMapManager)this.heatMapManager).setWifiManager(this.wifiManager);
        }
        else
        {
            final String filename = intent.getStringExtra(HEAT_MAP_FILENAME);
            try (final FileInputStream fin = this.getApplicationContext().openFileInput(filename))
            {
                final String serialized = IOUtils.toString(fin);
                this.heatMapManager = GSON.fromJson(serialized, WifiHeatMapManager.class);
                ((WifiHeatMapManager)this.heatMapManager).setWifiManager(this.wifiManager);
            }
            catch (final IOException e)
            {
                Toast.makeText(this,
                               String.format("Failed to read data from file [%s]: %s", filename,
                                             e.getMessage()),
                               Toast.LENGTH_LONG)
                        .show();
                Log.e("DATA_STORAGE", String.format("Failed to read data from file [%s]", filename),
                      e);
                finishAndRemoveTask();
            }
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
        menuInflater.inflate(R.menu.wifi_heat_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            case R.id.settings:
                final Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.save:
                final String filename = this.wifiManager.getConnectionInfo().getSSID()
                        .replaceAll("\"", "") + HEAT_MAP_FILE_EXTENSION;
                final String serialized = GSON.toJson(this.heatMapManager);
                try (final FileOutputStream fout = this.getApplicationContext()
                        .openFileOutput(filename, Context.MODE_PRIVATE))
                {
                    fout.write(serialized.getBytes());
                    Toast.makeText(this, "Data saved to [" + filename + "]", Toast.LENGTH_LONG)
                            .show();
                }
                catch (final IOException e)
                {
                    Toast.makeText(this, "Failed to save data: " + e.getMessage(),
                                   Toast.LENGTH_LONG)
                            .show();
                    Log.e("DATA_STORAGE", "Failed to save data.", e);
                }
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
