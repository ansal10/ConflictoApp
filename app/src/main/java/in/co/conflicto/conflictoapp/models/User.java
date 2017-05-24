package in.co.conflicto.conflictoapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.Utilis;

/**
 * Created by ansal on 5/19/17.
 */

public class User implements Serializable {
    public String name ;
    public String dpLink ;
    public String uuid;
    public String fcmToken;
    public String firebaseId;

    public User(JSONObject obj){
        try {
            this.name = obj.getString(Constants.NAME_KEY);
            this.dpLink = obj.getString(Constants.DP_LINK_KEY);
            this.uuid = obj.getString(Constants.UUID_KEY);
        } catch (JSONException e) {
            Utilis.exc("models", e);
        }
    }

    public User(String name, String dpLink, String uuid, String fcmToken, String firebaseId) {
        this.name = name;
        this.dpLink = dpLink;
        this.uuid = uuid;
        this.fcmToken = fcmToken;
        this.firebaseId = firebaseId;
    }
}
