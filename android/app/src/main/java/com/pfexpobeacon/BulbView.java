package com.pfexpobeacon;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

public class BulbView extends AppCompatButton {

    public Boolean isOn = false;

    public BulbView(Context context) {
        super(context);
        this.setTextColor(Color.BLUE);
        this.setOnClickListener(changeStatusListener);
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
        if (isOn) {
            setBackgroundColor(Color.YELLOW);
            setText("Switch OFF");
        } else {
            setBackgroundColor(Color.BLACK);
            setText("Switch ON");
        }
    }
}