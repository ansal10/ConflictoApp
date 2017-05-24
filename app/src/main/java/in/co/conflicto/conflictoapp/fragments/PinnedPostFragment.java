package in.co.conflicto.conflictoapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.adapters.PostItemRecyclerViewAdapter;
import in.co.conflicto.conflictoapp.fragments.interfaces.OnListFragmentInteractionListener;
import in.co.conflicto.conflictoapp.fragments.interfaces.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Post;
import in.co.conflicto.conflictoapp.utilities.DiskCaching;
import in.co.conflicto.conflictoapp.utilities.MyApplication;

/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class PinnedPostFragment extends Fragment implements PostFragmentListener {

    private OnListFragmentInteractionListener mListener;
    private PostFragmentListener postFragmentListener;
    private Activity activity;
    private PostItemRecyclerViewAdapter postAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String POST_TAG = "pinned_post";
    private DiskCaching ds;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PinnedPostFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PinnedPostFragment newInstance() {
        PinnedPostFragment fragment = new PinnedPostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this.getActivity();
        ds = MyApplication.getDiskCachingInstance();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        List<Post> posts = ds.getPosts(POST_TAG);

        Context context = mSwipeRefreshLayout.getContext();
        RecyclerView recyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.postAdapter = new PostItemRecyclerViewAdapter(this.activity, mListener, postFragmentListener, posts);
        recyclerView.setAdapter(postAdapter);

        mSwipeRefreshLayout.setOnRefreshListener( () -> {
            this.postAdapter.refresh();
        });

        return mSwipeRefreshLayout;
    }



    @Override
    public void onStop() {
        List<Post> posts = this.postAdapter.getPosts();
        ds.putPosts(POST_TAG, posts);
        super.onStop();

    }

    @Override
    public void onDestroy() {
        ds.removePosts(POST_TAG);
        super.onDestroy();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnListFragmentInteractionListener) context;
        postFragmentListener = this;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void refreshCompleted() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshStarted() {
        mSwipeRefreshLayout.setRefreshing(true);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

}


