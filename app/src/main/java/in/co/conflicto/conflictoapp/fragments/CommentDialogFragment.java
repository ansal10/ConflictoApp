package in.co.conflicto.conflictoapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.models.Comment;
import in.co.conflicto.conflictoapp.models.Post;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.JsonObjectRequestWithAuth;
import in.co.conflicto.conflictoapp.utilities.MyApplication;
import in.co.conflicto.conflictoapp.utilities.UIUtils;
import in.co.conflicto.conflictoapp.utilities.Utilis;
import in.co.conflicto.conflictoapp.utilities.VolleySingelton;


public class CommentDialogFragment extends DialogFragment implements View.OnClickListener {


    private Button conflictButton;
    private Button supportButton;
    private EditText commentBox;
    String postUUID;
    Post post;
    Comment comment;


    public static CommentDialogFragment newInstance(String postUUID, Comment comment) {
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.postUUID = postUUID;
        fragment.comment = comment;
        return fragment;
    }

    public static CommentDialogFragment newInstance(Post post){
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.postUUID = post.uuid;
        fragment.post = post;
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_dialog, container, false);
        conflictButton = (Button) view.findViewById(R.id.conflict_button_id);
        supportButton = (Button) view.findViewById(R.id.support_button_id);
        commentBox = (EditText) view.findViewById(R.id.comment_box_id);

        conflictButton.setOnClickListener(this);
        supportButton.setOnClickListener(this);

        if(comment != null){
            commentBox.setText(comment.comment);
            conflictButton.setText("DELETE");
            supportButton.setText("UPDATE");
        }



        return view;

    }


    @Override
    public void onClick(View v) {
        if(comment==null) {
            if (v.getId() == conflictButton.getId())
                postComment("CONFLICT");
            else if (v.getId() == supportButton.getId())
                postComment("SUPPORT");
        }else if (comment!=null){
            if(v.getId() == conflictButton.getId())
                deleteComment(comment);
            else if(v.getId() == supportButton.getId())
                updateComment(comment);
        }

    }

    public void postComment(String type){
        JSONObject js = new JSONObject();
        try {
            String comment = commentBox.getText().toString();
            if (comment.length() < 10){
                Toast.makeText(MyApplication.getInstance(), "Minimum length for comment is 10", Toast.LENGTH_SHORT).show();
                return;
            }
            js.put("type", type);
            js.put("post_uuid", postUUID );
            js.put("comment", comment );

//            if(this.post != null){
//                this.post.addComment(commentBox.getText().toString(), type);
//            }
            JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.POST, Constants.SERVER_URL + "/comment",  js,
                res -> {
                    dismiss();
                    Toast.makeText(MyApplication.getInstance(), "Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                }, error -> {
                Toast.makeText(MyApplication.getInstance(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                if(this.post != null){
//                    this.post.removeComment(commentBox.getText().toString(), type);
//                }
            });

            VolleySingelton.getInstance().getRequestQueue().add(request);
        } catch (JSONException e) {
            Utilis.exc("json", e);
        }
    }

    public void deleteComment(Comment comment){
        JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.DELETE, Constants.SERVER_URL+ "/comment/" + comment.uuid , null,
            response -> {
                dismiss();
                Toast.makeText(MyApplication.getInstance(), "Comment Deleted Successfully", Toast.LENGTH_SHORT).show();

            }, error -> {
                Toast.makeText(MyApplication.getInstance(), "Something went wrong", Toast.LENGTH_SHORT).show();
        });
        VolleySingelton.getInstance().getRequestQueue().add(request);
    }

    public void updateComment(Comment comment){
        JSONObject js = new JSONObject();
        try {
            js.put("comment", commentBox.getText().toString() );
        } catch (JSONException e) {
            Utilis.exc("json", e);
        }
        JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.PUT, Constants.SERVER_URL+ "/comment/" + comment.uuid , js,
                response -> {
                    dismiss();
                    Toast.makeText(MyApplication.getInstance(), "Comment Updated Successfully", Toast.LENGTH_SHORT).show();
                }, error -> Toast.makeText(MyApplication.getInstance(), "Something went wrong", Toast.LENGTH_SHORT).show());
        VolleySingelton.getInstance().getRequestQueue().add(request);

    }
}
