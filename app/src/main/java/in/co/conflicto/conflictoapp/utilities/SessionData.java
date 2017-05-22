package in.co.conflicto.conflictoapp.utilities;

import java.util.HashMap;
import java.util.Map;

import in.co.conflicto.conflictoapp.models.User;

/**
 * Created by ansal on 5/19/17.
 */

public class SessionData {
    public static final Map<String, Object> map = new HashMap<>();
    public static User currentUser;

    public static void setString(String key, String val){
        map.put(key, val);
    }

    public static String getString(String key, String defaultValue){
        if(map.containsKey(key))
            return (String) map.get(key);
        else
            return defaultValue;
    }
}
