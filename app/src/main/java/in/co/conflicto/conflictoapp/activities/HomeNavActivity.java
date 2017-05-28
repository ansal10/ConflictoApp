package in.co.conflicto.conflictoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.adapters.HomeTabsPagerAdapter;
import in.co.conflicto.conflictoapp.fragments.AllPostFragment;
import in.co.conflicto.conflictoapp.fragments.CommentDialogFragment;
import in.co.conflicto.conflictoapp.fragments.interfaces.OnListFragmentInteractionListener;
import in.co.conflicto.conflictoapp.fragments.interfaces.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Post;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.SessionData;
import in.co.conflicto.conflictoapp.utilities.UIUtils;

public class HomeNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnListFragmentInteractionListener {

    private HomeTabsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ConstraintLayout searchBarLayout;
    private String FRAGMENT_TAG = "fragment_tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewPostActivity.class);
            UIUtils.startActivity(intent, false);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView navProfileNameView = ((TextView)findViewById(R.id.nav_profile_name_id));
        if (navProfileNameView!=null)
            navProfileNameView.setText(SessionData.currentUser.name);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment =AllPostFragment.getInstance();
            ft.add(R.id.post_container,fragment,FRAGMENT_TAG);
            ft.commit();
        }else {
            FragmentTransaction ft = fm.beginTransaction();
            fragment =AllPostFragment.getInstance();
            ft.replace(R.id.post_container,fragment,FRAGMENT_TAG);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
//        else if (searchBarLayout.getVisibility() == View.VISIBLE)
//            searchBarLayout.setVisibility(View.GONE);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_nav, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        ImageView v = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        v.setImageResource(R.drawable.search_icon); //Changing the image
        searchView.setQueryHint("Search Something");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                if(newText.isEmpty())
                return false;
            }
        });

        return true;

    }
}
