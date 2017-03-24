package com.lvqingyang.emptyhand.Tools;

import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Author：LvQingYang
 * Date：2017/2/2
 * Email：biloba12345@gamil.com
 * God bless, never bug.
 */

public class TypefaceUtils {
    private static Typeface typeface;

    public static void setTypeface(TextView textView) {
//        if (typeface == null)
//            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/xiyuanjian.ttf");
//        if (textView != null) {
//            textView.setTypeface(typeface);
//        }
    }

    public static void setText(TextView textView,String text) {
//        if (typeface == null)
//            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/xiyuanjian.ttf");
        if (textView != null) {
//            textView.setTypeface(typeface);
            textView.setText(text);
        }
    }
}
