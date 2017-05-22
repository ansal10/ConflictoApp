package in.co.conflicto.conflictoapp.activities;

import android.content.Context;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.adapters.CommentItemRecyclerViewAdapter;
import in.co.conflicto.conflictoapp.fragments.CommentDialogFragment;
import in.co.conflicto.conflictoapp.fragments.dummy.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Comment;

public class PostDetailsActivity extends AppCompatActivity implements PostFragmentListener, View.OnClickListener,
        CommentItemRecyclerViewAdapter.CommentAdapterListener,
        CommentDialogFragment.DialogBoxListenerInterface{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private String postUUID;
    private TextView commentLabel;
    private CommentItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        postUUID = getIntent().getStringExtra("post_uuid");

        if(postUUID == null)
            postUUID = savedInstanceState.getString("post_uuid");


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("post_uuid", postUUID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        postUUID = savedInstanceState.getString("post_uuid");
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
}
