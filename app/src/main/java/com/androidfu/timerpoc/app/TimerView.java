package com.androidfu.timerpoc.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RemoteViews;


/**
 * Display a ticking second hand
 */
@RemoteViews.RemoteView
public class TimerView extends View {

    public interface OnTimerStateChanged {
        public void onFinished(TimerView timerView);

        public void onStarted(TimerView timerView);
    }

    private OnTimerStateChanged listener;
    private TimerViewCountDownTimer timerViewCountDownTimer;
    private Drawable secondHand;
    private Drawable timerFace;
    private int timerFaceWidth;
    private int timerFaceHeight;
    private boolean attached;
    private boolean changed;
    private long duration;

    private float mSecond = 0;
    private Time time = new Time();
    private long interval = 1000;
    private int repetitions = 1;

    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        Resources resources = getContext().getResources();

        timerFace = resources.getDrawable(R.drawable.timer_face);
        timerFaceWidth = timerFace.getIntrinsicWidth();
        timerFaceHeight = timerFace.getIntrinsicHeight();

        secondHand = resources.getDrawable(R.drawable.second_hand);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!attached) {
            attached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (attached) {
            timerViewCountDownTimer.cancel();
            attached = false;
        }
        listener = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < timerFaceWidth) {
            hScale = (float) widthSize / (float) timerFaceWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < timerFaceHeight) {
            vScale = (float) heightSize / (float) timerFaceHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSize((int) (timerFaceWidth * scale), widthMeasureSpec),
                resolveSize((int) (timerFaceHeight * scale), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        changed = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = this.changed;
        if (changed) {
            this.changed = false;
        }
        int availableWidth = 200;
        int availableHeight = 200;

        int x = availableWidth / 2;
        int y = availableHeight / 2;

        final Drawable dial = timerFace;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                    (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }
        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);
        canvas.save();

        canvas.rotate(mSecond, x, y);
        w = secondHand.getIntrinsicWidth();
        h = secondHand.getIntrinsicHeight();
        secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        secondHand.draw(canvas);
        canvas.restore();
        if (scaled) {
            canvas.restore();
        }
    }

    public TimerView setDuration(long duration) throws IllegalStateException {
        if (duration <= 0) {
            throw new IllegalStateException("Duration must be a positive number.");
        }
        this.duration = duration;
        return this;
    }

    public TimerView setInterval(long interval) throws IllegalStateException {
        if (interval < 100 || interval > 1000) {
            throw new IllegalStateException("Interval must be between 100ms and 1000ms.");
        }
        this.interval = interval;
        return this;
    }

    protected void start() throws IllegalStateException {
        if (duration == 0) {
            throw new IllegalStateException("You must set a duration before starting the timer.");
        }
        timerViewCountDownTimer = new TimerViewCountDownTimer(duration, interval);
        timerViewCountDownTimer.start();
        listener.onStarted(this);
    }

    public TimerView setRepetitions(int repetitions) {
        this.repetitions = repetitions;
        return this;
    }

    public void setListener(OnTimerStateChanged listener) {
        this.listener = listener;
    }

    public class TimerViewCountDownTimer extends CountDownTimer {
        public TimerViewCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            if (--repetitions > 0) {
                timerViewCountDownTimer.start();
                return;
            }
            listener.onFinished(TimerView.this);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time.setToNow();
            int second = time.second;
            mSecond = 6.0f * second;
            TimerView.this.invalidate();
        }
    }
}