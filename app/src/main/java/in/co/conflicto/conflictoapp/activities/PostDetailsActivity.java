package in.co.conflicto.conflictoapp.activities;

import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.adapters.CommentItemRecyclerViewAdapter;
import in.co.conflicto.conflictoapp.fragments.CommentDialogFragment;
import in.co.conflicto.conflictoapp.fragments.interfaces.CommentsActionListener;
import in.co.conflicto.conflictoapp.fragments.interfaces.DialogBoxListenerInterface;
import in.co.conflicto.conflictoapp.fragments.interfaces.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Comment;
import in.co.conflicto.conflictoapp.utilities.Constants;

public class PostDetailsActivity extends AppCompatActivity implements CommentsActionListener, View.OnClickListener,
        CommentItemRecyclerViewAdapter.CommentAdapterListener,
        DialogBoxListenerInterface {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private String postUUID;
    private TextView commentLabel;
    private CommentItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        postUUID = getIntent().getStringExtra(Constants.POST_UUID_KEY);

        if(postUUID == null)
            postUUID = savedInstanceState.getString(Constants.POST_UUID_KEY);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.POST_UUID_KEY, postUUID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        postUUID = savedInstanceState.getString(Constants.POST_UUID_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View view = findViewById(R.id.activity_post_details_root_id);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.comments_recycler_view_id);
         adapter = new CommentItemRecyclerViewAdapter(this, postUUID, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.setOnRefreshListener(adapter::refresh);
        commentLabel = (TextView) view.findViewById(R.id.comment_label_id);
        commentLabel.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void refreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void refreshStarted() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void scollToPosition(int pos) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == commentLabel.getId()){
            FragmentManager fragmentManager = getSupportFragmentManager();
            CommentDialogFragment fragment = CommentDialogFragment.newInstance(this, postUUID, null);
            fragment.show(fragmentManager, CommentDialogFragment.class.toString());
        }
    }

    @Override
    public void popupCommentEdit(Comment comment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CommentDialogFragment fragment = CommentDialogFragment.newInstance(this, postUUID, comment);
        fragment.show(fragmentManager, CommentDialogFragment.class.toString());
    }

    @Override
    public void commentUpdated(Comment comment) {
        adapter.commentUpdated(comment);
    }

    @Override
    public void commentDeleted(Comment comment) {
        adapter.commentDeleted(comment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClickedId = item.getItemId();
        if (itemClickedId == android.R.id.home)
            onBackPressed();
        else if (itemClickedId == R.id.action_conflict_comments_id)
            adapter.filterBy(CommentItemRecyclerViewAdapter.CONFLICTS_COMMENTS);
        else if (itemClickedId == R.id.action_support_comments_id)
            adapter.filterBy(CommentItemRecyclerViewAdapter.SUPPORTS_COMMENTS);
        else if (itemClickedId == R.id.action_self_comments_id)
            adapter.filterBy(CommentItemRecyclerViewAdapter.OWN_COMMENTS);
        else if (itemClickedId == R.id.action_sort_by_actions)
            adapter.sortBy(CommentItemRecyclerViewAdapter.SORT_POPULAR_FIRST);
        else if (itemClickedId == R.id.action_sort_by_latest)
            adapter.sortBy(CommentItemRecyclerViewAdapter.SORT_RECENT_FIRST);
        else if (itemClickedId == R.id.action_all_comments_id)
            adapter.filterBy(CommentItemRecyclerViewAdapter.ALL_COMMENTS);
        else return super.onOptionsItemSelected(item);
        return true;
    }
}
