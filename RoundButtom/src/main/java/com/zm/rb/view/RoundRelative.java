package com.zm.rb.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zm.rb.R;

public class RoundRelative extends RelativeLayout {
    int strokeColor = 0x0;
    int strokeWidth = 0;
    int strokeDashWidth = 0;
    int strokeDashGap = 0;
    float pressedRatio = 0f;
    int cornerRadius = 0;
    ColorStateList solidColor;

    public RoundRelative(Context context) {
        this(context, null);
    }

    public RoundRelative(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRelative(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundButton);

        pressedRatio = a.getFloat(R.styleable.RoundButton_btnPressedRatio, 0f);//0.80f
        cornerRadius = a.getLayoutDimension(R.styleable.RoundButton_btnCornerRadius, 0);

        solidColor = a.getColorStateList(R.styleable.RoundButton_btnSolidColor);
        strokeColor = a.getColor(R.styleable.RoundButton_btnStrokeColor, 0x0);
        strokeWidth = a.getDimensionPixelSize(R.styleable.RoundButton_btnStrokeWidth, 0);
        strokeDashWidth = a.getDimensionPixelSize(R.styleable.RoundButton_btnStrokeDashWidth, 0);
        strokeDashGap = a.getDimensionPixelSize(R.styleable.RoundButton_btnStrokeDashGap, 0);
        a.recycle();
        //setSingleLine(true);
        //setGravity(Gravity.CENTER);
        changeBg();
    }

    private void changeBg() {
        RoundDrawable rd = new RoundDrawable(cornerRadius == -1);
        rd.setCornerRadius(cornerRadius == -1 ? 0 : cornerRadius);
        rd.setStroke(strokeWidth, strokeColor, strokeDashWidth, strokeDashGap);

        if (solidColor == null) {
            solidColor = ColorStateList.valueOf(0);
        }
        if (solidColor.isStateful()) {
            rd.setSolidColors(solidColor);
        } else if (pressedRatio > 0.0001f) {
            rd.setSolidColors(csl(solidColor.getDefaultColor(), pressedRatio));
        } else {
            rd.setColor(solidColor.getDefaultColor());
        }
        this.setBackground(rd);
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        changeBg();
        invalidate();
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = ColorStateList.valueOf(solidColor);
        changeBg();
        invalidate();
    }

    public void setColor(int solidColor, int strokeColor) {
        this.strokeColor = strokeColor;
        this.solidColor = ColorStateList.valueOf(solidColor);
        changeBg();
        invalidate();
    }

    // 灰度
    int greyer(int color) {
        int blue = (color & 0x000000FF) >> 0;
        int green = (color & 0x0000FF00) >> 8;
        int red = (color & 0x00FF0000) >> 16;
        int grey = Math.round(red * 0.299f + green * 0.587f + blue * 0.114f);
        return Color.argb(0xff, grey, grey, grey);
    }

    // 明度
    int darker(int color, float ratio) {
        color = (color >> 24) == 0 ? 0x22808080 : color;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= ratio;
        return Color.HSVToColor(color >> 24, hsv);
    }

    ColorStateList csl(int normal, float ratio) {
        //        int disabled = greyer(normal);
        int pressed = darker(normal, ratio);
        int[][] states = new int[][]{{android.R.attr.state_pressed}, {}};
        int[] colors = new int[]{pressed, normal};
        return new ColorStateList(states, colors);
    }

    private static class RoundDrawable extends GradientDrawable {
        private boolean mIsStadium = false;

        private ColorStateList mSolidColors;
        private int mFillColor;

        public RoundDrawable(boolean isStadium) {
            mIsStadium = isStadium;
        }

        public void setSolidColors(ColorStateList colors) {
            mSolidColors = colors;
            setColor(colors.getDefaultColor());
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            if (mIsStadium) {
                RectF rect = new RectF(getBounds());
                setCornerRadius((rect.height() > rect.width() ? rect.width() : rect.height()) / 2);
            }
        }

        @Override
        public void setColor(int argb) {
            mFillColor = argb;
            super.setColor(argb);
        }

        @Override
        protected boolean onStateChange(int[] stateSet) {
            if (mSolidColors != null) {
                final int newColor = mSolidColors.getColorForState(stateSet, 0);
                if (mFillColor != newColor) {
                    setColor(newColor);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isStateful() {
            return super.isStateful() || (mSolidColors != null && mSolidColors.isStateful());
        }
    }
}