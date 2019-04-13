package com.pfexpobeacon;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class BulbView extends AppCompatButton {

    public Boolean isOn = false;

    public BulbView(Context context) {
        super(context);
        setTextColor(Color.BLUE);
        setOnClickListener(changeStatusListener);
        updateButton();
    }

    public BulbView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BulbView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setIsOn(Boolean initialBulbStatus) {
        isOn = initialBulbStatus;
        updateButton();
    }

    private OnClickListener changeStatusListener = new OnClickListener() {
        public void onClick(View v) {
            isOn = !isOn;
            updateButton();
        }
    };

    private void updateButton() {
        WritableMap event = Arguments.createMap();
        event.putBoolean("isOn", isOn);
        ReactContext reactContext = (ReactContext)getContext();
        reactContext.getJSModule(RCTEventEmitter .class).receiveEvent(
            getId(),
            "statusChange",
            event);

        if (isOn) {
            setBackgroundColor(Color.YELLOW);
            setText("Switch OFF");
        } else {
            setBackgroundColor(Color.BLACK);
            setText("Switch ON");
        }
    }

}