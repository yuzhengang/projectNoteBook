package com.km.notebook;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class SearchEditText extends android.support.v7.widget.AppCompatEditText {

    private int mPreBottom = -1;
    private OnPreDrawListener preDrawListener = null;

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchEditText(Context context) {
        super(context);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mPreBottom != getBottom()) {
            mPreBottom = getBottom();

            if (preDrawListener != null)
                preDrawListener.onPreDraw(mPreBottom);
        }

        super.onDraw(canvas);
    }

    public static interface OnPreDrawListener {
        public void onPreDraw(int bottom);
    }

    public void setOnPreDrawListener(OnPreDrawListener listener) {
        preDrawListener = listener;
    }


    public void setSpecifiedTextsColor(String text, String keyWord, int color) {
        List<Integer> sTextsStartList = new ArrayList<>();
        int sTextLength = keyWord.length();
        if (sTextLength <= 0){
            setText(text);
        }else{
            String temp = text;
            int lengthFront = 0;//记录被找出后前面的字段的长度
            int start = -1;
            do {
                start = temp.indexOf(keyWord);

                if (start != -1) {
                    start = start + lengthFront;
                    sTextsStartList.add(start);
                    lengthFront = start + sTextLength;
                    temp = text.substring(lengthFront);
                }

            } while (start != -1);

            SpannableStringBuilder styledText = new SpannableStringBuilder(text);
            for (Integer i : sTextsStartList) {
                styledText.setSpan( new ForegroundColorSpan(color), i, i + sTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            setText(styledText);
        }

    }
}
