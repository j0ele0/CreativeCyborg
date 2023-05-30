package com.example.creativcyborg.activities.phone.introduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.creativcyborg.R;
import com.example.creativcyborg.activities.phone.LandingActivity;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Interaktionslogik der activity_introduction.xml
 */
public class IntroductionActivity extends AppCompatActivity
{
    private int slidesCount;

    // Views
    private ViewPager2 viewPager;
    private Button skipButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        setBarColors();

        this.skipButton = findViewById(R.id.introduction_skip_button);
        this.previousButton = findViewById(R.id.introduction_previous_slide_button);
        this.nextButton = findViewById(R.id.introduction_next_slide_button);

        // Seiten dem ViewPager hinzufügen
        viewPager = findViewById(R.id.introduction_viewpager);
        IntroductionFragmentPageAdapter adapter = new IntroductionFragmentPageAdapter(getSupportFragmentManager(), getLifecycle());
        this.slidesCount = adapter.getItemCount();
        viewPager.setAdapter(adapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);
                changeButtonVisiblities();
            }
        });

        // TabLayout (Dots) setzen
        TabLayout tabLayout = findViewById(R.id.introduction_viewpager_dots);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) { }
        });
        tabLayoutMediator.attach();
    }

    /**
     * Setzt die NavigationBar- und StatusBar-Farbe der App passend zur Activity.
     */
    private void setBarColors()
    {
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // Text- und Icon-Farbe der StatusBar im hellen Design auf dunkel setzen
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * Wechselt zur nächsten Seite.
     */
    public void nextSlide_onClick(View view)
    {
        this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() + 1);
    }

    /**
     * Wechselt zur vorherigen Seite.
     */
    public void previousSlide_onClick(View view)
    {
        this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() - 1);
    }

    /**
     * Schließt die App-Einführung.
     */
    public void closeIntroduction_onClick(View view)
    {
        Intent intent = new Intent(IntroductionActivity.this, LandingActivity.class);
        IntroductionActivity.this.startActivity(intent);
    }

    /**
     * Ändert die Sichtbarkeit der Buttons auf der UI, je nachdem welche Seite gerade selektiert ist.
     */
    private void changeButtonVisiblities()
    {
        if (this.viewPager.getCurrentItem() == 0)
        {
            this.previousButton.animate().alpha(0).setDuration(300).start();
        }
        else
        {
            this.previousButton.animate().alpha(1).setDuration(300).start();
        }

        if (this.viewPager.getCurrentItem() == slidesCount - 1)
        {
            this.nextButton.animate().alpha(0).setDuration(300).start();
            this.skipButton.animate().alpha(0).setDuration(300).start();
        }
        else
        {
            this.nextButton.animate().alpha(1).setDuration(300).start();
            this.skipButton.animate().alpha(1).setDuration(300).start();
        }
    }
}