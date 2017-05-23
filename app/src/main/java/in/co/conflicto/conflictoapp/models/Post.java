package in.co.conflicto.conflictoapp.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

            this.tags = new ObjectMapper().readValue(obj.getString("tags"), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
            this.reactions = new ObjectMapper().readValue(obj.getString("reactions"), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));


        } catch (JSONException e) {
            Utilis.exc("models", e);
        } catch (IOException e) {
            Utilis.exc("json", e);
        }
    }

    public void flipAction(String action){
        int count = 0;

        if (this.reactions.contains(action)){
            this.reactions.remove(action);
            count = -1;
        }else{
            this.reactions.add(action);
            count = 1;
        }
        switch (action) {
            case "LIKE":
                this.likes += count;
                break;
            case "DISLIKE":
                this.dislikes += count;
                break;
            case "ENDORSE":
                this.endorse += count;
                break;
            case "REPORT":
                this.reports += count;
        }
    }

    public void addComment(String comment, String type) {
        if(type.equals("SUPPORT"))
            this.supports ++;
        else if(type.equals("CONFLICT"))
            this.conflicts ++;
    }

    public void removeComment(String comment, String type) {
        if(type.equals("SUPPORT"))
            this.supports --;
        else if(type.equals("CONFLICT"))
            this.conflicts --;
    }

    public Post(String title, String description, String uuid) {
        this.title = title;
        this.description = description;
        this.uuid = uuid;
    }
}
