package com.pfexpobeacon;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.Collection;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import static org.altbeacon.beacon.BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD;

public class BeaconMonitorActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "Altbeacon";
    private BeaconManager beaconManager = null;
    private boolean entryMessageRaised = false;
    private boolean exitMessageRaised = false;
    private Region beaconRegion = null;

    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_monitor_activity);

        configureViews();

        requestPermissions();

        configureBeaconManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                1234);
        }
    }

    private void configureViews() {
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startBeaconMonitoring();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                stopBeaconMonitoring();
            }
        });
    }

    private void configureBeaconManager() {
        //1. Fetch the beacon manager singleton for the app.
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //2. Set the beacon manager a beacon Pazza with a set layout.
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.EDDYSTONE_UID_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.EDDYSTONE_URL_LAYOUT));
        //beaconManager.getBeaconParsers().add(new BeaconParser(BeaconParser.URI_BEACON_LAYOUT));
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
                    int i = 0;
                    Log.d(TAG, "Beacons found: " + beacons.size());
                    for (Beacon beacon : beacons) {
                        Log.d(TAG, "Beacon " + i + " found");
                        i++;
                        showAlert("didRangeBeaconsInRegion", "Ranging region " + region.getUniqueId() +
                            "Beacon detected UUID/major/minor: " + beacon.getId1() +
                            "/" + beacon.getId2() + "/" + beacon.getId3());
                    }
                } else {
                    Log.d(TAG, "No beacons found.");
                }
            }
        });
    }

    private void addMonitorNotifier() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(final Region region) {
                if (!entryMessageRaised) {
                    showAlert("didEnterRegion",
                        "Entering region " + region.getUniqueId() + " Beacon detected UUID/major/minor: " +
                            region.getId1() + "/" +
                            region.getId2() + "/" + region.getId3());
                    entryMessageRaised = true;
                }
            }

            @Override
            public void didExitRegion(final Region region) {
                if (!exitMessageRaised) {
                    showAlert("didExitRegion",
                        "Exiting region " + region.getUniqueId() + " Beacon detected UUID/major/minor: " +
                            region.getId1() + "/" +
                            region.getId2() + "/" + region.getId3());
                    exitMessageRaised = true;
                }
            }

            @Override
            public void didDetermineStateForRegion(final int i, final Region region) {
                /* Not implemented */
            }
        });
    }

    /**
     * 7. Once you have set the service connect method you're in position to start actually monitoring for a
     * beacon represented by region.
     * To do this you construct a region objectm and give it an Id to distinguish one region from another if you decide
     * to monitor for more than one region.
     * Then you supply the beacon identifiers so
     */
    private void startBeaconMonitoring() {
        Log.d(TAG, "startBeaconMonitoring called");

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

            stopButton.setEnabled(true);
            startButton.setEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopBeaconMonitoring() {
        Log.d(TAG, "stopBeaconMonitoring called");
        try {
            beaconManager.stopMonitoringBeaconsInRegion(beaconRegion);
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        beaconManager.removeAllRangeNotifiers();
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.unbind(this);
    }

    private void showAlert(final String title, final String message) {
        Log.d(TAG, "showAlert called");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(BeaconMonitorActivity.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                        entryMessageRaised = false;
                    }
                });
                alertDialog.show();
            }
        });
    }
}
