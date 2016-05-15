package com.beast.bkara.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.hedgehog.ratingbar.RatingBar;

/**
 * Created by Darka on 4/28/2016.
 */
public class RatingBarView extends RatingBar {
    public RatingBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, com.hedgehog.ratingbar.R.styleable.RatingBar);
        Drawable halfDrawable = mTypedArray.getDrawable(com.hedgehog.ratingbar.R.styleable.RatingBar_starHalf);
        mTypedArray.recycle();
        setStarHalfDrawable(halfDrawable);
    }
}
