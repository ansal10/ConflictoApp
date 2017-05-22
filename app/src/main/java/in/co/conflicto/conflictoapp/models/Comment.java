package in.co.conflicto.conflictoapp.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import in.co.conflicto.conflictoapp.utilities.Utilis;

/**
 * Created by ansal on 5/22/17.
 */

public class Comment {
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
            comment = obj.getString("comment");
            likes = obj.getInt("likes");
            disLikes = obj.getInt("dislikes");
            reports = obj.getInt("reports");
            endorse = obj.getInt("endorse");
            postUUID = obj.getString("post_uuid");
            reactions = new ObjectMapper().readValue(obj.getString("reactions"), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
            type = obj.getString("type");
            uuid = obj.getString("uuid");
            user = new User(obj.getJSONObject("user").getJSONObject("fbprofile"));

        } catch (JSONException e) {
            Utilis.exc("json", e);
        }catch (IOException e){
            Utilis.exc("json", e);
        }
    }

    public boolean isConflict(){
        return type.equals("CONFLICT");
    }

    public boolean isSupport(){
        return type.equals("SUPPORT");
    }

}
