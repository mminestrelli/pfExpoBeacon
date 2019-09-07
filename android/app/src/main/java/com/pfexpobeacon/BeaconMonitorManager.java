package com.pfexpobeacon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import static org.altbeacon.beacon.BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD;

public class BeaconMonitorManager extends ReactContextBaseJavaModule implements BeaconConsumer {

    private static final String TAG = "ExpoBeacons - Android";
    private static final String I_BEACON_LAYOUT = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";

    private static final String REGION_UNIQUE_ID = "regionId";
    private static final String REGION_UUID = "regionUuid";

    private static final String BEACON_UUID = "uuid";
    private static final String BEACON_MAJOR_ID = "major";
    private static final String BEACON_MINOR_ID = "minor";
    private static final String BEACON_MAC_ADDRESS = "macAddress";
    private static final String BEACON_DISTANCE = "distance";
    private static final String BEACON_RSSI = "rssi";
    private static final String BEACON_PROXIMITY = "proximity";

    private static final String BEACON_PROXIMITY_UNKNOWN = "unknown";
    private static final String BEACON_PROXIMITY_IMMEDIATE = "immediate";
    private static final String BEACON_PROXIMITY_NEAR = "near";
    private static final String BEACON_PROXIMITY_FAR = "far";

    private static final String EVENT_BEACONS_RANGED = "EVENT_BEACONS_RANGED";
    private static final String EVENT_BEACONS_RANGE_STOPPED = "EVENT_BEACONS_RANGE_STOPPED";

    private BeaconManager beaconManager = null;
    private Region beaconRegion = null;
    private ReactApplicationContext reactContext;
    private Context applicationContext;

    BeaconMonitorManager(@Nonnull final ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.applicationContext = this.reactContext.getApplicationContext();
    }

    /**
     * Overrides ReactContextBaseJavaModule's getName method.
     * @return Module's name to be imported in React Native.
     */
    @Nonnull
    @Override
    public String getName() {
        return "BeaconManager";
    }

