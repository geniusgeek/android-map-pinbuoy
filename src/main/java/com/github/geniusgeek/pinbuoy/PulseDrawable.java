package com.github.geniusgeek.pinbuoy;

/*
 * Copyright Txus Ballesteros 2015 (@txusballesteros)
 *
 * This file is part of some open source application.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contact: Txus Ballesteros <txus.ballesteros@gmail.com>
 */
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.animation.DecelerateInterpolator;

public class PulseDrawable extends Drawable {
    private final static float CENTER_AREA_SIZE = 0.6f;
    private final static int PULSE_START_COLOR_OPACITY = 0;
    private final static float MINIMUM_RADIUS = 0;
    private final static int ANIMATION_DURATION_IN_MS = 1500;
    private final int color;
    private Paint centerPaint;
    private Paint pulsePaint;
    private float fullSizeRadius;
    private float currentExpandAnimationValue = 0f;
    private int currentAlphaAnimationValue = 255;
    private AnimatorSet animation = new AnimatorSet();

    public PulseDrawable(int color) {
        this.color = color;
        initializeDrawable();
    }

    private void initializeDrawable() {
        preparePaints();
        prepareAnimation();
    }

    public void prepareAnimation() {
        final ValueAnimator expandAnimator = getExpandAnimator();
        final ValueAnimator alphaAnimator = getValueAnimator();

        animation.playTogether(expandAnimator, alphaAnimator);
        animation.setDuration(ANIMATION_DURATION_IN_MS);
        animation.setInterpolator(new DecelerateInterpolator());

    }

    /**
     * start the animation
     */
    public void startAnimation(){
        if(animation.isRunning() || animation.isStarted())
            throw new IllegalStateException(" animation already started");

        animation.start();
    }

    /**
     * restart the animation
     */
    public void restartAnimation(){
        if(animation.isRunning() || animation.isStarted())
          stopAnimation();
        startAnimation();
    }

    /**
     * stop the animation
     */
    public void stopAnimation(){
        if(!animation.isRunning() || !animation.isStarted())
            throw new IllegalStateException(" the animation must be started to be stopped");
        animation.removeAllListeners();
        animation.end();
        animation.cancel();
    }

    @NonNull
    private ValueAnimator getExpandAnimator() {
        final ValueAnimator expandAnimator = ValueAnimator.ofFloat(0f, 1f);
        expandAnimator.setRepeatCount(ValueAnimator.INFINITE);
        expandAnimator.setRepeatMode(ValueAnimator.RESTART);
        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentExpandAnimationValue = (float) animation.getAnimatedValue();
                if (currentExpandAnimationValue == 0f) {
                    currentAlphaAnimationValue = 255;
                }
                invalidateSelf();
            }
        });
        return expandAnimator;
    }

    @NonNull
    private ValueAnimator getValueAnimator() {
        final ValueAnimator alphaAnimator = ValueAnimator.ofInt(255, 0);
        alphaAnimator.setStartDelay(ANIMATION_DURATION_IN_MS / 4);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAlphaAnimationValue = (int) animation.getAnimatedValue();
            }
        });
        return alphaAnimator;
    }

    private void preparePaints() {
        pulsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pulsePaint.setStyle(Paint.Style.FILL);
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(color);
    }

    @Override
    public void setAlpha(int alpha) {
        pulsePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) { }

    @Override
    public int getOpacity() {
        return pulsePaint.getAlpha();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float centerX = bounds.exactCenterX();
        float centerY = bounds.exactCenterY();
        calculateFullSizeRadius();
        preparePaintShader();
        renderPulse(canvas, centerX, centerY);
        renderCenterArea(canvas, centerX, centerY);
    }

    private void renderPulse(Canvas canvas, float centerX, float centerY) {
        float currentRadius = fullSizeRadius * currentExpandAnimationValue;
        if (currentRadius > MINIMUM_RADIUS) {
            canvas.drawCircle(centerX, centerY, currentRadius, pulsePaint);
        }
    }

    private void renderCenterArea(Canvas canvas, float centerX, float centerY) {
        float currentCenterAreaRadius = fullSizeRadius * CENTER_AREA_SIZE;
        if (currentCenterAreaRadius > MINIMUM_RADIUS) {
            canvas.save();
            float left = centerX - currentCenterAreaRadius;
            float top = centerY - currentCenterAreaRadius;
            float right = centerX + currentCenterAreaRadius;
            float bottom = centerY + currentCenterAreaRadius;
            canvas.clipRect(left, top, right, bottom);
            canvas.drawCircle(centerX, centerY, currentCenterAreaRadius, centerPaint);
            canvas.restore();
        }
    }
    private void preparePaintShader() {
        Rect bounds = getBounds();
        float centerX = bounds.exactCenterX();
        float centerY = bounds.exactCenterY();
        float radius = (Math.min(bounds.width(), bounds.height()) / 2);
        if (radius > MINIMUM_RADIUS) {
            int edgeColor = getPulseColor();
            int centerColor = Color.argb(PULSE_START_COLOR_OPACITY, Color.red(color),
                    Color.green(color),
                    Color.blue(color));
            pulsePaint.setShader(new RadialGradient(centerX, centerY, radius,
                    centerColor, edgeColor, Shader.TileMode.CLAMP));
        } else {
            pulsePaint.setShader(null);
        }
    }

    private int getPulseColor() {
        return Color.argb(currentAlphaAnimationValue, Color.red(color),
                Color.green(color),
                Color.blue(color));
    }

    private void calculateFullSizeRadius() {
        Rect bounds = getBounds();
        float minimumDiameter = Math.min(bounds.width(), bounds.height());
        fullSizeRadius = (minimumDiameter / 2);
    }
}