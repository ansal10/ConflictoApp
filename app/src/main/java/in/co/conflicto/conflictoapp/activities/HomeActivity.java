package in.co.conflicto.conflictoapp.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.fragments.CommentDialogFragment;
import in.co.conflicto.conflictoapp.fragments.interfaces.OnListFragmentInteractionListener;
import in.co.conflicto.conflictoapp.fragments.interfaces.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Post;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.UIUtils;
import in.co.conflicto.conflictoapp.adapters.HomeTabsPagerAdapter;

public class HomeActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private HomeTabsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private View view;
    private ConstraintLayout searchBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = HomeTabsPagerAdapter.getInstance(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//        searchBarLayout = (ConstraintLayout) findViewById(R.id.search_bar_view_id);
        searchBarLayout.setVisibility(View.GONE);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener((View view) -> {
            Intent intent = new Intent(this, NewPostActivity.class);
            UIUtils.startActivity(intent, false);
        });

        FloatingActionButton fsb = (FloatingActionButton) findViewById(R.id.floatingSearchButton);
        fsb.setOnClickListener((View view) -> {
            if (searchBarLayout.getVisibility() == View.GONE) {
                searchBarLayout.setVisibility(View.VISIBLE);
                scrollToPosition(0);
            }
            else
                searchBarLayout.setVisibility(View.GONE);
        });

    }

    @Override
    public void onBackPressed() {
        if (searchBarLayout.getVisibility() == View.VISIBLE)
            searchBarLayout.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onListFragmentInteraction(Post post) {
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra(Constants.POST_UUID_KEY, post.uuid);
        startActivity(intent);
    }

    @Override
    public void onCommentActionListener(Post post) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CommentDialogFragment fragment = CommentDialogFragment.newInstance(post);
        fragment.show(fragmentManager, CommentDialogFragment.class.toString());

    }

    public void scrollToPosition(int pos){
        int fragmentIndex = mViewPager.getCurrentItem();
        Fragment currentFragment = mSectionsPagerAdapter.getItem(fragmentIndex);
        ((PostFragmentListener)currentFragment).scollToPosition(pos);
    }


}


