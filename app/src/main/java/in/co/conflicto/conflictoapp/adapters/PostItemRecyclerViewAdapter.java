package in.co.conflicto.conflictoapp.adapters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.activities.HomeActivity;
import in.co.conflicto.conflictoapp.fragments.AllPostFragment.OnListFragmentInteractionListener;
import in.co.conflicto.conflictoapp.fragments.dummy.DummyContent.DummyItem;
import in.co.conflicto.conflictoapp.models.Post;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.DownloadImageTask;
import in.co.conflicto.conflictoapp.utilities.JsonObjectRequestWithAuth;
import in.co.conflicto.conflictoapp.utilities.MyApplication;
import in.co.conflicto.conflictoapp.utilities.SessionData;
import in.co.conflicto.conflictoapp.utilities.UIUtils;
import in.co.conflicto.conflictoapp.utilities.Utilis;
import in.co.conflicto.conflictoapp.utilities.VolleySingelton;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PostItemRecyclerViewAdapter extends RecyclerView.Adapter<PostItemRecyclerViewAdapter.ViewHolder>
    implements RecyclerView.OnItemTouchListener {

    private final List<Post> posts;
    private final OnListFragmentInteractionListener mListener;
    private final Activity activity;
    private Integer page = 0;
    private Boolean allPostLoaded = false;

    public PostItemRecyclerViewAdapter(Activity activity, OnListFragmentInteractionListener listener) {
        posts = new LinkedList<>();
        mListener = listener;
        this.activity = activity;
        this.loadPosts();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.post = posts.get(position);
        Post post = posts.get(position);
        holder.mPostTimestampView.setText("posted 2 mins ago");
        holder.mProfileNameView.setText(post.user.name);
        holder.mPostTitleView.setText(post.title);
        holder.mPostDescriptionView.setText(post.description);
        holder.mLikesView.setText(post.likes.toString());
        holder.mDislikeView.setText(post.dislikes.toString());
        holder.mEndorseView.setText(post.endorse.toString());
        holder.mCommentView.setText("9");
        holder.mConflictView.setText(post.conflicts.toString());
        holder.mSupportView.setText(post.supports.toString());

        holder.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onListFragmentInteraction(post);
            }
        });

        new DownloadImageTask(holder.mDPImageView)
                .execute(post.user.dpLink);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
//        public Post post;
        public final View mView ;
        public final TextView mProfileNameView ;
        public final TextView mPostTimestampView ;
        public final TextView mPostTitleView ;
        public final TextView mPostDescriptionView ;
        public final TextView mLikesView ;
        public final TextView mDislikeView ;
        public final TextView mEndorseView ;
        public final TextView mCommentView ;
        public final TextView mConflictView ;
        public final TextView mSupportView ;
        public final ImageView mDPImageView ;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mProfileNameView = (TextView) view.findViewById(R.id.profile_name_id);
            mPostTimestampView = (TextView) view.findViewById(R.id.post_timestamp_id);
            mPostTitleView = (TextView) view.findViewById(R.id.post_title_id);
            mPostDescriptionView = (TextView) view.findViewById(R.id.post_description_id);
            mLikesView = (TextView) view.findViewById(R.id.likes_id);
            mDislikeView = (TextView) view.findViewById(R.id.dislike_id);
            mEndorseView = (TextView) view.findViewById(R.id.endorse_id);
            mCommentView = (TextView) view.findViewById(R.id.comment_id);
            mConflictView = (TextView) view.findViewById(R.id.conflict_id);
            mSupportView = (TextView) view.findViewById(R.id.support_id);
            mDPImageView = (ImageView) view.findViewById(R.id.dp_id);
        }

    }

    public void loadPosts(){
        if (!this.allPostLoaded) {
            RequestQueue queue = VolleySingelton.getInstance().getRequestQueue();
            UIUtils.showLoader(activity);
            JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.GET, Constants.SERVER_URL + "/post", null,
                (JSONObject response) -> {
                    try {
                        JSONArray arr = response.getJSONArray("results");
                        for (int i = 0; i < arr.length(); i++) {
                            posts.add(new Post(arr.optJSONObject(i)));
                        }
                        this.notifyDataSetChanged();
                        this.page++;
                        if (arr.length() < 25) {
                            this.allPostLoaded = true;
                        }


                    } catch (JSONException e) {
                        Utilis.exc("volley", e);
                    }
                    UIUtils.hideLoader(activity);
                }, (VolleyError error) -> {
                    UIUtils.hideLoader(activity);
                    Utilis.exc("volley", error);
            });

            queue.add(request);

        }
    }

}
