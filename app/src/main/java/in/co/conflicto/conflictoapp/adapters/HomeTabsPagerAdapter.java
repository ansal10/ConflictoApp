package in.co.conflicto.conflictoapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.co.conflicto.conflictoapp.fragments.AllPostFragment;
import in.co.conflicto.conflictoapp.fragments.PinnedPostFragment;
import in.co.conflicto.conflictoapp.fragments.TopRatedPostFragment;

/**
 * Created by ansal on 5/14/17.
 */

public class HomeTabsPagerAdapter extends FragmentPagerAdapter{

    private Fragment allPostFragment;
    private Fragment pinnedPostFragment;
    private Fragment topRatedFragment;
    public HomeTabsPagerAdapter(FragmentManager fm) {
        super(fm);
        allPostFragment = AllPostFragment.newInstance();
        pinnedPostFragment = PinnedPostFragment.newInstance();
        topRatedFragment = TopRatedPostFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return allPostFragment;

            case 1:
                return pinnedPostFragment;

            case 2:
                return topRatedFragment;
        }
        return allPostFragment;
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

    public Fragment getFragmentAtPosition(int position){
        switch (position) {
            case 0:

                return allPostFragment;

            case 1:
                return pinnedPostFragment;

            case 2:
                return topRatedFragment;
        }
        return null;
    }



}
