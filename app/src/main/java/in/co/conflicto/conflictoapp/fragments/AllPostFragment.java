package in.co.conflicto.conflictoapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.adapters.PostItemRecyclerViewAdapter;
import in.co.conflicto.conflictoapp.fragments.dummy.DummyContent;
import in.co.conflicto.conflictoapp.fragments.dummy.DummyContent.DummyItem;
import in.co.conflicto.conflictoapp.fragments.dummy.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Post;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AllPostFragment extends Fragment implements PostFragmentListener {

    private OnListFragmentInteractionListener mListener;
    private PostFragmentListener postFragmentListener;
    private Activity activity;
    private PostItemRecyclerViewAdapter postAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllPostFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AllPostFragment newInstance() {
        AllPostFragment fragment = new AllPostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this.getActivity();
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
        Context context = mSwipeRefreshLayout.getContext();
        RecyclerView recyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.postAdapter = new PostItemRecyclerViewAdapter(this.activity, mListener, postFragmentListener);
        recyclerView.setAdapter(postAdapter);

        mSwipeRefreshLayout.setOnRefreshListener( () -> {
            this.postAdapter.refresh();
        });

        return mSwipeRefreshLayout;
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Post post);

        void onCommentActionListener(Post post);
    }
}


