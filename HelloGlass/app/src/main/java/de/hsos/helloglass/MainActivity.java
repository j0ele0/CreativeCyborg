package de.hsos.helloglass;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements GlassGestureDetector.OnGestureListener
{
    private GlassGestureDetector glassGestureDetector;

    String[] hexColors = { "#ffffff", "#000000", "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#00FFFF", "#FF00FF", "#C0C0C0", "#808080", "#800000", "#808000", "#008000", "#800080", "#008080", "#000080", "#FFA500", "#FFC0CB", "#800080", "#FF5733", "#F4A460", "#32CD32", "#48D1CC", "#66CDAA", "#FF69B4", "#DC143C", "#FF8C00", "#FFD700", "#DAA520", "#B8860B", "#F5DEB3"};
    float[] textSizes = { 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120 };
    int currentColorIndex = 0;
    int currentTextSizeIndex = 0;
    boolean isTextBlack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        glassGestureDetector = new GlassGestureDetector(this, this);
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture)
    {
        TextView gestureTextView = findViewById(R.id.gestureTextView);
        TextView textSizeTextView = findViewById(R.id.textSizeView);
        LinearLayout surface = findViewById(R.id.mainSurface);

        switch (gesture)
        {
            // Textfarbe wechseln
            case TAP:
            {
                isTextBlack = !isTextBlack;

                if (isTextBlack)
                {
                    gestureTextView.setTextColor(Color.parseColor("#000000"));
                }
                else
                {
                    gestureTextView.setTextColor(Color.parseColor("#ffffff"));
                }

                return true;
            }
            // Hintergrundfarbe wechseln (nach vorne)
            case SWIPE_FORWARD:
            {
                currentColorIndex++;

                if (currentColorIndex >= hexColors.length)
                {
                    currentColorIndex = hexColors.length -1;
                }

                surface.setBackgroundColor(Color.parseColor(hexColors[currentColorIndex]));
                return true;
            }
            // Hintergrundfarbe wechseln (nach hinten)
            case SWIPE_BACKWARD:
            {
                currentColorIndex--;

                if (currentColorIndex < 0)
                {
                    currentColorIndex = 0;
                }

                surface.setBackgroundColor(Color.parseColor(hexColors[currentColorIndex]));
                return true;
            }
            // Textgröße vergrößern
            case SWIPE_UP:
            {
                currentTextSizeIndex++;

                if (currentTextSizeIndex >= textSizes.length)
                {
                    currentTextSizeIndex = textSizes.length - 1;
                }

                gestureTextView.setTextSize(textSizes[currentTextSizeIndex]);
                textSizeTextView.setText(String.valueOf(textSizes[currentTextSizeIndex]));
                return true;
            }
            // Textgröße verkleinern
            case SWIPE_DOWN:
            {
                currentTextSizeIndex--;

                if (currentTextSizeIndex < 0)
                {
                    currentTextSizeIndex = 0;
                }

                gestureTextView.setTextSize(textSizes[currentTextSizeIndex]);
                textSizeTextView.setText(String.valueOf(textSizes[currentTextSizeIndex]));
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (glassGestureDetector.onTouchEvent(ev))
        {
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }
}