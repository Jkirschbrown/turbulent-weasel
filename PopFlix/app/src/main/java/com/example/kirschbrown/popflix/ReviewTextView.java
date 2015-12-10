package com.example.kirschbrown.popflix;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by jrkirsch on 12/4/2015.
 */
public class ReviewTextView extends TextView {

    boolean isCompressed;

    public ReviewTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isCompressed = false;
    }

    public void setReviewContent(String content) {
        setText(content);
    }

    public boolean setInitialState() {
        if (getLineCount() > 4) {
            setMaxLines(4);
            isCompressed = true;
        }
        return isCompressed;
    }

    public boolean changeState() {
        if (isCompressed) {
            setMaxLines(Integer.MAX_VALUE);
            isCompressed = false;
        } else {
            setInitialState();
        }
        return isCompressed;
    }

}
