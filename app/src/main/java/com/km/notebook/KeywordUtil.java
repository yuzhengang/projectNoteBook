package com.km.notebook;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：SelectCityDome
 * 类描述：
 * 创建人：
 * 创建时间：2016/7/26 15:22
 * 修改人：Administrator
 * 修改时间：2016/7/26 15:22
 * 修改备注：
 */

public class KeywordUtil {

    /**
     * 关键字高亮变色
     *
     * @param color
     *            变化的色值
     * @param text
     *            文字
     * @param keyword
     *            文字中的关键字
     * @return
     */
    public  static SpannableString matcherSearchTitle(int color, String text, String keyword) {
        NoteApplication.num=0;
        SpannableString s = new SpannableString(text);
        Pattern p = Pattern.compile(keyword);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            NoteApplication.num++;
            s.setSpan(new BackgroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")),start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return s;
    }

    /**
     * 多个关键字高亮变色
     *
     * @param color
     *            变化的色值
     * @param text
     *            文字
     * @param keyword
     *            文字中的关键字数组
     * @return
     */
    public static SpannableString matcherSearchTitle(int color, String text, String[] keyword) {
        SpannableString s = new SpannableString(text);
        for (int i = 0; i < keyword.length; i++) {
            Pattern p = Pattern.compile(keyword[i]);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }
}
