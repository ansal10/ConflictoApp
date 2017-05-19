package in.co.conflicto.conflictoapp.adapters;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.co.conflicto.conflictoapp.fragments.AllPostFragment;
import in.co.conflicto.conflictoapp.fragments.PinnedPostFragment;
import in.co.conflicto.conflictoapp.fragments.TopRatedPostFragment;
import in.co.conflicto.conflictoapp.fragments.dummy.DummyContent;

/**
 * Created by ansal on 5/14/17.
 */

public class HomeTabsPagerAdapter extends FragmentPagerAdapter{

    public HomeTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return AllPostFragment.newInstance();

            case 1: return PinnedPostFragment.newInstance();

            case 2: return TopRatedPostFragment.newInstance();
        }
        return AllPostFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "All Post";
            case 1: return "Pinned Post";
            case 2: return "Top Rated";
        }
        return null;
    }

}
