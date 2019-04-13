package com.pfexpobeacon;

import android.support.annotation.NonNull;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import java.util.Map;

public class BulbManager extends SimpleViewManager<BulbView> {

    @Override
    @NonNull
    public String getName() {
        return "Bulb";
    }

    @Override
    @NonNull
    protected BulbView createViewInstance(@NonNull ThemedReactContext reactContext) {

        return new BulbView(reactContext);
    }

    @ReactProp(name = "isOn")
    public void setBulbStatus(BulbView bulbView, Boolean isOn) {
        bulbView.setIsOn(isOn);
    }

    /**
     * To map the statusChange event name created in BulbView to the onStatusChange callback prop in JavaScript,
     * register it by overriding the getExportedCustomBubblingEventTypeConstants method.
     */
    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
            .put(
                "statusChange",
                MapBuilder.of(
                    "phasedRegistrationNames",
                    MapBuilder.of("bubbled", "onStatusChange")))
            .build();
    }
}