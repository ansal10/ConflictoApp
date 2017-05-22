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
    List<Comment> comments;


    public static CommentDialogFragment newInstance(String postUUID, List<Comment> comments) {
        CommentDialogFragment fragment = new CommentDialogFragment();
        fragment.postUUID = postUUID;
        fragment.comments = comments;
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

        return view;

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == conflictButton.getId())
            postComment("CONFLICT");
        else if(v.getId() == supportButton.getId())
            postComment("SUPPORT");

    }

    public void postComment(String type){
        JSONObject js = new JSONObject();
        try {
            js.put("type", type);
            js.put("post_uuid", postUUID );
            js.put("comment", commentBox.getText().toString());

            if(this.post != null){
                this.post.addComment(commentBox.getText().toString(), type);
            }
            JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.POST, Constants.SERVER_URL + "/comment",  js,
                res -> {

                }, error -> {
                Toast.makeText(MyApplication.getInstance(), "Something went wrong", Toast.LENGTH_SHORT).show();
                if(this.post != null){
                    this.post.removeComment(commentBox.getText().toString(), type);
                }
            });

            VolleySingelton.getInstance().getRequestQueue().add(request);
        } catch (JSONException e) {
            Utilis.exc("json", e);
        }
    }
}
