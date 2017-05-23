package in.co.conflicto.conflictoapp.adapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.activities.PostDetailsActivity;
import in.co.conflicto.conflictoapp.fragments.interfaces.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Comment;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.DownloadImageTask;
import in.co.conflicto.conflictoapp.utilities.JsonObjectRequestWithAuth;
import in.co.conflicto.conflictoapp.utilities.MyApplication;
import in.co.conflicto.conflictoapp.utilities.SessionData;
import in.co.conflicto.conflictoapp.utilities.UIUtils;
import in.co.conflicto.conflictoapp.utilities.Utilis;
import in.co.conflicto.conflictoapp.utilities.VolleySingelton;

/**
 * Created by ansal on 5/22/17.
 */

public class CommentItemRecyclerViewAdapter extends RecyclerView.Adapter<CommentItemRecyclerViewAdapter.ViewHolder> {


    private final List<Comment> comments;
    private final PostDetailsActivity activity;
    private final String postUUID;
    private final PostFragmentListener fragmentListener;
    private boolean allCommentsLoaded;
    private int page;

    public CommentItemRecyclerViewAdapter(PostDetailsActivity activity, String postUUID, PostFragmentListener fragmentListener){
        comments = new LinkedList<>();
        this.activity = activity;
        this.postUUID = postUUID;
        this.allCommentsLoaded = false;
        this.page = 0;
        this.fragmentListener = fragmentListener;
        this.loadComments();
    }

    public void refresh() {
        this.allCommentsLoaded = false;
        this.page = 0;
        this.comments.clear();
        loadComments();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Comment comment = comments.get(position);
        holder.mTimestampView.setText("5 hours ago");
        holder.mProfileNameView.setText(comment.user.name);
        holder.mCommentView.setText(comment.comment);
        holder.mLikeView.setText(comment.likes+" Like");
        holder.mDislikeView.setText(comment.disLikes+" Dislike");
        holder.mEndorseView.setText(comment.endorse+" Endorse");

        holder.mLikeView.setOnClickListener(v -> this.click(holder.mLikeView, "LIKE", position));
        holder.mDislikeView.setOnClickListener(v -> this.click(holder.mLikeView, "DISLIKE", position));
        holder.mEndorseView.setOnClickListener(v -> this.click(holder.mLikeView, "ENDORSE", position));
        holder.mExpandView.setOnClickListener(v -> this.click(holder.mLikeView, "EXPAND", position));


        // set background
        if(comment.isConflict())
            holder.mConstraintLayout.setBackgroundResource(R.color.conflictCommentBG);
        else if (comment.isSupport())
            holder.mConstraintLayout.setBackgroundResource(R.color.supportCommentBG);

        // set edit button visiblity
        if(SessionData.currentUser.uuid.equals(comment.user.uuid)) {
            holder.mEditView.setVisibility(View.VISIBLE);
            holder.mEditView.setOnClickListener(v -> this.click(holder.mLikeView, "EDIT", position));
        }
        else
            holder.mEditView.setVisibility(View.GONE);

        // set like dislikes color
        if(comment.reactions.contains("LIKE"))
            holder.mLikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextSelected));
        else holder.mLikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextDefault));

        if(comment.reactions.contains("DISLIKE"))
            holder.mDislikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextSelected));
        else holder.mDislikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextDefault));

        if(comment.reactions.contains("ENDORSE"))
            holder.mEndorseView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextSelected));
        else holder.mEndorseView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextDefault));



        Picasso.with(MyApplication.getInstance().getBaseContext()).
                load(comment.user.dpLink).into(holder.mProfileImageView);


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void loadComments(){
        if (!this.allCommentsLoaded) {
            RequestQueue queue = VolleySingelton.getInstance().getRequestQueue();
            fragmentListener.refreshStarted();
            JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.GET, Constants.SERVER_URL + "/comment/"+postUUID, null,
                (JSONObject response) -> {
                    try {
                        JSONArray arr = response.getJSONArray("results");
                        for (int i = 0; i < arr.length(); i++) {
                            comments.add(new Comment(arr.optJSONObject(i)));
                        }
                        this.notifyDataSetChanged();
                        this.page++;
                        if (arr.length() < 25) {
                            this.allCommentsLoaded = true;
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



    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mCommentView;
        public final TextView mExpandView;
        public final TextView mProfileNameView;
        public final TextView mTimestampView;
        public final ImageView mProfileImageView;
        public final ConstraintLayout mConstraintLayout;
        public final TextView mEditView;
        public final TextView mLikeView;
        public final TextView mDislikeView;
        public final TextView mEndorseView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mCommentView = (TextView) itemView.findViewById(R.id.comment_id);
            mExpandView = (TextView) itemView.findViewById(R.id.expand_id);
            mProfileImageView = (ImageView) itemView.findViewById(R.id.dp_id);
            mProfileNameView = (TextView) itemView.findViewById(R.id.profile_name_id);
            mTimestampView = (TextView) itemView.findViewById(R.id.post_timestamp_id);
            mConstraintLayout = (ConstraintLayout) itemView.findViewById(R.id.comment_constrained_layout_id);
            mEditView = (TextView) itemView.findViewById(R.id.edit_id);
            mLikeView = (TextView) itemView.findViewById(R.id.like_id);
            mDislikeView = (TextView) itemView.findViewById(R.id.dislike_id);
            mEndorseView = (TextView) itemView.findViewById(R.id.endorse_id);
        }
    }

    public void click(View v, String action, int position){
        if(action.equals("LIKE") || action.equals("DISLIKE") || action.equals("ENDORSE")){
            updateCommentAction(position, action);
        }
        else if(action.equals("EDIT")){
            activity.popupCommentEdit(comments.get(position));
        }
    }

    private void updateCommentAction(int id, String action) {
        Comment comment = comments.get(id);
        RequestQueue requestQueue = VolleySingelton.getInstance().getRequestQueue();
        JSONObject js = new JSONObject();
        try {
            js.put("action", action);
            comment.flipAction(action);
            this.notifyItemChanged(id);
            JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.PUT, Constants.SERVER_URL + "/comment/"+comment.uuid, js,
                    response -> {

                    }, error -> {
                comment.flipAction(action);
                this.notifyItemChanged(id);
                Toast.makeText(activity, "Something went Wrong", Toast.LENGTH_SHORT).show();
            });
            requestQueue.add(request);

        } catch (JSONException e) {
            Utilis.exc("json", e);
        }
    }

    public void commentUpdated(Comment comment) {
        for (int i = 0 ; i < comments.size() ; i++){
            if(comments.get(i).uuid.equals(comment.uuid)){
                comments.set(i, comment);
                this.notifyItemChanged(i);
                break;
            }
        }
    }

    public void commentDeleted(Comment comment) {
        for (int i = 0 ; i < comments.size() ; i++){
            if(comments.get(i).uuid.equals(comment.uuid)) {
                comments.remove(i);
                this.notifyDataSetChanged();
                break;
            }
        }

    }

    public interface CommentAdapterListener{
        void popupCommentEdit(Comment comment);
    }
}
