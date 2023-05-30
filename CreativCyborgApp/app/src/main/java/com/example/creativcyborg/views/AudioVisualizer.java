package com.example.creativcyborg.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.creativcyborg.R;

public class AudioVisualizer extends LinearLayout
{
    private final long ANIMATION_DURATION = 800;

    // Views
    FrameLayout bar1;
    FrameLayout bar2;
    FrameLayout bar3;
    FrameLayout bar4;
    FrameLayout bar5;

    // Animators
    ValueAnimator animator1;
    ValueAnimator animator2;
    ValueAnimator animator3;
    ValueAnimator animator4;
    ValueAnimator animator5;

    public AudioVisualizer(Context context)
    {
        super(context);
        init();
    }

    public AudioVisualizer(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public AudioVisualizer(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AudioVisualizer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.audio_visualizer_view, this);
        this.bar1 = findViewById(R.id.audio_visualizer_bar_1);
        this.bar2 = findViewById(R.id.audio_visualizer_bar_2);
        this.bar3 = findViewById(R.id.audio_visualizer_bar_3);
        this.bar4 = findViewById(R.id.audio_visualizer_bar_4);
        this.bar5 = findViewById(R.id.audio_visualizer_bar_5);

        animator1 = ValueAnimator.ofInt(dpToPx(16), dpToPx(80));
        animator1.setDuration(ANIMATION_DURATION);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator1.setInterpolator(new AccelerateDecelerateInterpolator());

        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
            {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                bar1.getLayoutParams().height = value.intValue();
                bar1.requestLayout();
            }
        });

        animator2 = ValueAnimator.ofInt(dpToPx(16), dpToPx(40));
        animator2.setDuration(ANIMATION_DURATION);
        animator2.setStartDelay(100);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.REVERSE);
        animator2.setInterpolator(new AccelerateDecelerateInterpolator());

        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
            {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                bar2.getLayoutParams().height = value.intValue();
                bar2.requestLayout();
            }
        });

        animator3 = ValueAnimator.ofInt(dpToPx(16), dpToPx(80));
        animator3.setDuration(ANIMATION_DURATION);
        animator3.setStartDelay(200);
        animator3.setRepeatCount(ValueAnimator.INFINITE);
        animator3.setRepeatMode(ValueAnimator.REVERSE);
        animator3.setInterpolator(new AccelerateDecelerateInterpolator());

        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
            {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                bar3.getLayoutParams().height = value.intValue();
                bar3.requestLayout();
            }
        });

        animator4 = ValueAnimator.ofInt(dpToPx(16), dpToPx(40));
        animator4.setDuration(ANIMATION_DURATION);
        animator4.setStartDelay(300);
        animator4.setRepeatCount(ValueAnimator.INFINITE);
        animator4.setRepeatMode(ValueAnimator.REVERSE);
        animator4.setInterpolator(new AccelerateDecelerateInterpolator());

        animator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
            {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                bar4.getLayoutParams().height = value.intValue();
                bar4.requestLayout();
            }
        });

        animator5 = ValueAnimator.ofInt(dpToPx(16), dpToPx(80));
        animator5.setDuration(ANIMATION_DURATION);
        animator5.setStartDelay(400);
        animator5.setRepeatCount(ValueAnimator.INFINITE);
        animator5.setRepeatMode(ValueAnimator.REVERSE);
        animator5.setInterpolator(new AccelerateDecelerateInterpolator());

        animator5.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
            {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                bar5.getLayoutParams().height = value.intValue();
                bar5.requestLayout();
            }
        });

        //startAnimation();
    }

    public void startAnimation()
    {
        animator1.start();
        animator2.start();
        animator3.start();
        animator4.start();
        animator5.start();
    }

    public void stopAnimation()
    {
        animator1.reverse();
        animator1.end();
        animator2.reverse();
        animator2.end();
        animator3.reverse();
        animator3.end();
        animator4.reverse();
        animator4.end();
        animator5.reverse();
        animator5.end();
    }

    // Hilfsmethode, um dp in Pixel umzuwandeln
    private int dpToPx(int dp)
    {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
