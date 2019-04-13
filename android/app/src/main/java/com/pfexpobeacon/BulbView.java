package com.pfexpobeacon;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatButton;

public class BulbView extends AppCompatButton {

    public BulbView(Context context) {
        super(context);
        setTextColor(Color.BLUE);
        setText("This button is created from JAVA code");
    }

    public BulbView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BulbView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}