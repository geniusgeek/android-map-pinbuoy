package com.github.geniusgeek.pinbuoy;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.github.geniusgeek.pinbuoy.utils.DrawableUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by root on 12/7/16.
 */

public class PinBuoy extends FrameLayout {
    private Ribbon ribbon;
    private volatile boolean isInnitialized;
    private volatile TextView realTimeTextView, titleTextView;
    private ImageView proceedAnimImageView, pinImageView, circlePin;
    private LinearLayout realtimeLinearLayout;
    private ViewFlipper viewFlipper;
    private String realTimeText, titleText, timeMeasurement;
    private PulseDrawable drawable;
    private ProgressBar progressBar;
    private View view;
    public enum States {PRELOAD, LOADING, COMPLETION}

    private RealtimeTextCallback realtimeTextCallback = new PinBuoy.RealtimeTextCallback() {
        @Override
        public void updateTime(String time) {
            setRealTimeText(time);
        }
    };
    private LifeCycleCallback lifeCycleCallback = new LifeCycleCallback() {

        @Override
        public void onPrepare() {
            drawable.prepareAnimation();
         }

        @Override
        public void onLoad() {
            drawable.startAnimation();
        }

        public void onInnitializedError(boolean completed, String error) {
            //positive reason
            setTitleText(error);
        }

        public void onInnitializedCompleteSuccess(boolean completed, String text) {
            //nagative reason
            setTitleText(text);
        }

        @Override
        public boolean isInnitialized() {
            return isInnitialized;
        }
    };
    private final LoadCompletionListener DEFAULT_LOADER_LISTENER = new LoadCompletionListener() {
        @Override
        public void onLoaded(boolean hasLoadedSuccessfully, String message) {
            isInnitialized = hasLoadedSuccessfully;
            if (hasLoadedSuccessfully) {
                lifeCycleCallback.onInnitializedCompleteSuccess(true, message);
                return;
            }
            lifeCycleCallback.onInnitializedError(hasLoadedSuccessfully, message);
        }
    };
    private HashMap<States, State> states;;
    private Iterator<States> stateListIterator = Arrays.asList(States.values()).iterator();;

