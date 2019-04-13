package com.pfexpobeacon;

import android.support.annotation.NonNull;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

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
}