    /**
     * Exposes Module's constants to be used in React Native.
     * @return a map with the constants.
     */
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(REGION_UNIQUE_ID, REGION_UNIQUE_ID);
        constants.put(REGION_UUID, REGION_UUID);
        constants.put(BEACON_UUID, BEACON_UUID);
        constants.put(BEACON_MAJOR_ID, BEACON_MAJOR_ID);
        constants.put(BEACON_MINOR_ID, BEACON_MINOR_ID);
        constants.put(BEACON_MAC_ADDRESS, BEACON_MAC_ADDRESS);
        constants.put(BEACON_DISTANCE, BEACON_DISTANCE);
        constants.put(BEACON_RSSI, BEACON_RSSI);
        constants.put(BEACON_PROXIMITY, BEACON_PROXIMITY);
        constants.put(BEACON_PROXIMITY_UNKNOWN, BEACON_PROXIMITY_UNKNOWN);
        constants.put(BEACON_PROXIMITY_IMMEDIATE, BEACON_PROXIMITY_IMMEDIATE);
        constants.put(BEACON_PROXIMITY_NEAR, BEACON_PROXIMITY_NEAR);
        constants.put(EVENT_BEACONS_RANGED, EVENT_BEACONS_RANGED);
        constants.put(EVENT_BEACONS_RANGE_STOPPED, EVENT_BEACONS_RANGE_STOPPED);
        return constants;
    }

    /**
     * Overrides BeaconConsumer's method.
     * Called by the BeaconManager to get the context of your Service or Activity.
     *
     * @return the context of your Service or Activity.
     */
    @Override
    public Context getApplicationContext() {
        return applicationContext;
    }

    /**
     * Called by the BeaconManager to unbind your BeaconConsumer to the BeaconService.
     * @param serviceConnection to unbind your BeaconConsumer.
     */
    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        applicationContext.unbindService(serviceConnection);
    }

    /**
     * Called by the BeaconManager to bind your BeaconConsumer to the BeaconService.
     * @return true if service was bound, false otherwise.
     */
    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return applicationContext.bindService(intent, serviceConnection, i);
    }

    /**
     * Method exposed as a ReactMethod to be used in any React Native app which imports
     * "BeaconManager" Module.
     *
     * When the service connect method is set, use this function to start actually ranging a
     * beacon represented by region.
     * Region is constructed here with an Id, so this method only allows ranging only one region.
     */
    @ReactMethod
    public void startRangingBeacons() {
        Log.d(TAG, "startRangingBeacons called");

        requestPermissions();

        configureBeaconManager();

        // Binds BeaconManager to BeaconConsumer interface implemented in this class,
        // to use overridden methods.
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

    /**
     * Requests ACCESS_COARSE_LOCATION and ACCESS_COARSE_LOCATION for version codes greater than
     * 23.
     */
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getCurrentActivity().requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);
        }
    }

    /**
     * Fetches the beacon manager singleton and sets a parser with a layout.
     * In this case an IBeacon layout is used.
     */
    private void configureBeaconManager() {
        //Fetches the beacon manager singleton for the app.
        beaconManager = BeaconManager.getInstanceForApplication(applicationContext);
        //Sets the beacon manager a beacon parser with a set layout.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(I_BEACON_LAYOUT));
    }

    /**
     * Called when the beacon service is running and ready to accept commands through
     * the BeaconManager. For that purpose, a Range Notifier is set up to react to beacons events.
     */
    @Override
    public void onBeaconServiceConnect() {
        Log.d(TAG, "onBeaconServiceConnect called");
        addRangeNotifier();
    }

    /**
     * Specifies a class that should be called each time the BeaconService gets ranging data,
     * which is nominally once per second when beacons are detected.
     * Permits to register several RangeNotifier objects.
     * The notifier is unregistered in stopRangingBeacons method.
     */
    private void addRangeNotifier() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons,
                                                final Region region) {
                if (beacons != null && !beacons.isEmpty()) {
                    Log.d(TAG, "Beacons found: " + beacons.size());
                    sendEvent(EVENT_BEACONS_RANGED,
                            createResponse(beacons, region));
                } else {
                    Log.d(TAG, "No beacons found.");
                }
            }
        });
    }

    /**
     * Returns a map with the response for ReactNative's apps listening to this Module.
     * Response is build with region and found beacons data.
     *
     * @param beacons to build response.
     * @param region to build response.
     * @return a WritableMap with response to be sent to ReactNative's apps listening
     * to the Module.
     */
    private WritableMap createResponse(@NonNull final Collection<Beacon> beacons,
                                       @NonNull final Region region) {
        WritableMap map = Arguments.createMap();

        map.putString(REGION_UNIQUE_ID, region.getUniqueId());
        map.putString(REGION_UUID, region.getId1() != null ? region.getId1().toString() : "");
        WritableArray a = new WritableNativeArray();
        for (Beacon beacon : beacons) {
            Log.d(TAG, "Mac addr: " + beacon.getBluetoothAddress() + " - Distance: "
                    + beacon.getDistance());
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

    /**
     * Translates the beacon's distance to mobile device reading the signal into a readable
     * category called proximity.
     * @param distance to decide proximity.
     * @return beacon's proximity to the device as a String.
     */
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

    /**
     * Emits an event with a name and params to be listened in ReactNative apps listening to this
     * Module.
     *
     * @param eventName the event name to be send.
     * @param params the params to be send to ReactNative apps listening.
     */
    private void sendEvent(String eventName, @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    /**
     * Tells the BeaconService to stop looking for beacons that match the passed Region object
     * and providing mDistance information for them. Removes all the Range Notifiers, and unbinds
     * an Android Activity or Service to the BeaconService.
     */
    @ReactMethod
    public void stopRangingBeacons() {
        Log.d(TAG, "stopRangingBeacons called");
        try {
            beaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);
            beaconManager.removeAllRangeNotifiers();
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.unbind(this);
            //sendEvent("EVENT_BEACONS_RANGE_STOPPED", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
