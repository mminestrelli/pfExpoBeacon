package com.pfexpobeacon;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import androidx.annotation.NonNull;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import static org.altbeacon.beacon.BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD;

public class BeaconMonitorManager extends ReactContextBaseJavaModule implements BeaconConsumer {

    public static final String PROMISE_MAC_ADDRESS = "macAddress";
    public static final String PROMISE_DISTANCE = "distance";

    private static final String TAG = "ReactNativeJS - Android";
    public static final String BEACON_MANAGER_DID_NOT_START_ERROR = "BEACON_MANAGER_DID_NOT_START_ERROR";
    public static final String BEACON_MANAGER_DID_NOT_STOP_ERROR = "BEACON_MANAGER_DID_NOT_STOP_ERROR";

    private BeaconManager beaconManager = null;
    private Region beaconRegion = null;
    private Promise promise;

    public BeaconMonitorManager(@Nonnull final ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "BeaconMonitor";
    }

    /**
     * 7. Once you have set the service connect method you're in position to start actually monitoring for a
     * beacon represented by region.
     * To do this you construct a region objectm and give it an Id to distinguish one region from another if you decide
     * to monitor for more than one region.
     * Then you supply the beacon identifiers so
     */
    @ReactMethod
    public void startBeaconMonitoring(@NonNull final Promise promise) {
        Log.d(TAG, "startBeaconMonitoring called");
        if (beaconManager != null && beaconManager.isBound(this) && beaconRegion != null) {
            Log.d(TAG, "beaconManager and beaconRegion already exists");
            return;
        }
        configureBeaconManager();

        try {
            //Region(UniqueId, UUID, major, minor)
            beaconRegion =
                new Region("MyBeacons", null, null, null);

            beaconManager.startMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
            this.promise = promise;
        } catch (Exception e) {
            //Tell react native about the exception
            promise.reject(BEACON_MANAGER_DID_NOT_START_ERROR, e);
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void stopBeaconMonitoring(@NonNull final Promise promise) {
        Log.d(TAG, "stopBeaconMonitoring called");
        if (beaconManager == null || beaconRegion == null) {
            return;
        }
        try {
            //beaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);
        } catch (Exception e) {
            e.printStackTrace();
            //Tell react native about the exception
            promise.reject(BEACON_MANAGER_DID_NOT_STOP_ERROR, e);
        }
        beaconManager.removeAllRangeNotifiers();
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.unbind(this);
    }

    private void configureBeaconManager() {
        if (beaconManager != null && beaconManager.isBound(this)) {
            Log.d(TAG, "beaconManager already exists");
            return;
        }

        //1. Fetch the beacon manager singleton for the app.
        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        //2. Set the beacon manager a beacon Pazza with a set layout.
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.EDDYSTONE_UID_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.EDDYSTONE_URL_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.URI_BEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().
            setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        //3. Bind the manager to a class tha implements the Android Beacon library beacon consumer interface.
        //In order to do that we can implement our MainActivity to implement the beacon consumer interface.
        beaconManager.bind(this);

        beaconManager.setBackgroundMode(false);
        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);
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
                    int i = 0;
                    Log.d(TAG, "Beacons found: " + beacons.size());
                    for (Beacon beacon : beacons) {
                        Log.d(TAG, "Beacon " + i + " found");
                        i++;
                        //Return react native beacon Info
                        //fixme promise.resolve() can be called just once. Array must be delivered.
                        resolvePromise(beacon);
                    }
                } else {
                    Log.d(TAG, "No beacons found.");
                }
            }
        });
    }

    private void resolvePromise(@NonNull final Beacon beacon) {
        if (promise != null) {
            WritableMap map = Arguments.createMap();

            map.putString(PROMISE_MAC_ADDRESS, beacon.getBluetoothAddress());
            map.putDouble(PROMISE_DISTANCE, beacon.getDistance());

            promise.resolve(map);
        }
    }

    private void addMonitorNotifier() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(final Region region) {

            }

            @Override
            public void didExitRegion(final Region region) {

            }

            @Override
            public void didDetermineStateForRegion(final int i, final Region region) {
                /* Not implemented */
            }
        });
    }

    @Override
    public Context getApplicationContext() {
        return getReactApplicationContext();
    }

    @Override
    public void unbindService(final ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(final Intent intent, final ServiceConnection serviceConnection, final int i) {
        return false;
    }
}
