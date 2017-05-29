package in.co.conflicto.conflictoapp.adapters;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.activities.PostDetailsActivity;
import in.co.conflicto.conflictoapp.fragments.interfaces.CommentsActionListener;
import in.co.conflicto.conflictoapp.fragments.interfaces.PostFragmentListener;
import in.co.conflicto.conflictoapp.models.Comment;
import in.co.conflicto.conflictoapp.utilities.Constants;
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


    public static final int SORT_RECENT_FIRST = 1;
    public static final int SORT_POPULAR_FIRST = 2;
    public static final int ALL_COMMENTS = 3;
    public static final int CONFLICTS_COMMENTS = 4;
    public static final int SUPPORTS_COMMENTS = 5;
    public static final int OWN_COMMENTS = 6;
    private final List<Comment> comments;
    private final List<Comment> commentsBckup;
    private final PostDetailsActivity activity;
    private final String postUUID;
    private final CommentsActionListener commentsActionListener;
    private boolean allCommentsLoaded;
    private boolean hasComments;
    private int page;

    public CommentItemRecyclerViewAdapter(PostDetailsActivity activity, String postUUID, CommentsActionListener commentsActionListener){
        comments = new LinkedList<>();
        commentsBckup = new LinkedList<>();
        this.activity = activity;
        this.postUUID = postUUID;
        this.allCommentsLoaded = false;
        this.page = 0;
        this.hasComments = false;
        this.commentsActionListener = commentsActionListener;
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
        if (this.hasComments){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_comment_layout, parent, false);
            return new NoCommentViewHolder(v);
        }else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_item, parent, false);
            return new ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Log.i("comment_position", position+"");

        if (!this.hasComments) {


            Comment comment = comments.get(position);
            holder.mTimestampView.setText(comment.getTimeElasped());
            holder.mProfileNameView.setText(comment.user.name);
            holder.mCommentView.setText(comment.getComment());
            holder.mLikeView.setText(comment.likes + " Like");
            holder.mDislikeView.setText(comment.disLikes + " Dislike");
            holder.mEndorseView.setText(comment.endorse + " Endorse");

            holder.mLikeView.setOnClickListener(v -> this.click(holder.mLikeView, Constants.LIKE_ACTION_KEY, position));
            holder.mDislikeView.setOnClickListener(v -> this.click(holder.mLikeView, Constants.DISLIKE_ACTION_KEY, position));
            holder.mEndorseView.setOnClickListener(v -> this.click(holder.mLikeView, Constants.ENDORSE_ACTION_KEY, position));
            holder.mExpandView.setOnClickListener(v -> this.click(holder.mLikeView, "EXPAND", position));


            // set background
            if (comment.isConflict())
                holder.mConstraintLayout.setBackgroundResource(R.drawable.conflict_bg_gradient);
            else if (comment.isSupport())
                holder.mConstraintLayout.setBackgroundResource(R.drawable.support_bg_gradient);

            // set edit button visiblity
            if (SessionData.currentUser.uuid.equals(comment.user.uuid)) {
                holder.mEditView.setVisibility(View.VISIBLE);
                holder.mEditView.setOnClickListener(v -> this.click(holder.mLikeView, "EDIT", position));
            } else
                holder.mEditView.setVisibility(View.GONE);

            //set expand button
            if (comment.comment.length() > Constants.UNEXPANDED_LENGTH){
                holder.mExpandView.setVisibility(View.VISIBLE);
                holder.mExpandView.setOnClickListener(v->{
                    comment.flipExpand();
                    this.notifyItemChanged(position);
                });
            }else{
                holder.mExpandView.setVisibility(View.GONE);
            }


            // set like dislikes color
            if (comment.reactions.contains(Constants.LIKE_ACTION_KEY))
                holder.mLikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextSelected));
            else
                holder.mLikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextDefault));

            if (comment.reactions.contains(Constants.DISLIKE_ACTION_KEY))
                holder.mDislikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextSelected));
            else
                holder.mDislikeView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextDefault));

            if (comment.reactions.contains(Constants.ENDORSE_ACTION_KEY))
                holder.mEndorseView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextSelected));
            else
                holder.mEndorseView.setTextColor(activity.getResources().getColor(R.color.commentLikeTextDefault));


            Picasso.with(MyApplication.getInstance().getBaseContext()).
                    load(comment.user.dpLink).into(holder.mProfileImageView);
        }


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void loadComments(){
        if (!this.allCommentsLoaded) {
            RequestQueue queue = VolleySingelton.getInstance().getRequestQueue();
            commentsActionListener.refreshStarted();
            JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.GET, Constants.SERVER_URL + "/comment/"+postUUID, null,
                (JSONObject response) -> {
                    try {
                        JSONArray arr = response.getJSONArray("results");
                        for (int i = 0; i < arr.length(); i++) {
                            comments.add(new Comment(arr.optJSONObject(i)));
                        }
                        if (this.page == 0 && arr.length() == 0){
                            this.hasComments = true;
                            this.comments.add(null);
                        }
                        this.notifyDataSetChanged();
                        this.page++;
                        if (arr.length() < 25) {
                            this.allCommentsLoaded = true;
                        }
                        commentsActionListener.refreshCompleted();


                    } catch (JSONException e) {
                        Utilis.exc("volley", e);
                        commentsActionListener.refreshCompleted();

                    }
                    UIUtils.hideLoader(activity);
                    commentsActionListener.refreshCompleted();

                }, (VolleyError error) -> {

                UIUtils.hideLoader(activity);
                commentsActionListener.refreshCompleted();
                Utilis.exc("volley", error);
            });

            queue.add(request);

        }
    }


    public void click(View v, String action, int position){
        if(action.equals(Constants.LIKE_ACTION_KEY) || action.equals(Constants.DISLIKE_ACTION_KEY) || action.equals(Constants.ENDORSE_ACTION_KEY)){
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
                Toast.makeText(activity, Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
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

    class ViewHolder extends RecyclerView.ViewHolder {

        final View mView;
        final TextView mCommentView;
        final TextView mExpandView;
        final TextView mProfileNameView;
        final TextView mTimestampView;
        final ImageView mProfileImageView;
        final ConstraintLayout mConstraintLayout;
        final TextView mEditView;
        final TextView mLikeView;
        final TextView mDislikeView;
        final TextView mEndorseView;

        ViewHolder(View itemView) {
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

    private class NoCommentViewHolder extends ViewHolder {
        NoCommentViewHolder(View v){
            super(v);
        }
    }

    public void sortBy(int field){
        class CommentComparator implements Comparator<Comment> {
            @Override
            public int compare(Comment a, Comment b) {
                if (field == SORT_RECENT_FIRST)
                    return (b.id - a.id);
                else if (field == SORT_POPULAR_FIRST)
                    return  (b.likes + b.endorse + b.disLikes) - (a.likes + a.disLikes + a.endorse);
                return 0;
            }
        }
        Collections.sort(comments, new CommentComparator());
        this.notifyDataSetChanged();

    }

    public void filterBy(int field){
        if (commentsBckup.size() == 0) {
            commentsBckup.clear();
            for (Comment c : comments) {
                commentsBckup.add(c);
            }
        }
        comments.clear();
        for (Comment c:commentsBckup){
            if (field == CONFLICTS_COMMENTS) {
                if (c.type.equals(Constants.COMMENT_CONFLICT_TYPE))
                    comments.add(c);
            }
            else if (field == SUPPORTS_COMMENTS) {
                if (c.type.equals(Constants.COMMENT_SUPPORT_TYPE))
                    comments.add(c);
            }
            else if (field == OWN_COMMENTS) {
                if (c.user.uuid.equals(SessionData.currentUser.uuid))
                    comments.add(c);
            }
            else if (field == ALL_COMMENTS){
                comments.add(c);
            }
        }
        if (field == ALL_COMMENTS){
            commentsBckup.clear();
        }
        this.notifyDataSetChanged();
    }


}
