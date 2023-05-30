package com.example.creativcyborg.tools;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationHelper
{
    public static void changeTextWithAnimation(TextView textView, String newText)
    {
        textView.animate().alpha(0).setDuration(400).withEndAction(() ->
        {
            textView.setText(newText);
            textView.animate().alpha(1).setDuration(400).start();
        }).start();
    }

    public static void changeImageSrcWithAnimation(ImageView imageView, Drawable newDrawable)
    {
        imageView.animate().alpha(0).setDuration(400).withEndAction(() ->
        {
            imageView.setImageDrawable(newDrawable);
            imageView.animate().alpha(1).setDuration(400).start();
        }).start();
    }
}
