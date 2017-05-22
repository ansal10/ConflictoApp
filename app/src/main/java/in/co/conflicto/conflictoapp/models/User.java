package in.co.conflicto.conflictoapp.models;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.conflicto.conflictoapp.utilities.Utilis;

/**
 * Created by ansal on 5/19/17.
 */

public class User {
    public String name ;
    public String dpLink ;
    public String uuid;
    public String fcmToken;
    public String firebaseId;

    public User(JSONObject obj){
        try {
            this.name = obj.getString("name");
            this.dpLink = obj.getString("dp_link");
        } catch (JSONException e) {
            Utilis.exc("models", e);
        }
    }
}
