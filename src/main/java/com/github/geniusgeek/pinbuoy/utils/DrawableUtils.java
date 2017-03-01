/*
 * Copyright (c) 2016.  Lukaround Inc ;This program is free software: you can &#10;
 * redistribute it and/or modify;it under the terms of the Lukaround Inc Public License as &#10;
 * published by Lukaround Software Foundation, either version 3 of the License or (at your option) any later version ;&#10;This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; &#10;without even the implied warranty of;MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.&#10;  See www.lukaround.org/developer/licence
 */

package com.github.geniusgeek.pinbuoy.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.VectorDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.geniusgeek.pinbuoy.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * Created by  Samuel Ekpe
 * User: Genius
 * Date: 4/23/16
 * Time: 7:35 AM
 */
public class DrawableUtils {
    /**
     * @throws AssertionError when trying to create an instance of this class
     */
    private DrawableUtils() {
        throw new AssertionError ( "cannot instantiate this class" );
    }


    /**
     * @param drawable
     * @return
     */
    public static Bitmap convertNinePatch(Drawable drawable) {
        Bitmap bmp = Bitmap.createBitmap ( 300, 300, Bitmap.Config.ARGB_8888 );

        Canvas canvas = new Canvas ( bmp );
        drawable.setBounds ( 0, 0, canvas.getWidth ( ), canvas.getHeight ( ) );
        drawable.draw ( canvas );
        return bmp;
    }

    /**
     * @param imageSpanList
     * @return
     */
    public static CharSequence getSpannable(List<ImageSpan> imageSpanList) {
        // SpannableStringBuilder spannableString= new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString ( " " );
        for (ImageSpan span : imageSpanList) {
            //   spannableString.append("", span, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan ( span, 0, spannableString.length ( ), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            break;
        }

        return spannableString;
    }

    /**
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap ( );
        }

        final int width = !drawable.getBounds ( ).isEmpty ( ) ? drawable
                .getBounds ( ).width ( ) : drawable.getIntrinsicWidth ( );

        final int height = !drawable.getBounds ( ).isEmpty ( ) ? drawable
                .getBounds ( ).height ( ) : drawable.getIntrinsicHeight ( );

        final Bitmap bitmap = Bitmap.createBitmap ( width <= 0 ? 1 : width,
                height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888 );

        Canvas canvas = new Canvas ( bitmap );
        drawable.setBounds ( 0, 0, canvas.getWidth ( ), canvas.getHeight ( ) );
        drawable.draw ( canvas );
        return bitmap;
    }

    public static BitmapDrawable drawableFromUrl(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL ( url ).openConnection ( );
        connection.connect ( );
        InputStream input = connection.getInputStream ( );

        Bitmap x = BitmapFactory.decodeStream ( input );
        return new BitmapDrawable ( x );
    }


    /**
     * a utitility function to change the view background solid drawable color
     * This can be used to indicate and switch between states in some application
     *
     * @param colorId
     * @param views
     */
    public static void changeViewBackgroundDrawableColor(Context context, int colorId, View... views) {
        for (View view : views) {
            Drawable background = view.getBackground ( );
            int color = context.getResources ( ).getColor ( colorId );
            setBackground ( background, color );
        }
    }

    /**
     * set background color
     *
     * @param background
     * @param color
     * @see <b>http://stackoverflow.com/questions/5940825/android-change-shape-color-in-runtime</b>
     * @see <b>http://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically</b>
     * @see <b>http://stackoverflow.com/questions/7164630/how-to-change-shape-color-dynamically</b>
     */
    public static void setBackground(Drawable background, int color) {
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint ( ).setColor ( color );
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor ( color );
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor ( color );
        }
    }


    /**
     * pass a view which is to be drawn with a prticulat color
     * @param view
     * @param color
     */
    public static void setDrawableColor(View view, int color) {
        Drawable background = view.getBackground();
        //background.mutate();
        setBackground(background,color);
    }


    public static int getThemeColor(Context context){
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.theme, typedValue, true))
            return typedValue.data;
        else
            return Color.TRANSPARENT;
    }


    /**
     * Create a circular shape
     */
    public static void makeShapes(Context context, Drawable background, int width, @ColorInt int color) {
        Shape s1 = new OvalShape ( );
        s1.resize ( width, width );
        ((ShapeDrawable) background).getPaint ( ).setColor ( color );
        ((ShapeDrawable) background).setShape ( s1 );
    }

    /**
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream ( );
        bitmap.compress ( Bitmap.CompressFormat.PNG, 100, stream );
        byte[] byteArray = stream.toByteArray ( );
        return byteArray;
    }

    /**
     * convert from bitmap to byte array
     *
     * @param bitmap
     * @return
     */
    public static String getBase64EncodedString(Bitmap bitmap) {

        return Base64.encodeToString ( bitmapToByteArray ( bitmap ),
                Base64.NO_WRAP );
    }


    /**
     * get a bitmap as a marker from a View {@link View }
     *
     * @param context
     * @param view
     * @return
     */
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics ( );
        ((Activity) context).getWindowManager ( ).getDefaultDisplay ( ).getMetrics ( displayMetrics );
        view.setLayoutParams ( new ViewGroup.LayoutParams ( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
        view.measure ( displayMetrics.widthPixels, displayMetrics.heightPixels );
        view.layout ( 0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels );
        view.buildDrawingCache ( );
        Bitmap bitmap = Bitmap.createBitmap ( view.getMeasuredWidth ( ), view.getMeasuredHeight ( ), Bitmap.Config.ARGB_8888 );

        Canvas canvas = new Canvas ( bitmap );
        view.draw ( canvas );

        return bitmap;
    }


    /**
     * @param string
     * @return
     */
    public static byte[] getBase64DecodedString(String string) {

        return Base64.decode ( string,
                Base64.NO_WRAP );
    }

    /**
     * @param drawable
     * @return
     */
    public static byte[] drawableToByteArray(Drawable drawable) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream ( );
        Bitmap bitmap = drawableToBitmap ( drawable );
        bitmap.compress ( Bitmap.CompressFormat.PNG, 100, stream );
        byte[] byteArray = stream.toByteArray ( );
        return byteArray;
    }


    /**
     * @param drawable
     * @param color
     * @return
     */
    public static Drawable changeDrawableColor(Drawable drawable, String color) {

        int iColor = Color.parseColor ( color );

        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = {0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0};

        ColorFilter colorFilter = new ColorMatrixColorFilter ( matrix );

        drawable.setColorFilter ( colorFilter );
        return drawable;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    @SuppressWarnings({"RestrictedApi"})
    private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int drawableResId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat) {
            return getBitmap((VectorDrawableCompat) drawable);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }

}
