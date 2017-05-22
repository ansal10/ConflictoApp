package in.co.conflicto.conflictoapp.activities;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.adapters.CommentItemRecyclerViewAdapter;
import in.co.conflicto.conflictoapp.fragments.dummy.PostFragmentListener;

public class PostDetailsActivity extends AppCompatActivity implements PostFragmentListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private String postUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
         postUUID = getIntent().getStringExtra("post_uuid");
    }


    @Override
    protected void onResume() {
        super.onResume();
        View view = findViewById(R.id.activity_post_details_root_id);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) view.findViewById(R.id.comments_recycler_view_id);
        CommentItemRecyclerViewAdapter adapter = new CommentItemRecyclerViewAdapter(this, postUUID, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.setOnRefreshListener(adapter::refresh);
    }

    @Override
    public void refreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void refreshStarted() {
        swipeRefreshLayout.setRefreshing(true);
    }

}
