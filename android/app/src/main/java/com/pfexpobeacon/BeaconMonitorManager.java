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
import com.facebook.react.bridge.WritableMap;
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

    public static final String PROMISE_MAC_ADDRESS = "macAddress";
    public static final String PROMISE_DISTANCE = "distance";

    private static final String TAG = "ReactNativeJS - Android";

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
        return "BeaconMonitor";
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
                    for (Beacon beacon : beacons) {
                        Log.d(TAG, "Mac addr: " + beacon.getBluetoothAddress() + " - Distance: " + beacon.getDistance());
                        //FIXME pasar array
                        sendEvent("didRangeBeaconsInRegion", createResponse(beacon));
                    }
                } else {
                    Log.d(TAG, "No beacons found.");
                }
            }
        });
    }

    /**
     * FIXME array delivered
     * private WritableMap createRangingResponse(Collection<Beacon> beacons, Region region) {
     * WritableMap map = new WritableNativeMap();
     * map.putString("identifier", region.getUniqueId());
     * map.putString("uuid", region.getId1() != null ? region.getId1().toString() : "");
     * WritableArray a = new WritableNativeArray();
     * for (Beacon beacon : beacons) {
     * WritableMap b = new WritableNativeMap();
     * b.putString("uuid", beacon.getId1().toString());
     * if (beacon.getIdentifiers().size() > 2) {
     * b.putInt("major", beacon.getId2().toInt());
     * b.putInt("minor", beacon.getId3().toInt());
     * }
     * b.putInt("rssi", beacon.getRssi());
     * if(beacon.getDistance() == Double.POSITIVE_INFINITY
     * || Double.isNaN(beacon.getDistance())
     * || beacon.getDistance() == Double.NaN
     * || beacon.getDistance() == Double.NEGATIVE_INFINITY){
     * b.putDouble("distance", 999.0);
     * b.putString("proximity", "far");
     * }else {
     * b.putDouble("distance", beacon.getDistance());
     * b.putString("proximity", getProximity(beacon.getDistance()));
     * }
     * a.pushMap(b);
     * }
     * map.putArray("beacons", a);
     * return map;
     * }
     */

    private WritableMap createResponse(@NonNull final Beacon beacon) {
        WritableMap map = Arguments.createMap();

        map.putString(PROMISE_MAC_ADDRESS, beacon.getBluetoothAddress());
        map.putDouble(PROMISE_DISTANCE, beacon.getDistance());

        return map;
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
