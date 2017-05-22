package in.co.conflicto.conflictoapp.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.fragments.AllPostFragment.OnListFragmentInteractionListener;
import in.co.conflicto.conflictoapp.fragments.dummy.DummyContent.DummyItem;
import in.co.conflicto.conflictoapp.fragments.dummy.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Post;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.DownloadImageTask;
import in.co.conflicto.conflictoapp.utilities.JsonObjectRequestWithAuth;
import in.co.conflicto.conflictoapp.utilities.UIUtils;
import in.co.conflicto.conflictoapp.utilities.Utilis;
import in.co.conflicto.conflictoapp.utilities.VolleySingelton;

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
    private PostFragmentListener fragmentListener;

    public PostItemRecyclerViewAdapter(Activity activity, OnListFragmentInteractionListener listener, PostFragmentListener fragmentListener) {
        posts = new LinkedList<>();
        mListener = listener;
        this.activity = activity;
        this.fragmentListener = fragmentListener;
        this.loadPosts();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item_2, parent, false);
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
        holder.mLikesView.setText(post.likes +"");
        holder.mDislikeView.setText(post.dislikes+ "");
        holder.mEndorseView.setText(post.endorse+"");
        holder.mConflictView.setText(post.conflicts+" Conflicts");
        holder.mSupportView.setText(post.supports+" Supports");
        holder.mActionPopup.setVisibility(View.GONE);

        holder.mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onListFragmentInteraction(post);
            }
        });
        if(post.reactions.size() > 0)
            holder.mActionLabelView.setTextColor(Color.BLUE);

        holder.mActionLabelView.setOnClickListener((View v)-> {
            if(holder.mActionPopup.getVisibility() == View.GONE)
                holder.mActionPopup.setVisibility(View.VISIBLE);
            else
                holder.mActionPopup.setVisibility(View.GONE);

        });

        holder.mLikeActionView.setOnClickListener(v->{
            this.updatePostAction(position, "LIKE");
            holder.mActionPopup.setVisibility(View.GONE);
            this.notifyItemChanged(position);
        });
        holder.mDislikeActionView.setOnClickListener(v->{
            this.updatePostAction(position, "DISLIKE");
            holder.mActionPopup.setVisibility(View.GONE);
            this.notifyItemChanged(position);
        });
        holder.mEndorseActionView.setOnClickListener(v->{
            this.updatePostAction(position, "ENDORSE");
            holder.mActionPopup.setVisibility(View.GONE);
            this.notifyItemChanged(position);
        });
        holder.mReportActionView.setOnClickListener(v->{
            this.updatePostAction(position, "REPORT");
            holder.mActionPopup.setVisibility(View.GONE);
            this.notifyItemChanged(position);
        });

        for(String action:post.reactions){
            switch (action) {
                case "LIKE":
                    holder.mLikeActionView.setTextColor(Color.BLUE);
                    break;
                case "DISLIKE":
                    holder.mDislikeActionView.setTextColor(Color.BLUE);
                    break;
                case "ENDORSE":
                    holder.mEndorseActionView.setTextColor(Color.BLUE);
                    break;
                case "REPORT":
                    holder.mReportActionView.setTextColor(Color.BLUE);
                    break;
            }
        }


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

    public void refresh() {
        this.allPostLoaded = false;
        this.page = 0;
        this.posts.clear();
        loadPosts();
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
        public final TextView mConflictView ;
        public final TextView mSupportView ;
        public final ImageView mDPImageView ;
        public final LinearLayout mActionPopup;
        public final TextView mActionLabelView;
        public final TextView mCommentLabelView;
        public final TextView mLikeActionView;
        public final TextView mDislikeActionView;
        public final TextView mEndorseActionView;
        public final TextView mReportActionView;


        public ViewHolder(View view) {
            super(view);
            mView = view;

            mProfileNameView = (TextView) view.findViewById(R.id.profile_name_id);
            mPostTimestampView = (TextView) view.findViewById(R.id.post_timestamp_id);
            mPostTitleView = (TextView) view.findViewById(R.id.post_title_id);
            mPostDescriptionView = (TextView) view.findViewById(R.id.post_description_id);
            mLikesView = (TextView) view.findViewById(R.id.likes_id);
            mDislikeView = (TextView) view.findViewById(R.id.dislikes_id);
            mEndorseView = (TextView) view.findViewById(R.id.endorse_id);
            mConflictView = (TextView) view.findViewById(R.id.conflict_id);
            mSupportView = (TextView) view.findViewById(R.id.support_id);
            mDPImageView = (ImageView) view.findViewById(R.id.dp_id);
            mActionPopup = (LinearLayout) view.findViewById(R.id.action_popup_id);
            mActionLabelView = (TextView) view.findViewById(R.id.action_label_id);
            mCommentLabelView = (TextView) view.findViewById(R.id.comment_label_id);
            mLikeActionView = (TextView) mActionPopup.findViewById(R.id.like_action_id);
            mDislikeActionView = (TextView) mActionPopup.findViewById(R.id.dislike_action_id);
            mEndorseActionView = (TextView) mActionPopup.findViewById(R.id.endorse_action_id);
            mReportActionView = (TextView) mActionPopup.findViewById(R.id.report_action_id);

        }


    }

    public void loadPosts(){
        if (!this.allPostLoaded) {
            RequestQueue queue = VolleySingelton.getInstance().getRequestQueue();
            fragmentListener.refreshStarted();
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
                        fragmentListener.refreshCompleted();


                    } catch (JSONException e) {
                        Utilis.exc("volley", e);
                        fragmentListener.refreshCompleted();

                    }
                    UIUtils.hideLoader(activity);
                    fragmentListener.refreshCompleted();

                }, (VolleyError error) -> {

                    UIUtils.hideLoader(activity);
                    fragmentListener.refreshCompleted();
                    Utilis.exc("volley", error);
                });

            queue.add(request);

        }
    }


    private void updatePostAction(int id, String action) {
        Post post = posts.get(id);
        RequestQueue requestQueue = VolleySingelton.getInstance().getRequestQueue();
        JSONObject js = new JSONObject();
        try {
            js.put("action", action);
            post.flipAction(action);
            JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.PUT, Constants.SERVER_URL + "/post/"+post.uuid, js,
                response -> {

            }, error -> {
                post.flipAction(action);
                Toast.makeText(activity, "Something went Wrong", Toast.LENGTH_SHORT).show();
            });
            requestQueue.add(request);

        } catch (JSONException e) {
            Utilis.exc("json", e);
        }
    }

}
