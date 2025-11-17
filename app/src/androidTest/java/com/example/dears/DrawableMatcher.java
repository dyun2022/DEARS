package com.example.dears;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class DrawableMatcher extends TypeSafeMatcher<View> {

    private final int expectedId;

    public DrawableMatcher(int expectedId) {
        super(View.class);
        this.expectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(View view) {
        if (!(view instanceof ImageView)) {
            return false;
        }

        ImageView imageView = (ImageView) view;
        if (expectedId < 0) {
            return imageView.getDrawable() == null;
        }

        Drawable expected = imageView.getContext().getDrawable(expectedId);
        Drawable actual = imageView.getDrawable();
        if (expected == null || actual == null) return false;

        // convert to bitmap and compare
        Bitmap expectedBitmap = ((BitmapDrawable) expected).getBitmap();
        Bitmap actualBitmap = ((BitmapDrawable) actual).getBitmap();
        return expectedBitmap.sameAs(actualBitmap);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("has drawable resource " + expectedId);
    }

    public static Matcher<View> withDrawable(int drawableId) {
        return new DrawableMatcher(drawableId);
    }
}
