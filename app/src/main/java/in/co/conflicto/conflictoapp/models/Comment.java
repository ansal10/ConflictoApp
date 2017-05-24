package in.co.conflicto.conflictoapp.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.Utilis;

/**
 * Created by ansal on 5/22/17.
 */

public class Comment implements Serializable {
    public String comment;
    public Integer likes;
    public Integer disLikes;
    public Integer reports;
    public Integer endorse;
    public String postUUID;
    public List<String> reactions;
    public String type;
    public String uuid;
    public User user;

    public Comment(JSONObject obj){
        try {
            comment = obj.getString(Constants.COMMENT_KEY);
            likes = obj.getInt(Constants.LIKES_KEY);
            disLikes = obj.getInt(Constants.DISLIKES_KEY);
            reports = obj.getInt(Constants.REPORTS_KEY);
            endorse = obj.getInt(Constants.ENDORSE_KEY);
            postUUID = obj.getString(Constants.POST_UUID_KEY);
            reactions = new ObjectMapper().readValue(obj.getString(Constants.REACTIONS_KEY), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
            type = obj.getString(Constants.TYPE_KEY);
            uuid = obj.getString(Constants.UUID_KEY);
            user = new User(obj.getJSONObject(Constants.USER_KEY).getJSONObject(Constants.FBPROFILE_KEY));

        } catch (JSONException e) {
            Utilis.exc("json", e);
        }catch (IOException e){
            Utilis.exc("json", e);
        }
    }

    public boolean isConflict(){
        return type.equals(Constants.COMMENT_CONFLICT_TYPE);
    }

    public boolean isSupport(){
        return type.equals(Constants.COMMENT_SUPPORT_TYPE);
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
                this.disLikes += count;
                break;
            case Constants.ENDORSE_ACTION_KEY:
                this.endorse += count;
                break;
            case Constants.REPORT_ACTION_KEY:
                this.reports += count;
        }
    }


}