    public PinBuoy(Context context) {
        super(context);
    }
    public PinBuoy(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PinBuoy(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PinBuoy(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void loadStates() {
        states = new HashMap<>();
        states.put(States.PRELOAD, new PreLoaderState());
        states.put(States.LOADING, new LoadingState());
        states.put(States.COMPLETION, new LoadCompletionState());
    }

    private void initView(Context context, AttributeSet attrs) {
        view = inflate(getContext(), R.layout.pinbuoy_layout, null);
        addView(view);
        initViews();
        initTypedArray(context, attrs);
        addProceedImageViewAnimation();
        loadStates();
    }

    private void addProceedImageViewAnimation() {
        // Drawable drawable= proceedAnimImageView.getDrawable();
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_animation);
        proceedAnimImageView.setAnimation(anim);
    }

    private View getView() {
        return view;
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        ribbon = new Ribbon(context, attrs);
        ribbon.setRoundRibbon(new PulseDrawable(R.color.deep_purple));
        drawable=((PulseDrawable)ribbon.getRoundRibbon());
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Pinbuoy, 0, 0);

        for (int i = 0, j = ta.getIndexCount(); i < j; ++i) {
            Integer attr = ta.getIndex(i);
            if (attr.equals(R.styleable.Pinbuoy_proceedBackgroundDrawable)) {
                setProceedBackgroundDrawable(ta.getDrawable(attr));
            } else if (attr.equals(R.styleable.Pinbuoy_progressBarDrawable)) {
                setProgressDrawable(ta.getDrawable(attr));
            } else if (attr.equals(R.styleable.Pinbuoy_realTimeTextBackgroundDrawable)) {
                setRealTimeTextBackground(ta.getDrawable(attr));
            } else if (attr.equals(R.styleable.Pinbuoy_proceedImageSrc)) {
                setProceedDrawable(ta.getDrawable(attr));
            } else if (attr.equals(R.styleable.Pinbuoy_pinMarkerDrawable)) {
                setPinMarkerDrawable(ta.getResourceId(attr, R.drawable.ic_map_marker));
            } else if (attr.equals(R.styleable.Pinbuoy_ribbonDrawable)) {
                Drawable ribbonDrawable = ta.getDrawable(R.styleable.Pinbuoy_ribbonDrawable);
                ribbon.setRoundRibbon(ribbonDrawable);
            } else if (attr.equals(R.styleable.Pinbuoy_ribbonColor)) {
                ribbon.setRoundRibbon(new PulseDrawable(ta.getColorStateList(attr).getDefaultColor()));
            } else if (attr.equals(R.styleable.Pinbuoy_realTimeText)) {
                setRealTimeText(ta.getString(attr));
            } else if (attr.equals(R.styleable.Pinbuoy_titleText)) {
                setTitleText(ta.getString(attr));
            } else if (attr.equals(R.styleable.Pinbuoy_deepColor)) {
                ColorStateList colorStateList = ta.getColorStateList(attr);
                setShapeColor(realtimeLinearLayout, colorStateList);
                setShapeColor(proceedAnimImageView, colorStateList);
                setShapeColor(viewFlipper.findViewById(R.id.circlePin), colorStateList);
                ribbon.setRoundRibbon(new PulseDrawable(colorStateList.getDefaultColor()));
            } else if (attr.equals(R.styleable.Pinbuoy_lightColor)) {
                setShapeColor(getView().findViewById(R.id.ribbon_frame), ta.getColorStateList(attr));
            } else if (attr.equals(R.styleable.Pinbuoy_textColor)) {
                realTimeTextView.setTextColor(ta.getColorStateList(attr));
                titleTextView.setTextColor(ta.getColorStateList(attr));
                setShapeColor(proceedAnimImageView, ta.getColorStateList(attr));
            }

        }
        circlePin.setImageDrawable(ribbon.getRoundRibbon());

        ta.recycle();//recycle the style attributes
    }

    public void setRibbonLoadingDrawable(Drawable drawable) {
        setBackgroundFix(circlePin, drawable);
    }

    private void setShapeColor(View view, ColorStateList colorStateList) {
        int color = colorStateList.getDefaultColor();
        DrawableUtils.setBackground(view.getBackground(), color);
    }

    private void setRealTimeTextBackground(Drawable drawable) {
        setBackgroundFix(realtimeLinearLayout, drawable);
    }

    private void setBackgroundFix(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }

    private void setProceedBackgroundDrawable(Drawable drawable) {
        setBackgroundFix(proceedAnimImageView, drawable);

    }

    private void setProceedDrawable(Drawable drawable) {
        proceedAnimImageView.setImageDrawable(drawable);
    }

    private void setProgressDrawable(Drawable drawable) {
        progressBar.setIndeterminateDrawable(drawable);

    }

    private void setPinMarkerDrawable(int resId) {
        pinImageView.setImageResource(resId);
    }

    public void initViews() {
        realTimeTextView = (TextView) findViewById(R.id.realtimeTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        proceedAnimImageView = (ImageView) findViewById(R.id.nextImageView);
        pinImageView = (ImageView) findViewById(R.id.pinImageView);
        circlePin = (ImageView) findViewById(R.id.circlePin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        realtimeLinearLayout = (LinearLayout) findViewById(R.id.realtimeLinearLayout);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        animateNextImageView();
    }

    public void nextState(String loadCompletionText) {
        if (stateListIterator.hasNext()) {
            States next = stateListIterator.next();
            nextState(next, loadCompletionText);
        }
    }

    public void nextState(States nextState, String loadCompletionText) {
        State state = states.get(nextState);
        executeStateTask(state,loadCompletionText);
    }

    /**
     * reset the state and set it to loading
     */
    public void reset(){
        State state = states.get(States.LOADING);
        executeStateTask(state,null);

    }

    private void executeStateTask(State nextState,String completionText) {
        if (nextState == null)
            throw new NullPointerException("state cannot be null");
        if (completionText!=null && nextState instanceof LoadCompletionState) {
            ((LoadCompletionState) nextState).executeTask(true, completionText);
            return;
        }
        nextState.executeTask();
    }

    public void setRealTimeText(CharSequence text) {
        realTimeTextView.setText(text);
    }

    public String getTitleText() {
        return titleTextView.getText().toString();
    }

    public void setTitleText(CharSequence text) {
        titleTextView.setText(text);
    }

    public void setTitleTextViewVisibility(int visibility) {
        titleTextView.setVisibility(visibility);
    }

    private void animateNextImageView() {
        Animation animation = new TranslateAnimation(0, 0, 20, 20);
        proceedAnimImageView.startAnimation(animation);
    }


    private boolean isBuoyVisible() {
        return viewFlipper.getCurrentView().getId()== R.id.ribbon_frame;
    }

    public RealtimeTextCallback getRealtimeTextCallback() {
        return realtimeTextCallback;
    }

    public LifeCycleCallback getLifeCycleCallback() {
        return lifeCycleCallback;
    }


    interface LifeCycleCallback {
        void onPrepare();

        void onLoad();//load animation

        void onInnitializedError(boolean completed, String error);

        void onInnitializedCompleteSuccess(boolean completed, String successText);

        boolean isInnitialized();
    }

    interface LoadCompletionListener {
        void onLoaded(boolean hasLoadedSuccessfully, String message);
    }

    /**
     * this interface is called reactively to update the text on the view
     */
    interface RealtimeTextCallback {
        void updateTime(String time);
    }


    interface State {
        void executeTask();

    }

    private class PreLoaderState implements State {
        @Override
        public void executeTask() {
            //just ensure that the preloader view is showing
            int currentViewId = viewFlipper.getCurrentView().getId();
            if (currentViewId != R.id.circlePin)
                viewFlipper.showNext();
            lifeCycleCallback.onPrepare();
        }
    }

    private class LoadingState implements State{
        @Override
        public void executeTask() {
            lifeCycleCallback.onLoad();
        }
    }

    private class LoadCompletionState implements State {
        @Override
        public void executeTask() {
             if(isBuoyVisible())
                return;
            viewFlipper.showNext();
            ((PulseDrawable)ribbon.getRoundRibbon()).stopAnimation();
            DEFAULT_LOADER_LISTENER.onLoaded(true, "set pickup Location");

        }

        public void executeTask(boolean completed, String text) {
            if(isBuoyVisible())
                return;
            viewFlipper.showNext();
            DEFAULT_LOADER_LISTENER.onLoaded(completed, text);

        }

    }
}
