package in.co.conflicto.conflictoapp.utilities;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shubhamagrawal on 04/04/17.
 */

public class JsonObjectRequestWithAuth extends JsonObjectRequest {
    public JsonObjectRequestWithAuth(int method, String url, JSONObject requestBody, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>();
        headers.putAll(super.getHeaders());

        // add headers <key,value>
        if(SessionData.currentUser == null){
            SessionData.currentUser = MyApplication.getInstance().retrieveCurrentUser();
        }
        String credentials = SessionData.currentUser.uuid + ":";
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
        return headers;
    }
}
