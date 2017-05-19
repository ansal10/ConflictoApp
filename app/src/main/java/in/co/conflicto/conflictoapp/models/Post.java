package in.co.conflicto.conflictoapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import in.co.conflicto.conflictoapp.fragments.dummy.DummyContent;
import in.co.conflicto.conflictoapp.utilities.Utilis;

/**
 * Created by ansal on 5/14/17.
 */

public class Post {
    public String title ;
    public String description ;
    public String category ;
    public Boolean sharedPost ;
    public String uuid ;
    public List<String> tags ;
    public Integer likes ;
    public Integer dislikes ;
    public Integer endorse ;
    public Integer supports ;
    public Integer conflicts ;
    public Integer reports ;
    public List<String> reactions;
    public User user;

    public Post(JSONObject obj){

        try {

            this.title = obj.getString("title");
            this.description = obj.getString("description");
            this.category = obj.getString("category");
            this.sharedPost = obj.getBoolean("shared_post");
            this.uuid = obj.getString("uuid");
            this.tags = new ArrayList<>();
            this.likes = obj.getInt("likes");
            this.dislikes = obj.getInt("dislikes");
            this.endorse = obj.getInt("endorse");
            this.supports = obj.getInt("supports");
            this.conflicts = obj.getInt("conflicts");
            this.reports = obj.getInt("reports");
            this.reactions = new ArrayList<>();
            this.user = new User(obj.getJSONObject("user").getJSONObject("fbprofile"));

            for (int i = 0 ; i < obj.getJSONArray("tags").length(); i++)
                this.tags.add(obj.getJSONArray("tags").optString(i));

            for (int i = 0 ; i < obj.getJSONArray("reactions").length(); i++)
                this.reactions.add(obj.getJSONArray("tags").optString(i));

        } catch (JSONException e) {
            Utilis.exc("models", e);
        }
    }

}
