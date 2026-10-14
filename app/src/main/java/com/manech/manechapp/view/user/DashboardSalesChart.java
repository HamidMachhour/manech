package com.manech.manechapp.view.user;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.manech.manechapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardSalesChart extends View {
    public static final class Entry {
        public final String label;
        public final double value;

        public Entry(String label, double value) {
            this.label = label;
            this.value = value;
        }
    }

    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Entry> entries = new ArrayList<>();
    private String emptyMessage;
    private float density;

    public DashboardSalesChart(Context context) {
        super(context);
        init(context);
    }

    public DashboardSalesChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DashboardSalesChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        density = getResources().getDisplayMetrics().density;
        axisPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        axisPaint.setStrokeWidth(dp(1));
        gridPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        gridPaint.setAlpha(50);
        gridPaint.setStrokeWidth(dp(1));
        barPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        textPaint.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        textPaint.setTextSize(dp(11));
    }

    private float dp(float value) {
        return value * density;
    }

    public void setEntries(List<Entry> values, String emptyMessage) {
        entries.clear();
        if (values != null) entries.addAll(values);
        this.emptyMessage = emptyMessage;
        requestLayout();
        invalidate();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return Math.max(super.getSuggestedMinimumWidth(),
                (int) (dp(64) + entries.size() * dp(48)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float left = dp(48);
        final float right = getWidth() - dp(8);
        final float top = dp(12);
        final float bottom = getHeight() - dp(34);

        if (entries.isEmpty()) {
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(emptyMessage == null ? "" : emptyMessage,
                    getWidth() / 2f, getHeight() / 2f, textPaint);
            return;
        }

        double maxValue = 0;
        for (Entry entry : entries) maxValue = Math.max(maxValue, entry.value);
        if (maxValue <= 0) maxValue = 1;

        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int line = 0; line <= 4; line++) {
            float y = bottom - (bottom - top) * line / 4f;
            canvas.drawLine(left, y, right, y, gridPaint);
            double value = maxValue * line / 4d;
            canvas.drawText(String.format(Locale.getDefault(), "%.0f", value),
                    left - dp(5), y + dp(4), textPaint);
        }
        canvas.drawLine(left, top, left, bottom, axisPaint);
        canvas.drawLine(left, bottom, right, bottom, axisPaint);

        float slotWidth = (right - left) / entries.size();
        float barWidth = Math.min(dp(28), slotWidth * 0.62f);
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            float center = left + slotWidth * (i + 0.5f);
            float barHeight = (float) (Math.max(0, entry.value) / maxValue * (bottom - top));
            canvas.drawRect(center - barWidth / 2f, bottom - barHeight,
                    center + barWidth / 2f, bottom, barPaint);
        }

        textPaint.setTextAlign(Paint.Align.CENTER);
        int labelStep = Math.max(1, (int) Math.ceil(entries.size() / 8f));
        for (int i = 0; i < entries.size(); i += labelStep) {
            Entry entry = entries.get(i);
            float center = left + slotWidth * (i + 0.5f);
            canvas.drawText(entry.label, center, getHeight() - dp(10), textPaint);
        }
        if ((entries.size() - 1) % labelStep != 0) {
            String label = entries.get(entries.size() - 1).label;
            float center = left + slotWidth * (entries.size() - 0.5f);
            canvas.drawText(label, center, getHeight() - dp(10), textPaint);
        }
    }
}
