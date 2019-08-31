package com.pfexpobeacon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import static org.altbeacon.beacon.BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD;

public class BeaconMonitorManager extends ReactContextBaseJavaModule implements BeaconConsumer {

    private static final String TAG = "ReactNativeJS - Android";

    public static final String REGION_UNIQUE_ID = "identifier";
    public static final String REGION_UUID = "uuid";

    public static final String BEACON_UUID = "uuid";
    public static final String BEACON_MAJOR_ID = "major";
    public static final String BEACON_MINOR_ID = "minor";
    public static final String BEACON_MAC_ADDRESS = "macAddress";
    public static final String BEACON_DISTANCE = "distance";
    public static final String BEACON_RSSI = "rssi";
    public static final String BEACON_PROXIMITY = "proximity";

    public static final String BEACON_PROXIMITY_UNKNOWN = "unknown";
    public static final String BEACON_PROXIMITY_IMMEDIATE = "immediate";
    public static final String BEACON_PROXIMITY_NEAR = "near";
    public static final String BEACON_PROXIMITY_FAR = "far";

    private BeaconManager beaconManager = null;
    private Region beaconRegion = null;
    private ReactApplicationContext reactContext;
    private Context applicationContext;

    public BeaconMonitorManager(@Nonnull final ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.applicationContext = this.reactContext.getApplicationContext();
    }

    @Nonnull
    @Override
    public String getName() {
        return "BeaconManager";
    }

    @Override
    public Context getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        applicationContext.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return applicationContext.bindService(intent, serviceConnection, i);
    }

    /**
     * 7. Once you have set the service connect method you're in position to start actually monitoring for a
     * beacon represented by region.
     * To do this you construct a region objectm and give it an Id to distinguish one region from another if you decide
     * to monitor for more than one region.
     * Then you supply the beacon identifiers so
     */
    @ReactMethod
    public void startRangingBeacons() {
        Log.d(TAG, "startRangingBeacons called");

        requestPermissions();

        configureBeaconManager();

        //3. Bind the manager to a class tha implements the Android Beacon library beacon consumer interface.
        //In order to do that we can implement our MainActivity to implement the beacon consumer interface.
        beaconManager.bind(this);

        beaconManager.setBackgroundMode(false);
        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);

        try {
            //Region(UniqueId, UUID, major, minor)
            beaconRegion =
                    new Region("MyBeacons", null, null, null);

            beaconManager.startMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getCurrentActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
        }
    }

    private void configureBeaconManager() {
        //1. Fetch the beacon manager singleton for the app.
        beaconManager = BeaconManager.getInstanceForApplication(applicationContext);
        //2. Set the beacon manager a beacon Pazza with a set layout.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
    }

    @Override
    public void onBeaconServiceConnect() {
        //4. When the beacon service starts, this is called, so you can set up notifiers to react to
        //beacon events.
        Log.d(TAG, "onBeaconServiceConnect called");

        //5. First, we can set up a monitor notifier which allows you to define methods
        // did enter or exit region. This methods are called when a mobile device enters or exits a beacon region.
        //addMonitorNotifier();

        //6. Now conversely if rather than monitoring for the device entering or
        // exiting the beacon region you want to range instead, you separately set up a range notifier.
        addRangeNotifier();
    }

    private void addRangeNotifier() {
        //Specifies a class that should be called each time the BeaconService gets ranging data,
        // which is nominally once per second when beacons are detected.
        //Permits to register several RangeNotifier objects.
        //The notifier must be unregistered using (@link #removeRangeNotifier)
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, final Region region) {
                if (beacons != null && !beacons.isEmpty()) {
                    Log.d(TAG, "Beacons found: " + beacons.size());
                    sendEvent("didRangeBeaconsInRegion", createResponse(beacons, region));
                } else {
                    Log.d(TAG, "No beacons found.");
                }
            }
        });
    }

    private WritableMap createResponse(@NonNull final Collection<Beacon> beacons, @NonNull final Region region) {
        WritableMap map = Arguments.createMap();

        map.putString(REGION_UNIQUE_ID, region.getUniqueId());
        map.putString(REGION_UUID, region.getId1() != null ? region.getId1().toString() : "");
        WritableArray a = new WritableNativeArray();
        for (Beacon beacon : beacons) {
            Log.d(TAG, "Mac addr: " + beacon.getBluetoothAddress() + " - Distance: " + beacon.getDistance());
            WritableMap b = Arguments.createMap();
            b.putString(BEACON_MAC_ADDRESS, beacon.getBluetoothAddress());
            b.putString(BEACON_UUID, beacon.getId1().toString());
            if (beacon.getIdentifiers().size() > 2) {
                b.putInt(BEACON_MAJOR_ID, beacon.getId2().toInt());
                b.putInt(BEACON_MINOR_ID, beacon.getId3().toInt());
            }
            b.putInt(BEACON_RSSI, beacon.getRssi());
            if (beacon.getDistance() == Double.POSITIVE_INFINITY
                    || Double.isNaN(beacon.getDistance())
                    || beacon.getDistance() == Double.NaN
                    || beacon.getDistance() == Double.NEGATIVE_INFINITY) {
                b.putDouble(BEACON_DISTANCE, 999.0);
                b.putString(BEACON_PROXIMITY, "far");
            } else {
                b.putDouble(BEACON_DISTANCE, beacon.getDistance());
                b.putString(BEACON_PROXIMITY, getProximity(beacon.getDistance()));
            }
            a.pushMap(b);
        }
        map.putArray("beacons", a);

        return map;
    }

    private String getProximity(double distance) {
        if (distance == -1.0) {
            return BEACON_PROXIMITY_UNKNOWN;
        } else if (distance < 1) {
            return BEACON_PROXIMITY_IMMEDIATE;
        } else if (distance < 3) {
            return BEACON_PROXIMITY_NEAR;
        } else {
            return BEACON_PROXIMITY_FAR;
        }
    }

    private void sendEvent(String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void stopRangingBeacons() {
        Log.d(TAG, "stopRangingBeacons called");
        try {
            beaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);
            beaconManager.removeAllRangeNotifiers();
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.unbind(this);
            //sendEvent("stopRangingBeacons", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
