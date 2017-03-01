package com.github.geniusgeek.pinbuoy;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by root on 12/7/16.
 */

public class Ribbon extends ImageView{
    private Drawable roundRibbon;

    public Ribbon(Context context) {
        super(context);
    }

    public Ribbon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Ribbon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Ribbon(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Drawable getRoundRibbon() {
        return roundRibbon;
    }

    public void setRoundRibbon(Drawable roundRibbon) {
        this.roundRibbon = roundRibbon;
        //this.roundRibbon.setBounds(0,0,20,20);
    }
}
