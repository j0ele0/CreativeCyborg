package com.example.creativcyborg.activities.phone.introduction;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class IntroductionFragmentPageAdapter extends FragmentStateAdapter
{


    public IntroductionFragmentPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 0:
                return new IntroductionPage1Fragment();
            case 1:
                return new IntroductionPage2Fragment();
            case 2:
                return new IntroductionPage3Fragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public int getItemCount()
    {
        return 3;
    }
}
