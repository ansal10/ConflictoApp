package in.co.conflicto.conflictoapp.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.Utilis;

/**
 * Created by ansal on 5/14/17.
 */

public class Post implements Serializable {
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
    public Boolean expanded;
    public Integer id;
    public DateTime createdOn;

    public User user;

    public Post(JSONObject obj){

        try {
            expanded = false;
            this.title = obj.getString(Constants.TITLE_KEY);
            this.description = obj.getString(Constants.DESCRIPTION_KEY);
            this.category = obj.getString(Constants.CATEGORY_KEY);
            this.sharedPost = obj.getBoolean(Constants.SHARED_POST_KEY);
            this.uuid = obj.getString(Constants.UUID_KEY);
            this.tags = new ArrayList<>();
            this.likes = obj.getInt(Constants.LIKES_KEY);
            this.dislikes = obj.getInt(Constants.DISLIKES_KEY);
            this.endorse = obj.getInt(Constants.ENDORSE_KEY);
            this.supports = obj.getInt(Constants.SUPPORTS_KEY);
            this.conflicts = obj.getInt(Constants.CONFLICTS_KEY);
            this.reports = obj.getInt(Constants.REPORTS_KEY);
            this.reactions = new ArrayList<>();
            this.user = new User(obj.getJSONObject(Constants.USER_KEY).getJSONObject(Constants.FBPROFILE_KEY));

            this.tags = new ObjectMapper().readValue(obj.getString(Constants.TAGS_KEY), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
            this.reactions = new ObjectMapper().readValue(obj.getString(Constants.REACTIONS_KEY), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
            this.id = obj.getInt("id");
            createdOn = new DateTime(obj.getString("created_on"));


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
            case Constants.LIKE_ACTION_KEY:
                this.likes += count;
                break;
            case Constants.DISLIKE_ACTION_KEY:
                this.dislikes += count;
                break;
            case Constants.ENDORSE_ACTION_KEY:
                this.endorse += count;
                break;
            case Constants.REPORT_ACTION_KEY:
                this.reports += count;
        }
    }

    public void addComment(String comment, String type) {
        if(type.equals(Constants.COMMENT_SUPPORT_TYPE))
            this.supports ++;
        else if(type.equals(Constants.COMMENT_CONFLICT_TYPE))
            this.conflicts ++;
    }

    public void removeComment(String comment, String type) {
        if(type.equals(Constants.COMMENT_SUPPORT_TYPE))
            this.supports --;
        else if(type.equals(Constants.COMMENT_CONFLICT_TYPE))
            this.conflicts --;
    }

    public Post(String title, String description, String uuid) {
        this.title = title;
        this.description = description;
        this.uuid = uuid;
    }

    public void flipExpand(){
        this.expanded = !this.expanded;
    }

    public String getDescription() {
        if (expanded || description.length() <= Constants.UNEXPANDED_LENGTH)
            return description;
        else return description.substring(0,Constants.UNEXPANDED_LENGTH) + ".....";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeElasped() {
        long timeDiffMillis = DateTime.now().getMillis() - createdOn.getMillis();
        int mins = (int) (timeDiffMillis/60000);
        if (mins <= 59)
            return "posted "+mins+" minutes ago";
        int hours = (mins/60);
        if (hours < 24)
            return "posted "+hours+ " hours ago";
        int days = hours/24;
        if (days < 7)
            return "posted "+days+" days ago";

        return "posted on "+ createdOn.toString("dd MMM yyyy");

    }
}
