package com.example.showmethemoney;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgress extends View {
    private Paint backgroundPaint;
    private Paint progressPaint;
    private float progress = 0;

    public CircularProgress(Context context) {
        super(context);
        init();
    }

    public CircularProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFE0E0E0);
        backgroundPaint.setStrokeWidth(20f);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setColor(0xFFFFC107); // Kuning
        progressPaint.setStrokeWidth(20f);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setAntiAlias(true);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float padding = 10f;
        float size = Math.min(getWidth(), getHeight()) - 2 * padding;
        RectF rect = new RectF(padding, padding, padding + size, padding + size);

        canvas.drawArc(rect, 0, 360, false, backgroundPaint);
        canvas.drawArc(rect, -90, 360 * (progress / 100f), false, progressPaint);
    }
}
