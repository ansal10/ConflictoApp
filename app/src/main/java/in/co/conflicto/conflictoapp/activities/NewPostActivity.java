package in.co.conflicto.conflictoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.models.Post;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.JsonObjectRequestWithAuth;
import in.co.conflicto.conflictoapp.utilities.UIUtils;
import in.co.conflicto.conflictoapp.utilities.Utilis;
import in.co.conflicto.conflictoapp.utilities.VolleySingelton;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private View view;
    private TextView submitButton;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Post post;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view = findViewById(R.id.new_post_activity_root_view_id);
        submitButton = (TextView) view.findViewById(R.id.post_submit_button_id);
        titleEditText = (EditText) view.findViewById(R.id.new_post_title_id);
        descriptionEditText = (EditText) view.findViewById(R.id.new_post_description_id);
        submitButton.setOnClickListener(this);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String post_uuid = intent.getStringExtra("post_uuid");
        if(title!=null && description!=null && post_uuid!=null){
            post = new Post(title, description, post_uuid);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == submitButton.getId()){
            uploadNewPost();
        }
    }

    private void uploadNewPost() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        if (title.length()< 10 || title.length()>250){
            Toast.makeText(this, "Title should be minimum 10 and maximum 250 of length", Toast.LENGTH_SHORT).show();
            return;
        }
        if (description.length() < 20){
            Toast.makeText(this, "Description should be minimum 20 length", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject js = new JSONObject();
        try {
            js.put("title", title);
            js.put("description", description);
            String url = Constants.SERVER_URL + "/post";
            int method = Request.Method.POST;
            if(post!=null) {
                url = url + "/" + post.uuid;
                method = Request.Method.PUT;
            }
            UIUtils.showLoader(this);
            JsonObjectRequest request = new JsonObjectRequestWithAuth(method, url, js,
                response -> {
                    UIUtils.hideLoader(this);
                    Toast.makeText(this, "Post Successfully submitted ", Toast.LENGTH_SHORT).show();
                    this.onBackPressed();
                }, error -> {
                UIUtils.hideLoader(this);
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            });
            VolleySingelton.getInstance().getRequestQueue().add(request);
        } catch (JSONException e) {
            Utilis.exc("json", e);
        }
    }
}
